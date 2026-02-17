package com.aspora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyForecast {

    private String time;

    @JsonProperty("rain_prob")
    private int rainProb;

    @JsonProperty("wind_kmh")
    private double windKmh;

    @JsonProperty("temperature_c")
    private double temperatureC;

    @JsonProperty("weather_code")
    private int weatherCode;
}
