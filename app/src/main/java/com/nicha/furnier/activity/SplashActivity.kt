package com.nicha.furnier.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.nicha.furnier.AppBaseActivity
import com.nicha.furnier.R
import android.view.WindowManager
import com.nicha.furnier.utils.Constants
import com.nicha.furnier.utils.Constants.SharedPref.MODE
import com.nicha.furnier.utils.extensions.*
import kotlinx.android.synthetic.main.activity_splash_new.rlMain

class SplashActivity : AppBaseActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_new)

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if(!getSharedPrefInstance().getBooleanValue(Constants.SharedPref.SHOW_SWIPE)){
            launchActivity<WalkThroughActivity>()
            finish()
        }
        rlMain.changeBackgroundColor()
        getSharedPrefInstance().removeKey(MODE)
        getSharedPrefInstance().setValue(MODE, 2)

        if(isLoggedIn()){
            if(isNetworkAvailable()){
                getRestApiImpl().TokenAPI(onApiSuccess = {
                    runDelayed(1000) {
                        launchActivity<DashBoardActivity>()
                        finish()
                    }
                }, onApiError = {
                    doLogin()
                })
            }
        }else{
            if(getSharedPrefInstance().getBooleanValue(Constants.SharedPref.SHOW_SWIPE)){
                launchActivityWithNewTask<DashBoardActivity>()
                finish()
            }
        }

    }

    private fun doLogin() {
        signIn(
            getEmail(),
            getPassword(),
            onApiSuccess = {
                if (it.billing.first_name.isEmpty()) {
                    launchActivity<EditProfileActivity> { }
                } else {
                    launchActivityWithNewTask<DashBoardActivity>()
                }
                finish()
            },
            onError = {
                snackBarError(it)
            })
    }


}