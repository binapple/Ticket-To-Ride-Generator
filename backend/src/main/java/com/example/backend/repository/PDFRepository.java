package com.example.backend.repository;

import com.example.backend.entitiy.PDF;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PDFRepository extends JpaRepository<PDF, Long> {
}
