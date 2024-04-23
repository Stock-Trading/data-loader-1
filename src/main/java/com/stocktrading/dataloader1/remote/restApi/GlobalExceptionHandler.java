package com.stocktrading.dataloader1.remote.restApi;

import com.stocktrading.dataloader1.domain.exception.AlreadySubscribedException;
import com.stocktrading.dataloader1.domain.exception.ModelNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ModelNotFoundException.class)
    ExceptionDto handleModelNotFound(ModelNotFoundException exception) {
        return ExceptionDto.builder()
                .message(exception.getMessage())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadySubscribedException.class)
    ExceptionDto handleAlreadySubscribed(AlreadySubscribedException exception) {
        return ExceptionDto.builder()
                .message(exception.getMessage())
                .timeStamp(LocalDateTime.now())
                .build();
    }

}
