package org.gca.schoolms.finance;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
public class FinanceController {

    private final InvoiceRepository invoiceRepository;

    public FinanceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/finance")
    public String financeHome(Model model) {
        model.addAttribute("invoices", invoiceRepository.findTop10ByOrderByDueDateAsc());
        model.addAttribute("outstandingBalance", invoiceRepository.sumOutstandingBalance().orElseThrow());
        return "finance/index";
    }
}
