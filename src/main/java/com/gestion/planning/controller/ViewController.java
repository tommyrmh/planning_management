package com.gestion.planning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile/view";
    }

    @GetMapping("/profile/edit")
    public String editProfile() {
        return "profile/edit";
    }

    @GetMapping("/projects")
    public String projects() {
        return "projects/list";
    }

    @GetMapping("/projects/new")
    public String newProject() {
        return "projects/create";
    }

    @GetMapping("/projects/{id}")
    public String projectDetail() {
        return "projects/detail";
    }

    @GetMapping("/projects/{id}/edit")
    public String editProject() {
        return "projects/edit";
    }
}
