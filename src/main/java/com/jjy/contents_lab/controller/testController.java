package com.jjy.contents_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class testController {
    @ResponseBody
    @GetMapping("/test")
    public String test() {
        return "<h1>test 컨트롤러 작동 성공! 화면이 잘 나옵니다.</h1>";
    }
    
}
