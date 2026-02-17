package com.aspora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventForecastResponse {

    private String classification;

    private String summary;

    private List<String> reason;

    @JsonProperty("event_window_forecast")
    private List<HourlyForecast> eventWindowForecast;
}
