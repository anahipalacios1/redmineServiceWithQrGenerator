package com.mycompany.qrcode.controllers;

import com.mycompany.qrcode.response.IssuesResponse;
import com.mycompany.qrcode.services.RedmineService;
import com.mycompany.qrcode.beans.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            return "seleccionar_id";  // Retorna la vista JSP "seleccionar_id"
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al obtener los datos del issue: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/export/pdf/{id}")
    public ResponseEntity<byte[]> exportPdfForIssue(@PathVariable("id") int id) {
        try {
            IssuesResponse issuesResponse = redmineService.getIssues();
            Issue issue = issuesResponse.getIssues().stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found"));
            byte[] pdfBytes = redmineService.exportReport(issue);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("issue_" + id + "_report.pdf")
                    .build());
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
