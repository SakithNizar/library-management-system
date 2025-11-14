package com.library.Library.Management.System.controller;

import com.library.Library.Management.System.model.Category;
import com.library.Library.Management.System.repository.CategoryRepository;
import com.library.Library.Management.System.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // -----------------------------------------------
    // ✅ GET all categories (USER or LIBRARIAN)
    // -----------------------------------------------
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_LIBRARIAN')")
    public ApiResponse<List<Category>> getAllCategories() {
        List<Category> list = categoryRepository.findAll();
        return new ApiResponse<>("✅ Fetched all categories successfully!", list);
    }

    // -----------------------------------------------
    // ✅ POST create category (LIBRARIAN only)
    // -----------------------------------------------
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category saved = categoryRepository.save(category);
        return new ApiResponse<>("✅ Category created successfully!", saved);
    }

    // -----------------------------------------------
    // ✅ PUT update category (LIBRARIAN only)
    // -----------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<Category> updateCategory(@PathVariable Long id,
                                                @RequestBody Category updatedCategory) {

        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedCategory.getName());
                    Category saved = categoryRepository.save(existing);
                    return new ApiResponse<>("✅ Category updated successfully!", saved);
                })
                .orElseGet(() -> new ApiResponse<>("❌ Category not found!", null));
    }

    // -----------------------------------------------
    // ✅ DELETE a category (LIBRARIAN only)
    // -----------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {

        if (!categoryRepository.existsById(id)) {
            return new ApiResponse<>("❌ Category not found!", "ID: " + id);
        }

        categoryRepository.deleteById(id);
        return new ApiResponse<>("🗑️ Category deleted successfully!", "Deleted ID: " + id);
    }
}
