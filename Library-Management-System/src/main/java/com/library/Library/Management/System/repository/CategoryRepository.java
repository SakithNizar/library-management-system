package com.library.Library.Management.System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.Library.Management.System.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}
