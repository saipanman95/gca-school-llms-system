package org.gca.schoolms.finance;

import java.util.List;
import java.math.BigDecimal;
import org.gca.schoolms.enrollment.EnrollmentFinanceAuthorizationType;
import org.gca.schoolms.enrollment.FinanceReviewStatus;
import org.gca.schoolms.enrollment.EnrollmentReviewService;
import org.gca.schoolms.settings.SchoolYearService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
public class FinanceController {

    private final EnrollmentReviewService enrollmentReviewService;
    private final FinanceLedgerService financeLedgerService;
    private final SchoolYearService schoolYearService;

    public FinanceController(EnrollmentReviewService enrollmentReviewService,
                             FinanceLedgerService financeLedgerService,
                             SchoolYearService schoolYearService) {
        this.enrollmentReviewService = enrollmentReviewService;
        this.financeLedgerService = financeLedgerService;
        this.schoolYearService = schoolYearService;
    }

    @GetMapping("/finance")
    public String financeHome(@RequestParam(required = false) Long editFeeTypeId, Model model) {
        model.addAttribute("outstandingBalance", financeLedgerService.totalOutstandingBalance());
        model.addAttribute("familyAccounts", financeLedgerService.familyAccounts());
        model.addAttribute("feeTypes", financeLedgerService.feeTypes());
        model.addAttribute("feeTypeForm", financeLedgerService.buildFeeTypeForm(editFeeTypeId));
        model.addAttribute("schoolProjectTypes", financeLedgerService.schoolProjectTypes());
        model.addAttribute("schoolYears", schoolYearService.schoolYears());
        model.addAttribute("currentSchoolYear", schoolYearService.currentSchoolYearLabel());
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

    @GetMapping("/finance/payments/{paymentId}/receipt")
    public String paymentReceipt(@PathVariable Long paymentId, Model model) {
        model.addAttribute("receipt", financeLedgerService.loadReceipt(paymentId));
        return "finance/payment-receipt";
    }

    @PostMapping("/finance/fee-types")
    public String createFeeType(FeeTypeForm feeTypeForm,
                                RedirectAttributes redirectAttributes) {
        try {
            if (feeTypeForm.getId() == null) {
                financeLedgerService.createFeeType(
                    feeTypeForm.getCode(),
                    feeTypeForm.getName(),
                    feeTypeForm.getDefaultAmount(),
                    feeTypeForm.getMaxAssessmentsPerStudentPerSchoolYear()
                );
                redirectAttributes.addFlashAttribute("message", "Fee type added.");
            } else {
                financeLedgerService.updateFeeType(
                    feeTypeForm.getId(),
                    feeTypeForm.getCode(),
                    feeTypeForm.getName(),
                    feeTypeForm.getDefaultAmount(),
                    feeTypeForm.getMaxAssessmentsPerStudentPerSchoolYear(),
                    feeTypeForm.isActive()
                );
                redirectAttributes.addFlashAttribute("message", "Fee type updated.");
            }
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
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
        try {
            financeLedgerService.assessFee(studentId, feeTypeId, amount, schoolYear, description);
            redirectAttributes.addFlashAttribute("message", "Fee assessed.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/finance";
    }

    @PostMapping("/finance/fees/{studentFeeId}/cancel")
    public String cancelFee(@PathVariable Long studentFeeId,
                            @RequestParam String cancellationReason,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            financeLedgerService.cancelFee(studentFeeId, cancellationReason, authentication.getName());
            redirectAttributes.addFlashAttribute("message", "Fee cancelled.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
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
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            Long paymentId = financeLedgerService.recordPayment(
                paymentPurpose,
                studentId,
                payerFamilyAccountId,
                schoolProjectTypeId,
                crossFamilyConfirmed,
                paymentMethod,
                amount,
                referenceNumber,
                notes,
                authentication.getName()
            );
            return "redirect:/finance/payments/" + paymentId + "/receipt";
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
