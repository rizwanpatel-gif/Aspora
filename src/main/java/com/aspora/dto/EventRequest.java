package com.aspora.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Event name is required")
    private String name;

    @Valid
    @NotNull(message = "Location is required")
    private Location location;

    @NotNull(message = "Start time is required")
    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @JsonProperty("end_time")
    private LocalDateTime endTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {

        @NotNull(message = "Latitude is required")
        private Double latitude;

        @NotNull(message = "Longitude is required")
        private Double longitude;
    }
}
