package com.example.WebOrder.exception;

import com.example.WebOrder.exception.status4xx.ForbiddenException;
import com.example.WebOrder.exception.status4xx.NoEntityException;
import com.example.WebOrder.exception.status4xx.NotAuthenticatedException;
import com.example.WebOrder.exception.status4xx.TypicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(TypicalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleStatus400Exception(final TypicalException ex, Model model){
        log.info("예외 발생: ", ex);
        model.addAttribute("errorMsg", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleStatus401Exception(final NotAuthenticatedException ex, Model model){
        log.info("예외 발생: ", ex);
        model.addAttribute("errorMsg", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleStatus403Exception(final ForbiddenException ex, Model model){
        log.info("예외 발생: ", ex);
        model.addAttribute("errorMsg", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(NoEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleStatus404Exception(final NoEntityException ex, Model model){
        log.info("예외 발생: ", ex);
        model.addAttribute("errorMsg", ex.getMessage());
        return "error/error";
    }
}
