package org.gca.schoolms.finance;

import java.util.List;
import java.math.BigDecimal;
import org.gca.schoolms.enrollment.EnrollmentFinanceAuthorizationType;
import org.gca.schoolms.enrollment.FinanceReviewStatus;
import org.gca.schoolms.enrollment.EnrollmentReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
public class FinanceController {

    private final EnrollmentReviewService enrollmentReviewService;
    private final FinanceLedgerService financeLedgerService;

    public FinanceController(EnrollmentReviewService enrollmentReviewService,
                             FinanceLedgerService financeLedgerService) {
        this.enrollmentReviewService = enrollmentReviewService;
        this.financeLedgerService = financeLedgerService;
    }

    @GetMapping("/finance")
    public String financeHome(Model model) {
        model.addAttribute("outstandingBalance", financeLedgerService.totalOutstandingBalance());
        model.addAttribute("familyAccounts", financeLedgerService.familyAccounts());
        model.addAttribute("feeTypes", financeLedgerService.feeTypes());
        model.addAttribute("schoolProjectTypes", financeLedgerService.schoolProjectTypes());
        model.addAttribute("students", financeLedgerService.students());
        model.addAttribute("studentFees", financeLedgerService.recentFees());
        model.addAttribute("paymentOpenFees", financeLedgerService.openFeesForPaymentPosting());
        model.addAttribute("paymentStudentOptions", financeLedgerService.paymentStudentOptions());
        model.addAttribute("paymentFamilyOptions", financeLedgerService.paymentFamilyOptions());
        model.addAttribute("payments", financeLedgerService.recentPaymentRows());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("paymentPurposes", PaymentPurpose.values());
        model.addAttribute("enrollmentQueue", enrollmentReviewService.loadFinanceQueue());
        model.addAttribute("financeStatuses", FinanceReviewStatus.values());
        model.addAttribute("financeAuthorizationTypes", EnrollmentFinanceAuthorizationType.values());
        return "finance/index";
    }

    @PostMapping("/finance/fee-types")
    public String createFeeType(@RequestParam String code,
                                @RequestParam String name,
                                @RequestParam(required = false) BigDecimal defaultAmount,
                                RedirectAttributes redirectAttributes) {
        financeLedgerService.createFeeType(code, name, defaultAmount);
        redirectAttributes.addFlashAttribute("message", "Fee type added.");
        return "redirect:/finance";
    }

    @PostMapping("/finance/school-project-types")
    public String createSchoolProjectType(@RequestParam String code,
                                          @RequestParam String name,
                                          RedirectAttributes redirectAttributes) {
        financeLedgerService.createSchoolProjectType(code, name);
        redirectAttributes.addFlashAttribute("message", "School project type added.");
        return "redirect:/finance";
    }

    @PostMapping("/finance/fees")
    public String assessFee(@RequestParam Long studentId,
                            @RequestParam Long feeTypeId,
                            @RequestParam BigDecimal amount,
                            @RequestParam String schoolYear,
                            @RequestParam(required = false) String description,
                            RedirectAttributes redirectAttributes) {
        financeLedgerService.assessFee(studentId, feeTypeId, amount, schoolYear, description);
        redirectAttributes.addFlashAttribute("message", "Fee assessed.");
        return "redirect:/finance";
    }

    @PostMapping("/finance/payments")
    public String recordPayment(@RequestParam PaymentPurpose paymentPurpose,
                                @RequestParam(required = false) Long studentId,
                                @RequestParam(required = false) Long payerFamilyAccountId,
                                @RequestParam(required = false) Long schoolProjectTypeId,
                                @RequestParam(defaultValue = "false") boolean crossFamilyConfirmed,
                                @RequestParam PaymentMethod paymentMethod,
                                @RequestParam BigDecimal amount,
                                @RequestParam(required = false) String referenceNumber,
                                @RequestParam(required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        try {
            financeLedgerService.recordPayment(
                paymentPurpose,
                studentId,
                payerFamilyAccountId,
                schoolProjectTypeId,
                crossFamilyConfirmed,
                paymentMethod,
                amount,
                referenceNumber,
                notes
            );
            redirectAttributes.addFlashAttribute("message", "Payment recorded.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/finance";
    }

    @PostMapping("/finance/enrollment-clearance")
    public String updateEnrollmentClearance(@RequestParam Long requestId,
                                            @RequestParam FinanceReviewStatus financeReviewStatus,
                                            @RequestParam(required = false) String financeComment,
                                            @RequestParam(required = false) List<EnrollmentFinanceAuthorizationType> authorizationTypes,
                                            @RequestParam(required = false) String authorizationNote,
                                            RedirectAttributes redirectAttributes) {
        enrollmentReviewService.updateFinanceReview(
            requestId,
            financeReviewStatus,
            financeComment,
            authorizationTypes == null ? List.of() : authorizationTypes,
            authorizationNote
        );
        redirectAttributes.addFlashAttribute("message", "Finance clearance updated.");
        return "redirect:/finance";
    }
}
