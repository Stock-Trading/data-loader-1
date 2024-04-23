package com.stocktrading.dataloader1.domain.exception;

public class AlreadySubscribedException extends RuntimeException {

    public AlreadySubscribedException(String message) {
        super(message);
    }
}
