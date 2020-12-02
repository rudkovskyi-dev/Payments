package ua.rudkovskyi.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String greeting() {
        return "welcome";
    }

    @GetMapping("/main")
    public String start(){
        return "main";
    }

}
