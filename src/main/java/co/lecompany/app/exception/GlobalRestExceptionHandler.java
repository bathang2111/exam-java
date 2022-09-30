package co.lecompany.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RestController
@Slf4j
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    //  Status code 500
    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleGenericException(Exception ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError apiError =
                new ApiError(
                        String.valueOf(httpStatus.value()),
                        ex.getLocalizedMessage());
        return new ResponseEntity<>(apiError, httpStatus);
    }

    @ExceptionHandler(value = {UserException.class})
    protected ResponseEntity<Object> userException(UserException ex) {
        ApiError apiError =
                new ApiError(
                        String.valueOf(ex.getCode()),
                        ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(ex.getCode()));
    }
}
