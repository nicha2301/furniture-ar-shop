package com.nicha.furnier.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.nicha.furnier.AppBaseActivity
import com.nicha.furnier.R
import com.nicha.furnier.adapter.ColorAdapter
import com.nicha.furnier.utils.ColorUtils
import com.nicha.furnier.utils.Constants
import com.nicha.furnier.utils.Constants.ControlPara.COLOR_ANIMATION_DURATION_MS
import com.nicha.furnier.utils.Constants.ControlPara.GESTURE_OVERLAY_DURATION_MS
import com.nicha.furnier.utils.Constants.ControlPara.MAX_SCALE
import com.nicha.furnier.utils.Constants.ControlPara.MIN_SCALE
import com.nicha.furnier.utils.Constants.ControlPara.SCAN_CHECK_INTERVAL_MS
import com.nicha.furnier.utils.Constants.ControlPara.ZOOM_OVERLAY_DURATION_MS
import com.nicha.furnier.utils.Constants.KeyIntent.PRODUCT_ID
import com.nicha.furnier.utils.Constants.KeyIntent.MODEL_URL
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.utils.Color as SceneViewColor

class ARViewActivity : AppBaseActivity() {

    private enum class OverlayStep(val message: Int, val animationRes: Int) {
        SCANNING(R.string.step_scanning, R.raw.ar_coaching_overlay),
        ROTATE(R.string.step_rotate, R.raw.anim_rotate_gesture),
        ZOOM(R.string.step_zoom, R.raw.anim_zoom_gesture)
    }

    private sealed class ARError(val message: String) {
        object NotTracking : ARError("")
        data class Generic(val error: String) : ARError(error)
    }

    private lateinit var arSceneView: ArSceneView
    private lateinit var placeButton: AppCompatImageButton
    private lateinit var closeButton: AppCompatImageButton
    private lateinit var btnColorPicker: AppCompatImageButton
    private lateinit var colorRecyclerView: RecyclerView
    private lateinit var overlayContainer: ConstraintLayout
    private lateinit var overlayAnimation: LottieAnimationView
    private lateinit var overlayText: TextView
    private lateinit var modelNode: ArModelNode

    private var isModelPlaced = false
    private var hasRotatedOrScaled = false
    private var isOverlayVisible = false
    private var isScanningComplete = false
    private var originalColor: FloatArray? = null
    private var currentOverlayStep: OverlayStep? = null

