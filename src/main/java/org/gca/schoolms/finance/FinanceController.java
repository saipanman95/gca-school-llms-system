package org.gca.schoolms.finance;

import java.util.List;
import java.math.BigDecimal;
import org.gca.schoolms.enrollment.EnrollmentFinanceAuthorizationType;
import org.gca.schoolms.enrollment.FinanceReviewStatus;
import org.gca.schoolms.enrollment.EnrollmentReviewService;
import org.gca.schoolms.settings.SchoolYearService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE','SCHOOL_CASHIER')")
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
    public String financeHome(Model model) {
        var financeQueue = enrollmentReviewService.loadFinanceQueue();
        model.addAttribute("home", financeLedgerService.loadFinanceHome(financeQueue.size()));
        return "finance/home";
    }

    @GetMapping("/finance/cashier")
    public String cashierHome(@RequestParam(required = false) Long selectedPayerId, Model model) {
        populateCashierModel(model, selectedPayerId);
        return "finance/cashier";
    }

    @GetMapping("/finance/clearance")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
    public String clearanceHome(Model model) {
        populateClearanceModel(model);
        return "finance/clearance";
    }

    @GetMapping("/finance/accounts")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
    public String accountsHome(Model model) {
        populateAccountsModel(model);
        return "finance/accounts";
    }

    @GetMapping("/finance/accounts/{familyAccountId}/statement")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
    public String familyStatement(@PathVariable Long familyAccountId, Model model) {
        model.addAttribute("statement", financeLedgerService.loadBillingStatement(familyAccountId));
        return "finance/statement";
    }

    @GetMapping("/finance/fees")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
    public String feeMaintenanceHome(@RequestParam(required = false) Long editFeeTypeId, Model model) {
        populateFeeMaintenanceModel(editFeeTypeId, model);
        return "finance/fees";
    }

    @GetMapping("/finance/payments/{paymentId}/receipt")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE','SCHOOL_CASHIER')")
    public String paymentReceipt(@PathVariable Long paymentId, Model model) {
        model.addAttribute("receipt", financeLedgerService.loadReceipt(paymentId));
        return "finance/payment-receipt";
    }

    @PostMapping("/finance/fee-types")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
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
        return "redirect:/finance/fees";
    }

    @PostMapping("/finance/school-project-types")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
    public String createSchoolProjectType(@RequestParam String code,
                                          @RequestParam String name,
                                          RedirectAttributes redirectAttributes) {
        financeLedgerService.createSchoolProjectType(code, name);
        redirectAttributes.addFlashAttribute("message", "School project type added.");
        return "redirect:/finance/fees";
    }

    @PostMapping("/finance/fees")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
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
        return "redirect:/finance/accounts";
    }

    @PostMapping("/finance/fees/{studentFeeId}/cancel")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
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
        return "redirect:/finance/accounts";
    }

    @PostMapping("/finance/accounts/{familyAccountId}/statement/email")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
    public String emailFamilyStatement(@PathVariable Long familyAccountId,
                                       RedirectAttributes redirectAttributes) {
        try {
            financeLedgerService.emailBillingStatement(familyAccountId);
            redirectAttributes.addFlashAttribute("message", "Billing statement emailed.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/finance/accounts";
    }

    @PostMapping("/finance/payments")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE','SCHOOL_CASHIER')")
    public String recordPayment(@RequestParam PaymentPurpose paymentPurpose,
                                @RequestParam(required = false) Long studentId,
                                @RequestParam(required = false) Long payerProfileId,
                                @RequestParam(required = false) Long schoolProjectTypeId,
                                @RequestParam(defaultValue = "false") boolean crossFamilyConfirmed,
                                @RequestParam(defaultValue = "false") boolean anonymousToFamily,
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
                payerProfileId,
                schoolProjectTypeId,
                crossFamilyConfirmed,
                paymentMethod,
                amount,
                referenceNumber,
                notes,
                anonymousToFamily,
                authentication.getName()
            );
            return "redirect:/finance/payments/" + paymentId + "/receipt";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/finance/cashier";
    }

    @PostMapping("/finance/payers")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE','SCHOOL_CASHIER')")
    public String createPayer(PayerProfileForm payerProfileForm,
                              RedirectAttributes redirectAttributes) {
        Long payerId = financeLedgerService.createPayerProfile(payerProfileForm);
        redirectAttributes.addFlashAttribute("message", "Payer added.");
        return "redirect:/finance/cashier?selectedPayerId=" + payerId;
    }

    @PostMapping("/finance/enrollment-clearance")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SCHOOL_ADMIN','SCHOOL_FINANCE')")
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
        return "redirect:/finance/clearance";
    }

    private void populateCashierModel(Model model, Long selectedPayerId) {
        model.addAttribute("paymentOpenFees", financeLedgerService.openFeesForPaymentPosting());
        model.addAttribute("paymentStudentOptions", financeLedgerService.paymentStudentOptions());
        model.addAttribute("payerOptions", financeLedgerService.payerOptions());
        model.addAttribute("selectedPayerOption", selectedPayerId != null ? financeLedgerService.loadPayerOption(selectedPayerId) : null);
        model.addAttribute("payerProfileForm", new PayerProfileForm());
        model.addAttribute("payments", financeLedgerService.recentPaymentRows());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("paymentPurposes", PaymentPurpose.values());
        model.addAttribute("schoolProjectTypes", financeLedgerService.schoolProjectTypes());
    }

    private void populateClearanceModel(Model model) {
        model.addAttribute("enrollmentQueue", enrollmentReviewService.loadFinanceQueue());
        model.addAttribute("financeStatuses", FinanceReviewStatus.values());
        model.addAttribute("financeAuthorizationTypes", EnrollmentFinanceAuthorizationType.values());
    }

    private void populateAccountsModel(Model model) {
        model.addAttribute("paymentStudentOptions", financeLedgerService.paymentStudentOptions());
        model.addAttribute("feeTypes", financeLedgerService.feeTypes());
        model.addAttribute("schoolYears", schoolYearService.schoolYears());
        model.addAttribute("currentSchoolYear", schoolYearService.currentSchoolYearLabel());
        model.addAttribute("studentFees", financeLedgerService.recentFees());
        model.addAttribute("outstandingAccounts", financeLedgerService.outstandingAccounts());
    }

    private void populateFeeMaintenanceModel(Long editFeeTypeId, Model model) {
        model.addAttribute("feeTypes", financeLedgerService.feeTypes());
        model.addAttribute("feeTypeForm", financeLedgerService.buildFeeTypeForm(editFeeTypeId));
        model.addAttribute("schoolProjectTypes", financeLedgerService.schoolProjectTypes());
    }
}
