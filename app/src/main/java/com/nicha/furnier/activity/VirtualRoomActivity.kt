package com.nicha.furnier.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.filament.View.AntiAliasing
import com.google.android.filament.View.BloomOptions
import com.google.android.filament.View.Dithering
import com.google.android.filament.View.DynamicResolutionOptions
import com.google.android.filament.View.RenderQuality
import com.google.android.filament.utils.HDRLoader
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.nicha.furnier.AppBaseActivity
import com.nicha.furnier.R
import com.nicha.furnier.adapter.FurnitureControlsListener
import com.nicha.furnier.adapter.SelectedFurnitureAdapter
import com.nicha.furnier.models.FurnitureItem
import com.nicha.furnier.utils.Constants
import com.nicha.furnier.utils.Constants.ControlPara.COLOR_ANIMATION_DURATION_MS
import com.nicha.furnier.utils.Constants.ControlPara.DEFAULT_ALPHA
import com.nicha.furnier.utils.Constants.ControlPara.FURNITURE_POSITIONS
import com.nicha.furnier.utils.Constants.ControlPara.HIGHLIGHT_SCALE_FACTOR
import com.nicha.furnier.utils.Constants.ControlPara.ROTATION_DURATION_MS
import com.nicha.furnier.utils.Constants.ControlPara.SCALE_ANIMATION_DURATION_MS
import com.nicha.furnier.utils.Constants.ControlPara.VALID_CATEGORIES
import com.nicha.furnier.utils.extensions.onClick
import io.github.sceneview.SceneView
import io.github.sceneview.environment.loadEnvironment
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.github.sceneview.utils.Color as SceneViewColor

class VirtualRoomActivity : AppBaseActivity(), FurnitureControlsListener {
    private lateinit var sceneView: SceneView
    private lateinit var loadingView: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
    private lateinit var selectedFurnitureAdapter: SelectedFurnitureAdapter
    
