package com.nicha.furnier

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.gson.Gson
import com.nicha.furnier.models.DashBoardResponse
import com.nicha.furnier.network.RestApiImpl
import com.nicha.furnier.utils.Constants
import com.nicha.furnier.utils.Constants.ONE_SIGNAL_APP_ID
import com.nicha.furnier.utils.Constants.SharedPref.ACCENTCOLOR
import com.nicha.furnier.utils.Constants.SharedPref.APPURL
import com.nicha.furnier.utils.Constants.SharedPref.BACKGROUNDCOLOR
import com.nicha.furnier.utils.Constants.SharedPref.CONSUMERKEY
import com.nicha.furnier.utils.Constants.SharedPref.CONSUMERSECRET
import com.nicha.furnier.utils.Constants.SharedPref.DASHBOARDDATA
import com.nicha.furnier.utils.Constants.SharedPref.KEY_DASHBOARD
import com.nicha.furnier.utils.Constants.SharedPref.KEY_PRODUCT_DETAIL
import com.nicha.furnier.utils.Constants.SharedPref.PRIMARYCOLOR
import com.nicha.furnier.utils.Constants.SharedPref.PRIMARYCOLORDARK
import com.nicha.furnier.utils.Constants.SharedPref.TEXTPRIMARYCOLOR
import com.nicha.furnier.utils.Constants.SharedPref.TEXTSECONDARYCOLOR
import com.nicha.furnier.utils.Constants.SharedPref.THEME
import com.nicha.furnier.utils.LocaleManager
import com.nicha.furnier.utils.SharedPrefUtils
import com.nicha.furnier.utils.extensions.getSharedPrefInstance
import com.nicha.furnier.utils.extensions.mainContent
import com.onesignal.OneSignal
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class FurnierApp : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        appInstance = this
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.ERROR)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.clearOneSignalNotifications()
        OneSignal.setAppId(ONE_SIGNAL_APP_ID)

        getSharedPrefInstance().apply {
            appTheme = getIntValue(THEME, Constants.THEME.LIGHT)

        }
        // Set Custom Font
        ViewPump.init(
            ViewPump.builder().addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.font_regular))
                        .setFontAttrId(R.attr.fontPath).build()
                )
            ).build()
        )

    }

    fun enableNotification(isEnabled: Boolean) {
        OneSignal.disablePush(isEnabled)
    }

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var localeManager: LocaleManager
        private lateinit var appInstance: FurnierApp
        var sharedPrefUtils: SharedPrefUtils? = null
        var noInternetDialog: Dialog? = null
        var restApiImpl: RestApiImpl? = null
        lateinit var language: String
        var appTheme: Int = 0
        fun getAppInstance(): FurnierApp = appInstance
        lateinit var mAppData: DashBoardResponse
        var mModeFlag: Boolean = false

        fun changeAppTheme(isDark: Boolean) {
            mAppDataChanges()
            getSharedPrefInstance().apply {
                when {
                    isDark -> {
                        setValue(THEME, Constants.THEME.DARK)
                    }
                    else -> {
                        setValue(THEME, Constants.THEME.LIGHT)
                    }
                }
                appTheme = getIntValue(THEME, Constants.THEME.LIGHT)
            }
        }


        @SuppressLint("ResourceType")
        fun mAppDataChanges() {
            mModeFlag = appTheme == Constants.THEME.DARK
            appInstance.mainContent {
                mAppData = it
                getSharedPrefInstance().setValue(
                    DASHBOARDDATA,
                    Gson().toJson(it.dashboard!!)
                )
                if (it.dashboard.layout == "layout1") {
                    getSharedPrefInstance().removeKey(KEY_DASHBOARD)
                    getSharedPrefInstance().setValue(KEY_DASHBOARD, 0)
                } else {
                    getSharedPrefInstance().removeKey(KEY_DASHBOARD)
                    getSharedPrefInstance().setValue(KEY_DASHBOARD, 1)
                }

                if (it.Productdetailview!!.layout == "layout1") {
                    getSharedPrefInstance().removeKey(KEY_PRODUCT_DETAIL)
                    getSharedPrefInstance().setValue(KEY_PRODUCT_DETAIL, 0)
                } else {
                    getSharedPrefInstance().removeKey(KEY_PRODUCT_DETAIL)
                    getSharedPrefInstance().setValue(KEY_PRODUCT_DETAIL, 1)
                }

                when {
                    mAppData.appSetup!!.consumerKey!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(CONSUMERKEY)
                        getSharedPrefInstance().setValue(
                            CONSUMERKEY,
                            mAppData.appSetup!!.consumerKey
                        )
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(CONSUMERKEY)
                        getSharedPrefInstance().setValue(
                            CONSUMERKEY,
                            getAppInstance().getString(R.string.consumerKey)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.consumerSecret!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(CONSUMERSECRET)
                        getSharedPrefInstance().setValue(
                            CONSUMERSECRET,
                            mAppData.appSetup!!.consumerSecret
                        )
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(CONSUMERSECRET)
                        getSharedPrefInstance().setValue(
                            CONSUMERSECRET,
                            getAppInstance().getString(R.string.consumerSecret)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.primaryColor!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(PRIMARYCOLOR)
                        when {
                            mModeFlag -> {
                                getSharedPrefInstance().removeKey(PRIMARYCOLOR)
                                getSharedPrefInstance().setValue(PRIMARYCOLOR, "#1D1D1D")
                            }
                            else -> {
                                getSharedPrefInstance().setValue(
                                    PRIMARYCOLOR,
                                    mAppData.appSetup!!.primaryColor!!
                                )
                            }
                        }
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(PRIMARYCOLOR)
                        getSharedPrefInstance().setValue(
                            PRIMARYCOLOR,
                            appInstance.resources.getString(R.color.colorPrimary)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.primaryColor!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(PRIMARYCOLORDARK)
                        when {
                            mModeFlag -> {
                                getSharedPrefInstance().removeKey(PRIMARYCOLORDARK)
                                getSharedPrefInstance().setValue(PRIMARYCOLORDARK, "#FFFFFF")
                            }
                            else -> {
                                getSharedPrefInstance().setValue(
                                    PRIMARYCOLORDARK,
                                    mAppData.appSetup!!.primaryColor!!
                                )
                            }
                        }
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(PRIMARYCOLORDARK)
                        getSharedPrefInstance().setValue(
                            PRIMARYCOLORDARK,
                            appInstance.resources.getString(R.color.colorPrimary)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.secondaryColor!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(ACCENTCOLOR)
                        when {
                            mModeFlag -> {
                                getSharedPrefInstance().removeKey(ACCENTCOLOR)
                                getSharedPrefInstance().setValue(ACCENTCOLOR, "#757575")
                            }
                            else -> {
                                getSharedPrefInstance().setValue(
                                    ACCENTCOLOR,
                                    mAppData.appSetup!!.secondaryColor!!
                                )
                            }
                        }
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(ACCENTCOLOR)
                        getSharedPrefInstance().setValue(
                            ACCENTCOLOR,
                            appInstance.resources.getString(R.color.colorAccent)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.textPrimaryColor!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(TEXTPRIMARYCOLOR)
                        when {
                            mModeFlag -> {
                                getSharedPrefInstance().removeKey(TEXTPRIMARYCOLOR)
                                getSharedPrefInstance().setValue(TEXTPRIMARYCOLOR, "#FFFFFF")
                            }
                            else -> {
                                getSharedPrefInstance().setValue(
                                    TEXTPRIMARYCOLOR,
                                    mAppData.appSetup!!.textPrimaryColor!!
                                )
                            }
                        }
                    }
                    else -> {
                        getSharedPrefInstance().setValue(
                            TEXTPRIMARYCOLOR,
                            appInstance.resources.getString(R.color.textColorPrimary)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.textSecondaryColor!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(TEXTSECONDARYCOLOR)
                        when {
                            mModeFlag -> {
                                getSharedPrefInstance().setValue(TEXTSECONDARYCOLOR, "#757575")
                            }
                            else -> {
                                getSharedPrefInstance().setValue(
                                    TEXTSECONDARYCOLOR,
                                    mAppData.appSetup!!.textSecondaryColor!!
                                )
                            }
                        }
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(TEXTSECONDARYCOLOR)
                        getSharedPrefInstance().setValue(
                            TEXTSECONDARYCOLOR,
                            appInstance.resources.getString(R.color.textColorSecondary)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.backgroundColor!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(BACKGROUNDCOLOR)
                        when {
                            mModeFlag -> {
                                getSharedPrefInstance().setValue(BACKGROUNDCOLOR, "#121212")
                            }
                            else -> {
                                getSharedPrefInstance().setValue(
                                    BACKGROUNDCOLOR,
                                    mAppData.appSetup!!.backgroundColor!!
                                )
                            }
                        }
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(BACKGROUNDCOLOR)
                        getSharedPrefInstance().setValue(
                            BACKGROUNDCOLOR,
                            appInstance.resources.getString(R.color.colorScreenBackground)
                        )
                    }
                }

                when {
                    mAppData.appSetup!!.appUrl!!.isNotEmpty() -> {
                        getSharedPrefInstance().removeKey(APPURL)
                        getSharedPrefInstance().setValue(APPURL, mAppData.appSetup!!.appUrl!!)
                    }
                    else -> {
                        getSharedPrefInstance().removeKey(APPURL)
                        getSharedPrefInstance().setValue(
                            APPURL,
                            getAppInstance().getString(R.string.base_url)
                        )
                    }
                }

            }
        }

    }
}
