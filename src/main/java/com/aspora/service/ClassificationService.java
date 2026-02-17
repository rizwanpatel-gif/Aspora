package com.aspora.service;

import com.aspora.dto.EventForecastResponse;
import com.aspora.dto.EventRequest;
import com.aspora.dto.HourlyForecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassificationService {

    private static final int RAIN_PROB_RISKY_THRESHOLD = 60;
    private static final double WIND_RISKY_THRESHOLD = 30.0;
    private static final double WIND_UNSAFE_THRESHOLD = 50.0;

    private static final List<Integer> THUNDERSTORM_CODES = List.of(95, 96, 99);
    private static final List<Integer> HEAVY_PRECIPITATION_CODES = List.of(65, 67, 75, 77, 82, 85, 86);
    private static final List<Integer> MODERATE_PRECIPITATION_CODES = List.of(61, 63, 66, 80, 81);

    private final WeatherService weatherService;

    public EventForecastResponse classify(EventRequest request) {
        List<HourlyForecast> forecasts = weatherService.fetchForecast(request);

        if (forecasts.isEmpty()) {
            return EventForecastResponse.builder()
                    .classification("Safe")
                    .summary("No hourly forecast data available for the event window")
                    .reason(List.of("No weather data found for the specified time range"))
                    .eventWindowForecast(forecasts)
                    .build();
        }

        List<String> unsafeReasons = new ArrayList<>();
        List<String> riskyReasons = new ArrayList<>();

        for (HourlyForecast forecast : forecasts) {
            int code = forecast.getWeatherCode();
            double wind = forecast.getWindKmh();
            int rainProb = forecast.getRainProb();

            if (THUNDERSTORM_CODES.contains(code)) {
                unsafeReasons.add("Thunderstorm forecast at " + forecast.getTime()
                        + " (weather code: " + code + ")");
            }
            if (HEAVY_PRECIPITATION_CODES.contains(code)) {
                unsafeReasons.add("Heavy precipitation forecast at " + forecast.getTime()
                        + " (weather code: " + code + ")");
            }
            if (wind > WIND_UNSAFE_THRESHOLD) {
                unsafeReasons.add("Dangerous wind speed of " + wind + " km/h at " + forecast.getTime());
            }

            if (MODERATE_PRECIPITATION_CODES.contains(code)) {
                riskyReasons.add("Moderate rain forecast at " + forecast.getTime()
                        + " (weather code: " + code + ")");
            }
            if (rainProb > RAIN_PROB_RISKY_THRESHOLD) {
                riskyReasons.add("Rain probability is " + rainProb + "% at " + forecast.getTime());
            }
            if (wind > WIND_RISKY_THRESHOLD && wind <= WIND_UNSAFE_THRESHOLD) {
                riskyReasons.add("Wind speed is " + wind + " km/h at " + forecast.getTime());
            }
        }

        String classification;
        List<String> reasons;
        String summary;

        if (!unsafeReasons.isEmpty()) {
            classification = "Unsafe";
            reasons = unsafeReasons;
            summary = "Severe weather conditions expected during the event. It is not safe to proceed.";
        } else if (!riskyReasons.isEmpty()) {
            classification = "Risky";
            reasons = riskyReasons;
            summary = "Weather conditions may impact the event. Proceed with caution.";
        } else {
            classification = "Safe";
            reasons = List.of("No adverse weather conditions detected during the event window");
            summary = "Weather conditions look favorable for the event.";
        }

        return EventForecastResponse.builder()
                .classification(classification)
                .summary(summary)
                .reason(reasons)
                .eventWindowForecast(forecasts)
                .build();
    }
}
