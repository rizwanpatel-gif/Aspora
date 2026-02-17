package com.aspora.controller;

import com.aspora.dto.EventForecastResponse;
import com.aspora.dto.HourlyForecast;
import com.aspora.service.ClassificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventForecastController.class)
class EventForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClassificationService classificationService;

    @Test
    void shouldReturnForecast_whenValidRequest() throws Exception {
        EventForecastResponse mockResponse = EventForecastResponse.builder()
                .classification("Safe")
                .summary("Weather conditions look favorable for the event.")
                .reason(List.of("No adverse weather conditions detected"))
                .eventWindowForecast(List.of(
                        HourlyForecast.builder().time("17:00").rainProb(20).windKmh(10).temperatureC(25).weatherCode(0).build()
                ))
                .build();

        when(classificationService.classify(any())).thenReturn(mockResponse);

        String requestBody = """
                {
                  "name": "Football Match",
                  "location": {
                    "latitude": 19.0760,
                    "longitude": 72.8777
                  },
                  "start_time": "2026-01-10T17:00:00",
                  "end_time": "2026-01-10T19:00:00"
                }
                """;

        mockMvc.perform(post("/event-forecast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classification").value("Safe"))
                .andExpect(jsonPath("$.event_window_forecast").isArray());
    }

    @Test
    void shouldReturnBadRequest_whenNameMissing() throws Exception {
        String requestBody = """
                {
                  "location": {
                    "latitude": 19.0760,
                    "longitude": 72.8777
                  },
                  "start_time": "2026-01-10T17:00:00",
                  "end_time": "2026-01-10T19:00:00"
                }
                """;

        mockMvc.perform(post("/event-forecast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenLocationMissing() throws Exception {
        String requestBody = """
                {
                  "name": "Football Match",
                  "start_time": "2026-01-10T17:00:00",
                  "end_time": "2026-01-10T19:00:00"
                }
                """;

        mockMvc.perform(post("/event-forecast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenEndTimeBeforeStartTime() throws Exception {
        when(classificationService.classify(any())).thenReturn(null);

        String requestBody = """
                {
                  "name": "Football Match",
                  "location": {
                    "latitude": 19.0760,
                    "longitude": 72.8777
                  },
                  "start_time": "2026-01-10T19:00:00",
                  "end_time": "2026-01-10T17:00:00"
                }
                """;

        mockMvc.perform(post("/event-forecast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
