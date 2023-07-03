package com.openjob.web.payment;


import com.openjob.common.enums.ServiceType;
import com.openjob.common.model.Company;
import com.openjob.common.model.Invoice;
import com.openjob.web.company.CompanyService;
import com.openjob.web.dto.CreatePaymentDTO;
import com.openjob.web.dto.ExecutePaymentDTO;
import com.openjob.web.trackinginvoice.InvoiceService;
import com.openjob.web.user.UserService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/paypal")
public class PaypalController {
    private final PaypalService paypalService;
    private final CompanyService companyService;
    private final InvoiceService invoiceService;

    @Value("${client.base_url}")
    private String clientBaseUrl;

    private static final String SUCCESS_URL = "/success";
    private static final String FAILURE_URL = "/failure";

    @GetMapping()
    public String paypal(@RequestParam("price") Double price, Model model) {
        model.addAttribute("price", price);
        return "paypal";
    }

    @PostMapping("/pay")
    public RedirectView createPayment(@ModelAttribute CreatePaymentDTO dto) {
        try {
            Payment payment = paypalService.createPayment(dto.getPrice(), "USD", "Paypal", "ORDER",
                    "Nap tien", clientBaseUrl+ "/paypal" + FAILURE_URL,
                    clientBaseUrl+ "/dashboard/paypal" + SUCCESS_URL);
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    RedirectView redirectView = new RedirectView();
                    redirectView.setUrl(link.getHref());
                    return redirectView;
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
                companyService.updateAccountBalance(dto.getCompanyId(), Double.parseDouble(payment.getTransactions().get(0).getAmount().getTotal()) * 10);
                // tracking
                Company company = companyService.getById(dto.getCompanyId());
                Invoice invoice = new Invoice();
                invoice.setCompanyId(company.getId());
                invoice.setCompanyName(company.getName());
                invoice.setServiceType(ServiceType.COIN_IN);
                invoice.setAmount(Double.parseDouble(payment.getTransactions().get(0).getAmount().getTotal()) * 10);
                invoiceService.save(invoice);
                return ResponseEntity.ok("Thanh toán thành công");
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Thanh toán không thành công");
    }
}