    private val selectedFurniture = mutableListOf<FurnitureItem>()
    private val furnitureNodes = mutableMapOf<String, ModelNode>()
    private val originalColors = mutableMapOf<String, SceneViewColor>()
    private val originalRotations = mutableMapOf<String, Rotation>()
    private var isLoading = false
    private var currentSelectedModelId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_room)

        try {
            initViews()
            setupSceneView()
            setupBottomSheet()
            setupFurnitureList()
            loadFurnitureData()
            loadRoom()
        } catch (e: Exception) {
        }
    }
    
    // Khởi tạo các view cơ bản
    private fun initViews() {
        sceneView = findViewById(R.id.sceneView)
        loadingView = findViewById(R.id.loadingView)
    }
    
    // Cấu hình SceneView và môi trường 3D
    private fun setupSceneView() {
        with(sceneView) {
            lifecycleScope.launch {
                try {
                    loadHdrEnvironment()
                } catch (e: Exception) {
                }
            }
            
            renderQuality = RenderQuality().apply {
                hdrColorBuffer = com.google.android.filament.View.QualityLevel.MEDIUM
            }
            
            bloomOptions = BloomOptions().apply {
                enabled = false 
            }
            
            antiAliasing = AntiAliasing.NONE
            dynamicResolution = DynamicResolutionOptions().apply { enabled = false }
            dithering = Dithering.NONE
            isFrontFaceWindingInverted = false
        }
    }
    
    private suspend fun loadHdrEnvironment() = withContext(Dispatchers.IO) {
        sceneView.environment = HDRLoader.loadEnvironment(
            context = this@VirtualRoomActivity,
            hdrFileLocation = "environments/studio_small_09_4k.hdr",
            specularFilter = true,
            createSkybox = true
        )
    }
    
    // Thiết lập BottomSheet và các thành phần liên quan
    private fun setupBottomSheet() {
        val bottomSheet = findViewById<MaterialCardView>(R.id.bottomSheet)
        val drawerIndicator = findViewById<ImageView>(R.id.drawerIndicator)
        
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        setupBottomSheetCallbacks(drawerIndicator)

        drawerIndicator.onClick {
            toggleBottomSheet()
        }
    }
    
    private fun setupBottomSheetCallbacks(drawerIndicator: ImageView) {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                drawerIndicator.rotation = if (newState == BottomSheetBehavior.STATE_EXPANDED) 180f else 0f
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                drawerIndicator.rotation = slideOffset * 180f
            }
        })
    }
    
    private fun toggleBottomSheet() {
        val newState = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) 
            BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.state = newState
    }
    
    // Cấu hình danh sách nội thất đã chọn
    private fun setupFurnitureList() {
        val rvSelectedFurniture = findViewById<RecyclerView>(R.id.rvSelectedFurniture)
        selectedFurnitureAdapter = SelectedFurnitureAdapter(
            onItemFocused = { item -> 
                toggleControlsForModel(item)
            }
        )
        
        selectedFurnitureAdapter.setControlsListener(this)

        rvSelectedFurniture.apply {
            layoutManager = LinearLayoutManager(this@VirtualRoomActivity)
            adapter = selectedFurnitureAdapter
        }
    }
    
    // Tải dữ liệu nội thất từ intent
    private fun loadFurnitureData() {
        intent.getParcelableArrayListExtra<FurnitureItem>(Constants.KeyIntent.SELECTED_FURNITURE)?.let { items ->
            selectedFurniture.addAll(items)
            selectedFurnitureAdapter.setData(selectedFurniture)
        }
    }

    // Tải mô hình phòng 3D
    private fun loadRoom() {
        try {
            showLoading(true)
            
            val roomNode = ModelNode().apply {
                loadModelGlbAsync(
                    glbFileLocation = "environments/room.glb",
                    scaleToUnits = 10.0f,
                    onError = { 
                        handleError(getString(R.string.error_loading_room))
                    },
                    onLoaded = { 
                        showToast(getString(R.string.room_loaded_loading_furniture))
                        setupInitialCameraView()
                        loadFurnitureModels()
                    }
                )
            }
            sceneView.addChild(roomNode)
        } catch (e: Exception) {
        }
    }
    
    // Thiết lập vị trí camera ban đầu
    private fun setupInitialCameraView() {
        try {
            sceneView.cameraNode.position = Position(10f, 5f, 0f)
            sceneView.cameraNode.rotation = Rotation(160f, 90f, 180f)
        } catch (e: Exception) {
        }
    }

    // Tải các mô hình nội thất
    private fun loadFurnitureModels() {
        try {
            if (selectedFurniture.isEmpty()) {
                completeLoading()
                return
            }

            var loadedCount = 0
            
            val validFurniture = selectedFurniture.filter { it.categoryName in VALID_CATEGORIES }
            val uniqueFurniture = validFurniture.distinctBy { it.categoryName }
            
            uniqueFurniture.forEach { item ->
                val modelNode = ModelNode().apply {
                    loadModelGlbAsync(
                        glbFileLocation = item.modelUrl,
                        scaleToUnits = 1.0f,
                        centerOrigin = Position(0f, 0f, 0f),
                        onError = { 
                            showToast(getString(R.string.error_loading_furniture, item.name))
                            incrementLoadCounter(++loadedCount, uniqueFurniture.size)
                        },
                        onLoaded = { 
                            position = FURNITURE_POSITIONS[item.categoryName] ?: Position(0f, 0f, 0f)
                            originalRotations[item.id] = rotation
                            modelInstance?.materialInstances?.firstOrNull()?.let { material ->
                                originalColors[item.id] = SceneViewColor(1f, 1f, 1f, DEFAULT_ALPHA)
                            }
                            furnitureNodes[item.id] = this
                            incrementLoadCounter(++loadedCount, uniqueFurniture.size)
                        }
                    )
                }
                sceneView.addChild(modelNode)
            }
        } catch (e: Exception) {
        }
    }

    // Tăng bộ đếm tải và kiểm tra hoàn thành
    private fun incrementLoadCounter(loadedCount: Int, totalCount: Int) {
        if (loadedCount >= totalCount) {
            completeLoading()
        }
    }
    
    // Hoàn thành quá trình tải và ẩn loading view
    private fun completeLoading() {
        showLoading(false)
    }
    
    private fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) View.VISIBLE else View.GONE
        isLoading = show
    }
    
    // Xử lý lỗi và hiển thị thông báo
    private fun handleError(message: String) {
        showLoading(false)
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    // Bật/tắt điều khiển cho mô hình được chọn
    private fun toggleControlsForModel(item: FurnitureItem) {
        if (currentSelectedModelId == item.id) {
            selectedFurnitureAdapter.clearSelection()
            currentSelectedModelId = null
            return
        }
        
        currentSelectedModelId = item.id
        selectedFurnitureAdapter.setSelectedItem(item.id)
        
        // Hiệu ứng nhấp nháy để thấy rõ model đang chọn
        highlightModel(item.id)
    }
    
    private fun highlightModel(modelId: String) {
        furnitureNodes[modelId]?.animateScales(
            Scale(HIGHLIGHT_SCALE_FACTOR, HIGHLIGHT_SCALE_FACTOR, HIGHLIGHT_SCALE_FACTOR),
            Scale(1.0f, 1.0f, 1.0f)
        )?.apply { 
            duration = COLOR_ANIMATION_DURATION_MS 
        }
    }
    
    override fun onColorSelected(furnitureId: String, colorHex: String) {
        if (colorHex == "RESET") {
            resetModelColor(furnitureId)
        } else {
            changeModelColor(furnitureId, colorHex)
        }
    }
    
    override fun onRotationChanged(furnitureId: String, angle: Float) {
        rotateModel(furnitureId, angle)
    }
    
    override fun onResetClicked(furnitureId: String) {
        resetModelById(furnitureId)
    }
    
    // Thay đổi màu của mô hình
    private fun changeModelColor(furnitureId: String, colorHex: String) {
        val node = furnitureNodes[furnitureId] ?: return
        
        try {
            val color = Color.parseColor(colorHex)
            val r = Color.red(color) / 255f
            val g = Color.green(color) / 255f
            val b = Color.blue(color) / 255f
            
            applyColorToModel(node, SceneViewColor(r, g, b, DEFAULT_ALPHA))
            highlightModel(furnitureId)
        } catch (e: Exception) {
        }
    }
    
    // Đặt lại màu gốc cho mô hình
    private fun resetModelColor(furnitureId: String) {
        val node = furnitureNodes[furnitureId] ?: return
        val originalColor = originalColors[furnitureId] ?: return
        
        applyColorToModel(node, originalColor)
        highlightModel(furnitureId)
    }
    
    // Áp dụng màu cho mô hình
    private fun applyColorToModel(node: ModelNode, color: SceneViewColor) {
        node.modelInstance?.materialInstances?.forEach { material ->
            material.setBaseColor(color)
        }
    }
    
    // Xoay mô hình theo góc
    private fun rotateModel(furnitureId: String, angleDegrees: Float) {
        val node = furnitureNodes[furnitureId] ?: return
        val originalRotation = originalRotations[furnitureId] ?: Rotation(0f, 0f, 0f)
        
        val newRotation = Rotation(
            originalRotation.x,
            angleDegrees,
            originalRotation.z
        )
        
        node.rotation = newRotation

        node.animateScales(
            Scale(1.01f, 1.01f, 1.01f),
            Scale(1.0f, 1.0f, 1.0f)
        ).apply { 
            duration = 150L 
        }
    }
    
    // Đặt lại trạng thái ban đầu cho mô hình
    private fun resetModelById(furnitureId: String) {
        resetModelColor(furnitureId)
        
        val node = furnitureNodes[furnitureId] ?: return
        val originalRotation = originalRotations[furnitureId] ?: return
        
        node.animateScales(
            Scale(0.9f, 0.9f, 0.9f),
            Scale(1.1f, 1.1f, 1.1f),
            Scale(1.0f, 1.0f, 1.0f)
        ).apply {
            duration = SCALE_ANIMATION_DURATION_MS
        }
        
        node.animateRotations(originalRotation).apply {
            duration = ROTATION_DURATION_MS
            startDelay = 100L
        }
        
        showToast(getString(R.string.model_reset_success))
    }
    
    // Giải phóng tài nguyên khi đóng activity
    override fun onDestroy() {
        cleanupResources()
        super.onDestroy()
    }
    
    private fun cleanupResources() {
        furnitureNodes.forEach { (_, node) ->
            sceneView.removeChild(node)
        }
        furnitureNodes.clear()
        
        if (::sceneView.isInitialized) {
            sceneView.destroy()
        }
    }
    
    // Xử lý sự kiện nút back
    override fun onBackPressed() {
        if (currentSelectedModelId != null) {
            selectedFurnitureAdapter.clearSelection()
            currentSelectedModelId = null
        } else {
            super.onBackPressed()
        }
    }
} 