package com.stocktrading.dataloader1.domain.ports;

import java.util.List;

public interface DataMapper<T> {

    List<T> map(String data);

}
