package com.stocktrading.dataloader1.domain.mapper;

import java.util.List;

public interface DataMapper<T> {

    List<T> map(String data);

}
