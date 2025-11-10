package com.library.Library.Management.System.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.Library.Management.System.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
