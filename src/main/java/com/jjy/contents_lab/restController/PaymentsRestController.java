package com.jjy.contents_lab.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjy.contents_lab.dto.PaymentsDto;
import com.jjy.contents_lab.service.PaymentsService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/subscribe")
public class PaymentsRestController {
    private final PaymentsService paymentsService;

    public PaymentsRestController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerSubscription(HttpSession session, @RequestBody PaymentsDto paymentsDto) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "로그인 세션이 만료되었습니다. 다시 로그인해 주세요.");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        paymentsDto.setUserId(userId); 

        boolean success = paymentsService.processSubscription(paymentsDto);

        if (success) {
            response.put("success", true);
            response.put("message", "구독 신청 및 첫 달 결제가 완료되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "결제 승인에 실패했습니다. 카드를 확인해 주세요.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
