package io.alviss.recipe_api.recipe_api.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.alviss.recipe_api.recipe_api.config.exception.InvalidPasswordException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.UnexpectedTypeException;


@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    @ApiResponse(responseCode = "4xx/5xx", description = "Error")
    public ResponseEntity<ErrorResponse> handleNotFound(final ResponseStatusException exception) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(exception.getStatus().value());
        errorResponse.setException(exception.getClass().getSimpleName());
        errorResponse.setMessage(exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception) {
        final BindingResult bindingResult = exception.getBindingResult();
        
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .map(error -> {
                    final FieldError fieldError = new FieldError();
                    fieldError.setErrorCode(error.getCode());
                    fieldError.setField(error.getField());
                    fieldError.setMessage(error.getDefaultMessage());
        		    System.out.println(error.toString());
                    return fieldError;
                })
                .collect(Collectors.toList());
        final ObjectError globalErr = bindingResult.getGlobalErrors().get(0);
        final FieldError confirmPasswordError = new FieldError();
        confirmPasswordError.setErrorCode(globalErr.getCode());confirmPasswordError.setMessage(globalErr.getDefaultMessage());
        confirmPasswordError.setField("confirmPassword");
        fieldErrors.add(confirmPasswordError);
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setException(exception.getClass().getSimpleName());
        errorResponse.setFieldErrors(fieldErrors);
        errorResponse.setMessage(exception.getMessage());
        if (exception.getMessage().contains("Validation failed for")) {
            errorResponse.setMessage("Validation of required input fields failed!");
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedHttpMediaType (final HttpMediaTypeNotSupportedException ex) {
        ex.printStackTrace();
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        errorResponse.setException(ex.getClass().getSimpleName());
        errorResponse.setMessage("Invalid Content-Type passed in request. Acceptable type is 'application/json'");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable (final HttpMessageNotReadableException ex) {
        ex.printStackTrace();
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setException(ex.getClass().getSimpleName());
        errorResponse.setMessage(ex.getMessage());
        if (ex.getMessage().contains("Required request body is missing")) {
            errorResponse.setMessage("Please pass in a request body!");
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword (final InvalidPasswordException ex) {
        ex.printStackTrace();
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setException(ex.getClass().getSimpleName());
        errorResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound (final UsernameNotFoundException exception) {
        exception.printStackTrace();
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setException(exception.getClass().getSimpleName());
        errorResponse.setMessage("User with email does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(InvalidPasswordException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidPassword (final InvalidPasswordException ex) {
//        ex.printStackTrace();
//        final ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setException(ex.getClass().getSimpleName());
//        errorResponse.setMessage(ex.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(final Throwable exception) {
        exception.printStackTrace();
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setException(exception.getClass().getSimpleName());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
