package com.aspora.service;

import com.aspora.dto.EventForecastResponse;
import com.aspora.dto.EventRequest;
import com.aspora.dto.HourlyForecast;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassificationServiceTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private ClassificationService classificationService;

    private EventRequest buildRequest() {
        return EventRequest.builder()
                .name("Test Event")
                .location(EventRequest.Location.builder()
                        .latitude(19.076)
                        .longitude(72.8777)
                        .build())
                .startTime(LocalDateTime.of(2026, 1, 10, 17, 0))
                .endTime(LocalDateTime.of(2026, 1, 10, 19, 0))
                .build();
    }

    @Test
    void shouldClassifyAsSafe_whenNoAdverseConditions() {
        List<HourlyForecast> forecasts = List.of(
                HourlyForecast.builder().time("17:00").rainProb(20).windKmh(10).temperatureC(25).weatherCode(0).build(),
                HourlyForecast.builder().time("18:00").rainProb(15).windKmh(8).temperatureC(24).weatherCode(1).build()
        );
        when(weatherService.fetchForecast(any())).thenReturn(forecasts);

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Safe");
        assertThat(response.getEventWindowForecast()).hasSize(2);
    }

    @Test
    void shouldClassifyAsRisky_whenRainProbabilityExceeds60() {
        List<HourlyForecast> forecasts = List.of(
                HourlyForecast.builder().time("17:00").rainProb(70).windKmh(10).temperatureC(25).weatherCode(3).build(),
                HourlyForecast.builder().time("18:00").rainProb(40).windKmh(8).temperatureC(24).weatherCode(2).build()
        );
        when(weatherService.fetchForecast(any())).thenReturn(forecasts);

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Risky");
        assertThat(response.getReason()).anyMatch(r -> r.contains("Rain probability is 70%"));
    }

    @Test
    void shouldClassifyAsRisky_whenModerateRainForecast() {
        List<HourlyForecast> forecasts = List.of(
                HourlyForecast.builder().time("17:00").rainProb(50).windKmh(10).temperatureC(20).weatherCode(61).build()
        );
        when(weatherService.fetchForecast(any())).thenReturn(forecasts);

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Risky");
        assertThat(response.getReason()).anyMatch(r -> r.contains("Moderate rain"));
    }

    @Test
    void shouldClassifyAsRisky_whenWindBetween30And50() {
        List<HourlyForecast> forecasts = List.of(
                HourlyForecast.builder().time("17:00").rainProb(10).windKmh(35).temperatureC(22).weatherCode(0).build()
        );
        when(weatherService.fetchForecast(any())).thenReturn(forecasts);

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Risky");
        assertThat(response.getReason()).anyMatch(r -> r.contains("Wind speed is 35.0 km/h"));
    }

    @Test
    void shouldClassifyAsUnsafe_whenThunderstormForecast() {
        List<HourlyForecast> forecasts = List.of(
                HourlyForecast.builder().time("17:00").rainProb(90).windKmh(40).temperatureC(22).weatherCode(95).build()
        );
        when(weatherService.fetchForecast(any())).thenReturn(forecasts);

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Unsafe");
        assertThat(response.getReason()).anyMatch(r -> r.contains("Thunderstorm"));
    }

    @Test
    void shouldClassifyAsUnsafe_whenHeavyPrecipitation() {
        List<HourlyForecast> forecasts = List.of(
                HourlyForecast.builder().time("17:00").rainProb(85).windKmh(20).temperatureC(18).weatherCode(65).build()
        );
        when(weatherService.fetchForecast(any())).thenReturn(forecasts);

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Unsafe");
        assertThat(response.getReason()).anyMatch(r -> r.contains("Heavy precipitation"));
    }

    @Test
    void shouldClassifyAsUnsafe_whenWindExceeds50() {
        List<HourlyForecast> forecasts = List.of(
                HourlyForecast.builder().time("17:00").rainProb(10).windKmh(55).temperatureC(22).weatherCode(0).build()
        );
        when(weatherService.fetchForecast(any())).thenReturn(forecasts);

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Unsafe");
        assertThat(response.getReason()).anyMatch(r -> r.contains("Dangerous wind speed"));
    }

    @Test
    void shouldReturnSafe_whenNoForecastData() {
        when(weatherService.fetchForecast(any())).thenReturn(List.of());

        EventForecastResponse response = classificationService.classify(buildRequest());

        assertThat(response.getClassification()).isEqualTo("Safe");
        assertThat(response.getSummary()).contains("No hourly forecast data");
    }
}
