package org.gca.schoolms.admin;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice(assignableTypes = DataImportController.class)
public class AdminImportExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded() {
        return "redirect:/admin/imports?uploadError=fileTooLarge";
    }
}
