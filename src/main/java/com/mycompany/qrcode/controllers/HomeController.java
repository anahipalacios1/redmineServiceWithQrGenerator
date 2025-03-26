package com.mycompany.qrcode.controllers;

import com.mycompany.qrcode.response.IssuesResponse;
import com.mycompany.qrcode.services.RedmineService;
import com.mycompany.qrcode.config.RedmineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final RedmineService redmineService;
    private final RedmineConfig redmineConfig; // Inyectamos RedmineConfig

    @Autowired
    public HomeController(RedmineService redmineService, RedmineConfig redmineConfig) {
        this.redmineService = redmineService;
        this.redmineConfig = redmineConfig; // Inyectamos RedmineConfig
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            IssuesResponse issuesResponse = redmineService.getIssues();
            if (issuesResponse != null && issuesResponse.getIssues() != null) {
                model.addAttribute("issues", issuesResponse.getIssues());
                model.addAttribute("urlQr", redmineConfig.getUrlQr());
                model.addAttribute("key", redmineConfig.getKey());
            } else {
                model.addAttribute("error", "No se encontraron issues.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al obtener issues: " + e.getMessage());
        }
        return "listado";
    }
}
