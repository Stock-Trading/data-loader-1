package com.stocktrading.dataloader1.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class DayParameters {

    @JsonProperty("1. open")
    double open;
    @JsonProperty("2. high")
    double high;
    @JsonProperty("3. low")
    double low;
    @JsonProperty("4. close")
    double close;
    @JsonProperty("5. volume")
    int volume;

}
