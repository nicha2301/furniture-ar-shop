package com.nicha.furnier.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nicha.furnier.AppBaseActivity
import com.nicha.furnier.R
import com.nicha.furnier.adapter.FurnitureCategoryAdapter
import com.nicha.furnier.adapter.FurnitureItemAdapter
import com.nicha.furnier.models.FurnitureCategory
import com.nicha.furnier.models.FurnitureItem
import com.nicha.furnier.models.StoreProductModel
import com.nicha.furnier.models.Category
import com.nicha.furnier.utils.Constants
import com.nicha.furnier.utils.extensions.onClick
import com.nicha.furnier.utils.extensions.snackBar
import com.nicha.furnier.utils.extensions.launchActivity
import com.nicha.furnier.utils.extensions.isNetworkAvailable
import com.nicha.furnier.utils.extensions.getRestApiImpl

class InteriorDesignActivity : AppBaseActivity() {

    private lateinit var categoryAdapter: FurnitureCategoryAdapter
    private lateinit var furnitureAdapter: FurnitureItemAdapter
    
    private val selectedItems = mutableMapOf<String, FurnitureItem>()
    private var currentCategory: FurnitureCategory? = null
    private val categoryMap = mutableMapOf<String, FurnitureCategory>()
    private val productsByCategory = mutableMapOf<String, List<FurnitureItem>>()
    private val allProducts = mutableListOf<StoreProductModel>()
    
