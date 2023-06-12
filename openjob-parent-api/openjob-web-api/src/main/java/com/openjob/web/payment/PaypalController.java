//package com.openjob.web.payment;
//
//
//import com.paypal.api.payments.Links;
//import com.paypal.api.payments.Payment;
//import com.paypal.base.rest.PayPalRESTException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/client/paypal")
//public class PaypalController {
//    private final PaypalService paypalService;
//
//    @Value("${client.base_url}")
//    private String clientBaseUrl;
//
//    private static final String SUCCESS_URL = "/success";
//    private static final String FAILURE_URL = "/failure";
//
//
//    @PostMapping("/pay")
//    public String createPayment(@ModelAttribute CreatePaymentDTO dto) {
//        try {
//            Payment payment = paypalService.createPayment(dto.getPrice(), "USD", "Paypal", "ORDER",
//                    "Nap tien", clientBaseUrl+ "/client/paypal" + FAILURE_URL,
//                    clientBaseUrl+ "/client/paypal" + SUCCESS_URL);
//            for(Links link:payment.getLinks()) {
//                if(link.getRel().equals("approval_url")) {
//                    return link.getHref();
//                }
//            }
//        } catch (PayPalRESTException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @PostMapping(value = SUCCESS_URL)
//    public ResponseEntity<ResponseDTO> executePayment(@RequestBody ExecutePaymentDTO dto) {
//        try {
//            Payment payment = paypalService.executePayment(dto.getPaymentId(), dto.getPayerId());
//            System.out.println(payment.toJSON());
//            if (payment.getState().equals("approved")) {
//                return ResponseGenerator.generate(
//                        HttpStatus.OK.value(),
//                        Boolean.TRUE,
//                        SuccessMessage.PAYMENT_SUCCESS,
//                        null
//                );
//            }
//        } catch (PayPalRESTException e) {
//            System.out.println(e.getMessage());
//        }
//        return ResponseGenerator.generate(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                Boolean.FALSE,
//                SuccessMessage.PAYMENT_FAILURE,
//                null
//        );
//    }
//}
