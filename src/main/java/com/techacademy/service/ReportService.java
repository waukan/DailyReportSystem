package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Employee employee, Report report) {


        if(findByReportDate(report.getReportDate(),employee.getCode()) != null) {
                return ErrorKinds.DATECHECK_ERROR;
            }

        report.setEmployee(employee);

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    //日報更新
    @Transactional
    public ErrorKinds rewrite(Report report) {

        Integer id = report.getId();
        Employee employee =findById(id).getEmployee();
        report.setEmployee(employee);

        if(!(findById(id).getReportDate().equals(report.getReportDate()))) {
            if(findByReportDate(report.getReportDate(),report.getEmployee().getCode()) != null) {
                return ErrorKinds.DATECHECK_ERROR;
            }
        }

        LocalDateTime create =findById(id).getCreatedAt();
        LocalDateTime now = LocalDateTime.now();

        report.setCreatedAt(create);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
}


    // 日報削除
    @Transactional
    public void delete(Integer id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public List<Report> findByName(String code) {
        return reportRepository.findAllByEmployeeCode(code);

    }


    // 検索
    public Report findById(Integer id) {
        Optional<Report> option = reportRepository.findById(id);
        Report report = option.orElse(null);
        return report;
    }

    public Report findByReportDate(LocalDate date, String code) {
        return reportRepository.findByReportDateAndEmployeeCode(date,code);
    }

}
