package com.openjob.web.payment;


import com.openjob.web.dto.CreatePaymentDTO;
import com.openjob.web.dto.ExecutePaymentDTO;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/paypal")
public class PaypalController {
    private final PaypalService paypalService;

    @Value("${client.base_url}")
    private String clientBaseUrl;

    private static final String SUCCESS_URL = "/success";
    private static final String FAILURE_URL = "/failure";

    @GetMapping()
    public String paypal() {
        return "paypal";
    }

    @PostMapping("/pay")
    @ResponseBody
    public String createPayment(@ModelAttribute CreatePaymentDTO dto) {
        try {
            Payment payment = paypalService.createPayment(dto.getPrice(), "USD", "Paypal", "ORDER",
                    "Nap tien", clientBaseUrl+ "/client/paypal" + FAILURE_URL,
                    clientBaseUrl+ "/client/paypal" + SUCCESS_URL);
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping(value = SUCCESS_URL)
    @ResponseBody
    public ResponseEntity<String> executePayment(@RequestBody ExecutePaymentDTO dto) {
        try {
            Payment payment = paypalService.executePayment(dto.getPaymentId(), dto.getPayerId());
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                return ResponseEntity.ok("Thanh toán thành công");
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Thanh toán không thành công");
    }
}
