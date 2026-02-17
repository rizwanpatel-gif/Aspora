package com.aspora.service;

import com.aspora.dto.EventRequest;
import com.aspora.dto.HourlyForecast;
import com.aspora.dto.openmeteo.OpenMeteoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestClient openMeteoRestClient;

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public List<HourlyForecast> fetchForecast(EventRequest request) {
        LocalDate startDate = request.getStartTime().toLocalDate();
        LocalDate endDate = request.getEndTime().toLocalDate();

        log.info("Fetching weather forecast for lat={}, lon={}, from={} to={}",
                request.getLocation().getLatitude(),
                request.getLocation().getLongitude(),
                startDate, endDate);

        OpenMeteoResponse response = openMeteoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", request.getLocation().getLatitude())
                        .queryParam("longitude", request.getLocation().getLongitude())
                        .queryParam("hourly", "temperature_2m,precipitation_probability,weather_code,wind_speed_10m")
                        .queryParam("start_date", startDate.toString())
                        .queryParam("end_date", endDate.toString())
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);

        if (response == null || response.getHourly() == null) {
            throw new RuntimeException("Empty response from OpenMeteo API");
        }

        return filterToEventWindow(response, request.getStartTime(), request.getEndTime());
    }

    private List<HourlyForecast> filterToEventWindow(OpenMeteoResponse response,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        OpenMeteoResponse.Hourly hourly = response.getHourly();
        List<HourlyForecast> forecasts = new ArrayList<>();
        DateTimeFormatter apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        for (int i = 0; i < hourly.getTime().size(); i++) {
            LocalDateTime forecastTime = LocalDateTime.parse(hourly.getTime().get(i), apiFormatter);

            if (!forecastTime.isBefore(startTime) && forecastTime.isBefore(endTime)) {
                forecasts.add(HourlyForecast.builder()
                        .time(forecastTime.format(HOUR_FORMATTER))
                        .rainProb(hourly.getPrecipitationProbability().get(i))
                        .windKmh(hourly.getWindSpeed10m().get(i))
                        .temperatureC(hourly.getTemperature2m().get(i))
                        .weatherCode(hourly.getWeatherCode().get(i))
                        .build());
            }
        }

        return forecasts;
    }
}
