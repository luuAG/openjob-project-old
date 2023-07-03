package com.openjob.web.trackinginvoice;


import com.openjob.common.model.Invoice;
import com.openjob.common.model.PagingModel;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/tracking")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @GetMapping("/invoice/{companyId}")
    public ResponseEntity<Page<Invoice>> getInvoices(
            @And({
                    @Spec(path = "companyName", spec = Like.class),
                    @Spec(path = "serviceType", spec = Equal.class),
                    @Spec(path = "createdAt", params = {"startDate", "endDate"}, spec = Between.class)
            })Specification<Invoice> invoiceSpec, PagingModel pagingModel,
            @PathVariable("companyId") String companyId){
        if (invoiceSpec == null)
            invoiceSpec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("companyId"), companyId));
        return ResponseEntity.ok(invoiceService.getAll(invoiceSpec, pagingModel.getPageable()));
    }
}
