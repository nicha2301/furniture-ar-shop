package com.nicha.furnier.activity

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
import com.nicha.furnier.adapter.SelectedFurnitureAdapter
import com.nicha.furnier.models.FurnitureItem
import com.nicha.furnier.utils.Constants
import com.nicha.furnier.utils.extensions.onClick
import io.github.sceneview.SceneView
import io.github.sceneview.environment.loadEnvironment
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

class VirtualRoomActivity : AppBaseActivity() {
    private lateinit var sceneView: SceneView
    private lateinit var loadingView: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
    private lateinit var selectedFurnitureAdapter: SelectedFurnitureAdapter
    
    private val selectedFurniture = mutableListOf<FurnitureItem>()
    private val furnitureNodes = mutableMapOf<String, ModelNode>()
    private var isLoading = false
    
    companion object {
        private val FURNITURE_POSITIONS = mapOf(
            "Tables" to Position(3f, 0f, -2f),
            "Chairs" to Position(3f, 0.2f, -3f),
            "Decor" to Position(0.25f, 0.8f, 1.4f),
            "Beds" to Position(3.8f, 3f, -3.5f),
            "Storage" to Position(2.8f, 3f, -3.5f),
            "Sofas" to Position(0f, 0f, 0f)
        )
        
        private val VALID_CATEGORIES = setOf("Tables", "Chairs", "Decor", "Beds", "Storage", "Sofas")
    }
    
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
            Toast.makeText(this, getString(R.string.error_init_3d_view, e.localizedMessage), Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun initViews() {
        sceneView = findViewById(R.id.sceneView)
        loadingView = findViewById(R.id.loadingView)
    }
    
    private fun setupSceneView() {
        with(sceneView) {
            lifecycleScope.launch {
                environment = HDRLoader.loadEnvironment(
                    context = this@VirtualRoomActivity,
                    hdrFileLocation = "environments/studio_small_09_4k.hdr",
                    specularFilter = true,
                    createSkybox = true
                )
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
    
    private fun setupBottomSheet() {
        val bottomSheet = findViewById<MaterialCardView>(R.id.bottomSheet)
        val drawerIndicator = findViewById<ImageView>(R.id.drawerIndicator)
        
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                drawerIndicator.rotation = if (newState == BottomSheetBehavior.STATE_EXPANDED) 180f else 0f
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                drawerIndicator.rotation = slideOffset * 180f
            }
        })

        drawerIndicator.onClick {
            bottomSheetBehavior.state = if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) 
                BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
        }
    }
    
    private fun setupFurnitureList() {
        val rvSelectedFurniture = findViewById<RecyclerView>(R.id.rvSelectedFurniture)
        selectedFurnitureAdapter = SelectedFurnitureAdapter(
            onItemFocused = { item -> focusOnFurniture(item.id) }
        )

        rvSelectedFurniture.apply {
            layoutManager = LinearLayoutManager(this@VirtualRoomActivity)
            adapter = selectedFurnitureAdapter
        }
    }
    
    private fun loadFurnitureData() {
        intent.getParcelableArrayListExtra<FurnitureItem>(Constants.KeyIntent.SELECTED_FURNITURE)?.let { items ->
            selectedFurniture.addAll(items)
            selectedFurnitureAdapter.setData(selectedFurniture)
        }
    }

    private fun loadRoom() {
        try {
            isLoading = true
            loadingView.visibility = View.VISIBLE
            
            val roomNode = ModelNode().apply {
                loadModelGlbAsync(
                    glbFileLocation = "environments/room.glb",
                    scaleToUnits = 10.0f,
                    onError = { 
                        handleError(getString(R.string.error_loading_room))
                    },
                    onLoaded = { 
                        Toast.makeText(
                            this@VirtualRoomActivity, 
                            getString(R.string.room_loaded_loading_furniture),
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        setupInitialCameraView()
                        loadFurnitureModels()
                    }
                )
            }
            sceneView.addChild(roomNode)
        } catch (e: Exception) {
            handleError(getString(R.string.error_loading_room_model, e.localizedMessage))
        }
    }
    
    private fun setupInitialCameraView() {
        try {
            sceneView.cameraNode.position = Position(11f, 4f, -1f)
            sceneView.cameraNode.rotation = Rotation(160f, 89f, 180.0f)
        } catch (e: Exception) {
            Log.e(null, "Error setting initial camera view: ${e.localizedMessage}")
        }
    }

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
                            Toast.makeText(this@VirtualRoomActivity, 
                                getString(R.string.error_loading_furniture, item.name), 
                                Toast.LENGTH_SHORT
                            ).show()
                            incrementLoadCounter(++loadedCount, uniqueFurniture.size)
                        },
                        onLoaded = { 
                            position = FURNITURE_POSITIONS[item.categoryName] ?: Position(0f, 0f, 0f)
                            furnitureNodes[item.id] = this
                            incrementLoadCounter(++loadedCount, uniqueFurniture.size)
                        }
                    )
                }
                sceneView.addChild(modelNode)
            }
        } catch (e: Exception) {
            handleError(getString(R.string.error_loading_furniture_models, e.localizedMessage))
        }
    }

    private fun incrementLoadCounter(loadedCount: Int, totalCount: Int) {
        if (loadedCount >= totalCount) {
            completeLoading()
        }
    }
    
    private fun completeLoading() {
        loadingView.visibility = View.GONE
        isLoading = false
    }
    
    private fun handleError(message: String) {
        loadingView.visibility = View.GONE
        isLoading = false
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun focusOnFurniture(furnitureId: String) {
        try {
            val node = furnitureNodes[furnitureId] ?: return
            
            val targetPosition = node.position
            val cameraPosition = Position(
                targetPosition.x + 1.0f,
                targetPosition.y + 1.0f,
                targetPosition.z + 1.0f
            )
            
            sceneView.cameraNode.position = cameraPosition
            sceneView.cameraNode.lookAt(targetPosition)
            
            val furnitureName = selectedFurniture.find { it.id == furnitureId }?.name ?: ""
            Toast.makeText(this, getString(R.string.viewing_furniture, furnitureName), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(null, "Error focusing on furniture: ${e.localizedMessage}")
        }
    }
    
    override fun onDestroy() {
        furnitureNodes.forEach { (_, node) ->
            sceneView.removeChild(node)
        }
        furnitureNodes.clear()
        
        if (::sceneView.isInitialized) {
            sceneView.destroy()
        }
        
        super.onDestroy()
    }
} 