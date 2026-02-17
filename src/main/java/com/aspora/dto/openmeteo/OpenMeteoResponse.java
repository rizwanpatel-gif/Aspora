package com.aspora.dto.openmeteo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OpenMeteoResponse {

    private double latitude;
    private double longitude;

    @JsonProperty("hourly")
    private Hourly hourly;

    @Data
    @NoArgsConstructor
    public static class Hourly {

        private List<String> time;

        @JsonProperty("temperature_2m")
        private List<Double> temperature2m;

        @JsonProperty("precipitation_probability")
        private List<Integer> precipitationProbability;

        @JsonProperty("weather_code")
        private List<Integer> weatherCode;

        @JsonProperty("wind_speed_10m")
        private List<Double> windSpeed10m;
    }
}
