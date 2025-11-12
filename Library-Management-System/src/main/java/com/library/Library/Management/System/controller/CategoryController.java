package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Category;
import com.library.Library.Management.System.repository.CategoryRepository;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ✅ GET all
    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        List<Category> list = categoryRepository.findAll();
        return new ApiResponse<>("Fetched all categories successfully!", list);
    }

    // ✅ POST create
    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category saved = categoryRepository.save(category);
        return new ApiResponse<>("Category created successfully!", saved);
    }

    // ✅ PUT update
    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(cat -> {
                    cat.setName(updatedCategory.getName());
                    Category saved = categoryRepository.save(cat);
                    return new ApiResponse<>("Category updated successfully!", saved);
                })
                .orElseGet(() -> new ApiResponse<>("Category not found!", null));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return new ApiResponse<>("Category deleted successfully!", "Deleted ID: " + id);
    }
}
