package com.aspora.controller;

import com.aspora.dto.EventForecastResponse;
import com.aspora.dto.EventRequest;
import com.aspora.service.ClassificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Event Forecast")
public class EventForecastController {

    private final ClassificationService classificationService;

    @PostMapping("/event-forecast")
    @Operation(summary = "Get weather advisory for an event")
    public ResponseEntity<EventForecastResponse> getEventForecast(
            @Valid @RequestBody EventRequest request) {

        if (request.getEndTime().isBefore(request.getStartTime()) ||
                request.getEndTime().isEqual(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        EventForecastResponse response = classificationService.classify(request);
        return ResponseEntity.ok(response);
    }
}
