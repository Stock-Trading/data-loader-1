package com.stocktrading.dataloader1.domain.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Component
public class DataLoaderModel {

    private final String uuid = UUID.randomUUID().toString();

}
