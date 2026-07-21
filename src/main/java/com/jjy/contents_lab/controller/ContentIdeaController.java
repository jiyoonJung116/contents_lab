package com.jjy.contents_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentIdeaController {
    @GetMapping("/idea")
    public String contentIdeaPage() {
        return "content_idea";
    }
}
