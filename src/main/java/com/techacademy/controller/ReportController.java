package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail user, Model model) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getAuthority()));
        String code = user.getUsername();

        if(isAdmin) {
        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());
        } else {
            model.addAttribute("listSize", reportService.findByName(code).size());
            model.addAttribute("reportList", reportService.findByName(code));
        }

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report) {

        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@AuthenticationPrincipal UserDetail user, @Validated Report report, BindingResult res, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            return create(report);
        }

        Employee employee = user.getEmployee();
        ErrorKinds result = reportService.save(employee,report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report);
            }

        return "redirect:/reports";
    }
/*
    @GetMapping(value = "/{code}/update")
    public String edit(@PathVariable("code") String code, Model model) {
        model.addAttribute("employee",employeeService.findByCode(code));
        return "employees/update";
    }

    @PostMapping(value = "/{code}/update")
    public String update(@PathVariable("code") String code, @Validated Employee employee, BindingResult res, Model model) {

        if (res.hasErrors()) {
            return "/employees/update";
        }

        ErrorKinds result = employeeService.rewrite(code,employee);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return "/employees/update";
        }

        return "redirect:/employees";
    }
*/
    // 日報削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id,Model model) {

        reportService.delete(id);

        return "redirect:/reports";
    }

}
