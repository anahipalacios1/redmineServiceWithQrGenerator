package com.mycompany.qrcode.controllers;

import com.mycompany.qrcode.response.IssuesResponse;
import com.mycompany.qrcode.services.RedmineService;
import com.mycompany.qrcode.beans.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class IssueController {

    private final RedmineService redmineService;

    @Autowired
    public IssueController(RedmineService redmineService) {
        this.redmineService = redmineService;
    }

    @GetMapping("/issue")
    public String getIssueById(@RequestParam("id") int id, Model model) {
        try {
            IssuesResponse issuesResponse = redmineService.getIssues();
            Issue issue = issuesResponse.getIssues().stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found"));
            model.addAttribute("issue", issue);
            return "seleccionar_id";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al obtener los datos del issue: " + e.getMessage());
            return "error";
        }
    }
}
