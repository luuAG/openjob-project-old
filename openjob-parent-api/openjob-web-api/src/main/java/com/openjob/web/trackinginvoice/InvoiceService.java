package com.openjob.web.trackinginvoice;

import com.openjob.common.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepo;

    public Page<Invoice> getAll(Specification<Invoice> specification, Pageable pageable){
        return invoiceRepo.findAll(specification, pageable);
    }
    public Invoice save(Invoice invoice){
        invoice.setCreatedAt(new Date());

        return invoiceRepo.save(invoice);
    }
}
