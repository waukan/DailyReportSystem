package com.techacademy.repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, String> {
    public List<Report> findAllByEmployeeCode(String code);
    public Report findByReportDateAndEmployeeCode(LocalDate date, String Code);
    public Optional<Report> findById(Integer id);
    public List<Report> findByTitleContainingOrContentContaining(String title, String content);
}