    private val handler = Handler(Looper.getMainLooper())
    private val scanCheckRunnable = object : Runnable {
        override fun run() {
            checkScanStatus()
            handler.postDelayed(this, SCAN_CHECK_INTERVAL_MS)
        }
    }

    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_view)
        initializeViews()
        setupGestureDetectors()
        showOverlay(OverlayStep.SCANNING)
        startScanCheck()
    }

    // Khởi tạo views và listeners
    private fun initializeViews() {
        arSceneView = findViewById(R.id.arSceneView)
        placeButton = findViewById(R.id.place)
        closeButton = findViewById(R.id.fabClose)
        btnColorPicker = findViewById(R.id.btnColorPicker)
        colorRecyclerView = findViewById(R.id.colorRecyclerView)
        overlayContainer = findViewById(R.id.overlayContainer)
        overlayAnimation = findViewById(R.id.overlayAnimation)
        overlayText = findViewById(R.id.overlayText)

        placeButton.setOnClickListener { if (!isModelPlaced && !isOverlayVisible) placeModel() }
        setupColorPicker()
        closeButton.setOnClickListener {
            finish()
        }
    }

    // Thiết lập cử chỉ xoay và phóng to/thu nhỏ
    private fun setupGestureDetectors() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float
            ): Boolean {
                if (isModelPlaced && modelNode.anchor != null && !isOverlayVisible) {
                    modelNode.rotation = Rotation(
                        modelNode.rotation.x,
                        modelNode.rotation.y + distanceX * 0.005f,
                        modelNode.rotation.z
                    )
                    showGestureOverlayIfNeeded()
                }
                return true
            }
        })

        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (isModelPlaced && modelNode.anchor != null && !isOverlayVisible) {
                    val scale = (modelNode.scale.x * detector.scaleFactor).coerceIn(MIN_SCALE, MAX_SCALE)
                    modelNode.scale = Position(scale, scale, scale)
                    showGestureOverlayIfNeeded()
                }
                return true
            }
        })
    }

    // Thiết lập ColorPicker và RecyclerView
    private fun setupColorPicker() {
        btnColorPicker.setOnClickListener { toggleColorPickerVisibility() }
        
        val colorList = ColorUtils.getARColorsFromResources(this)
        
        colorRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ARViewActivity, LinearLayoutManager.VERTICAL, false)
            adapter = ColorAdapter(colorList) { color ->
                if (color == getString(R.string.color_reset)) resetModelColor() else changeModelColor(color)
                hideColorPicker()
            }
        }
    }

    // Hiển thị hoặc ẩn ColorPicker với animation
    private fun toggleColorPickerVisibility() {
        if (colorRecyclerView.visibility == View.VISIBLE) {
            hideColorPicker()
        } else {
            colorRecyclerView.apply {
                alpha = 0f
                translationY = -50f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(COLOR_ANIMATION_DURATION_MS)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun hideColorPicker() {
        colorRecyclerView.animate()
            .alpha(0f)
            .translationY(50f)
            .setDuration(COLOR_ANIMATION_DURATION_MS)
            .withEndAction { colorRecyclerView.visibility = View.GONE }
            .start()
    }

    // Thay đổi màu mô hình AR
    private fun changeModelColor(colorHex: String) {
        if (!isModelPlaced || modelNode.anchor == null) return
        try {
            val color = Color.parseColor(colorHex)
            val r = Color.red(color) / 255f
            val g = Color.green(color) / 255f
            val b = Color.blue(color) / 255f
            if (originalColor == null) originalColor = floatArrayOf(r, g, b, 1.0f)
            applyColorToModel(r, g, b, 1.0f)
        } catch (e: IllegalArgumentException) {
            showError(ARError.Generic(getString(R.string.error_invalid_color)))
        }
    }

    // Đặt lại màu gốc của mô hình
    private fun resetModelColor() {
        if (isModelPlaced && modelNode.anchor != null && originalColor != null) {
            applyColorToModel(originalColor!![0], originalColor!![1], originalColor!![2], originalColor!![3])
        }
    }

    private fun applyColorToModel(r: Float, g: Float, b: Float, a: Float) {
        modelNode.modelInstance?.materialInstances?.forEach { material ->
            material.setBaseColor(SceneViewColor(r, g, b, a))
        }
    }

    // Thiết lập node mô hình AR
    private fun setupModelNode() {
        val modelUrl = intent.getStringExtra(MODEL_URL).toString()

        modelNode = ArModelNode(placementMode = PlacementMode.PLANE_HORIZONTAL).apply {
            loadModelGlbAsync(
                glbFileLocation = modelUrl,
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f),
                onError = { error ->
                    runOnUiThread {
                        Snackbar.make(arSceneView, getString(R.string.error_load_model, error.message), Snackbar.LENGTH_LONG).show()
                    }
                }
            ) {
                modelInstance?.materialInstances?.firstOrNull()?.let { material ->
                    originalColor = floatArrayOf(1f, 1f, 1f, 1f)
                }
            }
            onAnchorChanged = { anchor ->
                placeButton.visibility = if (anchor != null) View.GONE else View.VISIBLE
                btnColorPicker.visibility = if (anchor != null) View.VISIBLE else View.GONE
            }
        }
        arSceneView.addChild(modelNode)
    }

    // Kiểm tra trạng thái quét mặt phẳng
    private fun checkScanStatus() {
        arSceneView.arSession?.takeIf { it.isTrackingPlane }?.let { session ->
            val hasDetectedPlane = session.getAllTrackables(Plane::class.java).any { plane ->
                plane.trackingState == TrackingState.TRACKING &&
                        plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING
            }
            if (hasDetectedPlane && !isScanningComplete) {
                isScanningComplete = true
                setupModelNode()
                placeButton.visibility = View.VISIBLE
                hideOverlay()
                stopScanCheck()
            }
        }
    }

    // Đặt mô hình lên mặt phẳng
    private fun placeModel() {
        try {
            if (modelNode.anchor == null && arSceneView.arSession?.isTrackingPlane == true) {
                modelNode.anchor()
                arSceneView.planeRenderer.isVisible = false
                isModelPlaced = true
                hideOverlay()
                showOverlay(OverlayStep.ROTATE, GESTURE_OVERLAY_DURATION_MS) { showOverlay(OverlayStep.ZOOM, ZOOM_OVERLAY_DURATION_MS) }
            } else {
                showError(ARError.Generic(getString(R.string.error_camera_slow)))
            }
        } catch (e: Exception) {
            showError(when (e) {
                is com.google.ar.core.exceptions.NotTrackingException -> ARError.NotTracking
                else -> ARError.Generic(e.message ?: "Unknown error")
            })
        }
    }

    // Hiển thị overlay hướng dẫn
    private fun showOverlay(step: OverlayStep, duration: Long? = null, nextAction: (() -> Unit)? = null) {
        if (currentOverlayStep == step) return
        currentOverlayStep = step
        overlayText.setText(step.message)
        overlayAnimation.setAnimation(step.animationRes)
        overlayContainer.visibility = View.VISIBLE
        isOverlayVisible = true
        duration?.let {
            overlayContainer.postDelayed({
                hideOverlay()
                nextAction?.invoke()
            }, it)
        }
    }

    private fun hideOverlay() {
        overlayContainer.visibility = View.GONE
        isOverlayVisible = false
        currentOverlayStep = null
    }

    private fun showGestureOverlayIfNeeded() {
        if (!hasRotatedOrScaled) {
            hasRotatedOrScaled = true
            showOverlay(OverlayStep.ROTATE, GESTURE_OVERLAY_DURATION_MS) { showOverlay(OverlayStep.ZOOM, ZOOM_OVERLAY_DURATION_MS) }
        }
    }

    // Xử lý lỗi AR
    private fun showError(error: ARError) {
        val message = when (error) {
            is ARError.NotTracking -> getString(R.string.error_not_tracking)
            is ARError.Generic -> error.message
        }
        Snackbar.make(arSceneView, message, Snackbar.LENGTH_SHORT).show()
    }

    // Xử lý sự kiện chạm
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || isOverlayVisible) return super.onTouchEvent(event)
        try {
            if (isModelPlaced && modelNode.anchor != null && arSceneView.arSession?.isTrackingPlane == true) {
                gestureDetector.onTouchEvent(event)
                scaleGestureDetector.onTouchEvent(event)
            }
        } catch (e: Exception) {
            showError(ARError.NotTracking)
        }
        return super.onTouchEvent(event)
    }

    // Quản lý lifecycle
    override fun onResume() {
        super.onResume()
        try {
            arSceneView.onResume(this)
            startScanCheck()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        try {
            stopScanCheck()
            arSceneView.onPause(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onPause()
    }

    override fun onDestroy() {
        try {
            stopScanCheck()
            arSceneView.onDestroy(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun startScanCheck() {
        handler.post(scanCheckRunnable)
    }

    private fun stopScanCheck() {
        handler.removeCallbacks(scanCheckRunnable)
    }
}