    private lateinit var toolbar: Toolbar
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvFurniture: RecyclerView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var tvSelectedCount: TextView
    private lateinit var btnViewDesign: Button
    private lateinit var furnitureContainer: View
    private lateinit var categoriesContainer: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interior_design)
        
        initViews()
        setupToolbar()
        setupRecyclerViews()
        loadAllCategoriesAndProducts()
        setupClickListeners()
    }
    
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        rvCategories = findViewById(R.id.rvCategories)
        rvFurniture = findViewById(R.id.rvFurniture)
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
        tvSelectedCount = findViewById(R.id.tvSelectedCount)
        btnViewDesign = findViewById(R.id.btnViewDesign)
        furnitureContainer = findViewById(R.id.furnitureContainer)
        categoriesContainer = findViewById(R.id.categoriesContainer)
        tvSelectedCount.text = getString(R.string.selected_items_count, 0)
    }
    
    private fun setupToolbar() {
        setToolbar(toolbar)
        mAppBarColor()
        title = getString(R.string.title_interior_design)
    }
    
    private fun setupRecyclerViews() {
        setupCategoryRecyclerView()
        setupFurnitureRecyclerView()
    }
    
    private fun setupClickListeners() {
        btnViewDesign.onClick {
            if (selectedItems.isEmpty()) {
                snackBar(getString(R.string.error_no_furniture_selected))
                return@onClick
            }
            
            val itemsWithoutModels = selectedItems.values.filter { it.modelUrl.isEmpty() }
            if (itemsWithoutModels.isNotEmpty()) {
                snackBar(getString(R.string.warning_items_without_models, itemsWithoutModels.size))
            }
            
            launchActivity<VirtualRoomActivity> {
                putParcelableArrayListExtra(Constants.KeyIntent.SELECTED_FURNITURE, ArrayList(selectedItems.values))
            }
        }
    }
    
    private fun setupCategoryRecyclerView() {
        categoryAdapter = FurnitureCategoryAdapter { category ->
            onCategorySelected(category)
        }
        
        rvCategories.apply {
            layoutManager = LinearLayoutManager(this@InteriorDesignActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }
    
    // Xử lý khi danh mục được chọn
    private fun onCategorySelected(category: FurnitureCategory) {
        currentCategory = category
        loadFurnitureItems(category)
        tvCategoryTitle.text = category.name
        furnitureContainer.visibility = View.VISIBLE
    }
    
    private fun setupFurnitureRecyclerView() {
        furnitureAdapter = FurnitureItemAdapter(
            onItemSelected = { item -> onFurnitureItemSelected(item) },
            getSelectedItemId = { getSelectedItemIdForCurrentCategory() }
        )
        
        rvFurniture.apply {
            layoutManager = GridLayoutManager(this@InteriorDesignActivity, 2)
            adapter = furnitureAdapter
        }
    }
    
    // Xử lý khi một mẫu nội thất được chọn
    private fun onFurnitureItemSelected(item: FurnitureItem) {
        // Add or replace selected item for current category
        currentCategory?.let { category ->
            selectedItems[category.id] = item
            updateButtonState()
        }
    }
    
    // Lấy ID của mẫu nội thất đã chọn cho danh mục hiện tại
    private fun getSelectedItemIdForCurrentCategory(): String {
        return currentCategory?.let { category ->
            selectedItems[category.id]?.id
        } ?: ""
    }
    
    // Tải tất cả danh mục và sản phẩm nội thất từ API
    private fun loadAllCategoriesAndProducts() {
        if (!isNetworkAvailable()) {
            snackBar(getString(R.string.error_no_internet))
            return
        }
        
        showProgress(true)
        
        val productParams = createProductApiParams()
        
        fetchAllProducts(productParams)
    }
    
    // Tạo tham số cho API lấy sản phẩm
    private fun createProductApiParams(): MutableMap<String, Int> {
        val params: MutableMap<String, Int> = HashMap()
        params["page"] = 1
        params["per_page"] = 100
        return params
    }
    
    // Gọi API để lấy danh sách sản phẩm
    private fun fetchAllProducts(params: MutableMap<String, Int>) {
        getRestApiImpl().listAllProducts(params, 
            onApiSuccess = { products ->
                handleProductsResponse(products)
            }, 
            onApiError = { errorMsg ->
                handleApiError(errorMsg)
            }
        )
    }
    
    // Xử lý phản hồi từ API sản phẩm
    private fun handleProductsResponse(products: List<StoreProductModel>) {
        allProducts.clear()
        allProducts.addAll(products)
        
        val categoryParams: MutableMap<String, Int> = HashMap()
        fetchAllCategories(categoryParams)
    }
    
    // Gọi API để lấy danh sách danh mục
    private fun fetchAllCategories(params: MutableMap<String, Int>) {
        getRestApiImpl().listAllCategory(params,
            onApiSuccess = { categories ->
                showProgress(false)
                processAllCategories(categories)
            }, 
            onApiError = { errorMsg ->
                handleApiError(errorMsg)
            }
        )
    }
    
    private fun handleApiError(errorMsg: String) {
        showProgress(false)
        snackBar(errorMsg)
    }
    
    // Xử lý dữ liệu danh mục từ API
    private fun processAllCategories(categories: ArrayList<Category>) {
        val allFurnitureCategories = mutableListOf<FurnitureCategory>()
        
        categories.forEach { category ->
            val furnitureCategory = createFurnitureCategory(category)
            
            val furnitureItems = getCategoryProducts(category)
            
            allFurnitureCategories.add(furnitureCategory)
            categoryMap[category.id.toString()] = furnitureCategory
            productsByCategory[category.id.toString()] = furnitureItems
        }
        
        updateCategoriesUI(allFurnitureCategories)
    }
    
    // Tạo mô hình FurnitureCategory từ dữ liệu API
    private fun createFurnitureCategory(category: Category): FurnitureCategory {
        return FurnitureCategory(
            category.id.toString(),
            category.name ?: "Unknown",
            category.image?.src ?: ""
        )
    }
    
    // Lọc và lấy sản phẩm thuộc một danh mục
    private fun getCategoryProducts(category: Category): List<FurnitureItem> {
        val categoryProducts = allProducts.filter { product ->
            product.categories?.any { cat -> cat.id == category.id } == true &&
            !product.modelUrl.isNullOrEmpty()
        }
        
        return categoryProducts.map { product ->
            FurnitureItem(
                product.id.toString(),
                product.name ?: "Unknown",
                product.images?.firstOrNull()?.src ?: "",
                category.id.toString(),
                category.name ?: "Unknown",
                product.modelUrl ?: ""
            )
        }
    }
    
    // Cập nhật giao diện với các danh mục đã tải
    private fun updateCategoriesUI(categories: List<FurnitureCategory>) {
        if (categories.isNotEmpty()) {
            categoryAdapter.setData(categories)
            
            // Show the categories container
            if (::categoriesContainer.isInitialized) {
                categoriesContainer.visibility = View.VISIBLE
            }
            
            currentCategory = categories[0]
            tvCategoryTitle.text = categories[0].name
            loadFurnitureItems(categories[0])
        } else {
            snackBar(getString(R.string.error_no_categories))
        }
    }
    
    // Tải các mẫu nội thất cho danh mục đã chọn
    private fun loadFurnitureItems(category: FurnitureCategory) {
        val items = productsByCategory[category.id] ?: emptyList()
        furnitureAdapter.setData(items)
        
        if (items.isEmpty()) {
            snackBar(getString(R.string.error_no_products_in_category, category.name))
        }
    }
    
    // Cập nhật trạng thái nút xem thiết kế
    private fun updateButtonState() {
        btnViewDesign.isEnabled = selectedItems.isNotEmpty()
        btnViewDesign.setBackgroundColor(resources.getColor(R.color.colorPrimary, theme))
        tvSelectedCount.text = getString(R.string.selected_items_count, selectedItems.size)
    }
} 