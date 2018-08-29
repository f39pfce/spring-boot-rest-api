package rest_api.presentation.logging.error_handling;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import rest_api.presentation.controllers.merchant.MerchantNotFoundException;
import rest_api.presentation.controllers.payment.PaymentNotFoundException;
import rest_api.presentation.security.UnauthorizedAccessException;
import rest_api.presentation.controllers.user.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Aspect used to handle any exceptions thrown by controllers and translate them into HTTP return body and code
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Return conflict status when there is an Exception thrown due to repositories persistence problems
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            TransactionSystemException.class})
    public void handleNonPersistedEntity() {}

    /**
     * Return a well formatted list of binding errors
     *
     * @param e exception
     * @return ResponseEntity response
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class})
    public ResponseEntity handleMissingFields(Exception e) {
        BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        if (bindingResult != null) {
            List<String> errors = new ArrayList<>();
            for(FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.status(500).build();
    }

    /**
     * Return a well formatted list of binding errors
     *
     * @param e exception
     * @return ResponseEntity response
     */
    @ExceptionHandler({
            ConstraintViolationException.class})
    public ResponseEntity handleConstraintViolationException(Exception e) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ((ConstraintViolationException) e).getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + " " + violation.getMessage());
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.status(500).build();
    }

    /**
     * Return not found where there is an Exception thrown due to repositories not found problems
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            PaymentNotFoundException.class,
            MerchantNotFoundException.class})
    public void handleEntityNotFound() {}

    /**
     * Return unauthorized when any Exception is thrown due to authorization failures
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            UnauthorizedAccessException.class,
            UsernameNotFoundException.class})
    public void handleUnauthorizedAccess() {}

    /**
     * Return server error for the following Exception list
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            RuntimeException.class})
    public void handleServerErrors() {}

    /**
     * Return user already exists code when UserExistsException is thrown.
     *
     * @return ResponseEntity
     */
    @ExceptionHandler({UserExistsException.class})
    public ResponseEntity handleUserExistsOnCreateUser() {
        return ResponseEntity.badRequest().body("User already exists.");
    }
}
