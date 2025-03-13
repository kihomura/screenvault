package com.kihomura.screenvault.exception;

import com.kihomura.screenvault.pojo.dto.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    @ExceptionHandler({Exception.class})
    public ResponseMessage handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        return new ResponseMessage(500, e.getMessage(), null);
    }
}
