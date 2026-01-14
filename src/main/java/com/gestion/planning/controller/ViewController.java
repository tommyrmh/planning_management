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

    @GetMapping("/projects/board")
    public String projectsBoard() {
        return "projects/board";
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

    @GetMapping("/tasks")
    public String tasks() {
        return "tasks/list";
    }

    @GetMapping("/tasks/board")
    public String tasksBoard() {
        return "tasks/board";
    }

    @GetMapping("/tasks/new")
    public String newTask() {
        return "tasks/create";
    }

    @GetMapping("/tasks/{id}")
    public String taskDetail() {
        return "tasks/detail";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editTask() {
        return "tasks/edit";
    }

    @GetMapping("/availability")
    public String availability() {
        return "availability/list";
    }

    @GetMapping("/availability/calendar")
    public String availabilityCalendar() {
        return "availability/calendar";
    }

    @GetMapping("/availability/new")
    public String newAvailability() {
        return "availability/create";
    }

    @GetMapping("/planning")
    public String planning() {
        return "planning/list";
    }

    @GetMapping("/planning/calendar")
    public String planningCalendar() {
        return "planning/calendar";
    }

    @GetMapping("/planning/new")
    public String newPlanning() {
        return "planning/create";
    }
}
