package com.jjy.contents_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jjy.contents_lab.service.PaymentsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentsController {
    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @GetMapping("/pay")
    public String contentIdeaPage(Model model, 
                            HttpSession session,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size) {

        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null) {
            return "redirect:/"; 
        }
        
        try {
            Long userId = Long.parseLong(session.getAttribute("userId").toString());
            model.addAttribute("pay_list", paymentsService.getPaymentsList(userId, page, size));
            model.addAttribute("pay_info", paymentsService.getPaymentsInfo(userId));
        } catch (Exception e) {
            System.out.println("Error in sendLog: " + e.getMessage());
        }

        return "pay_list";
    }
}
