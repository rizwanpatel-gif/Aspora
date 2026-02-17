# Aspora

Weather risk assessment API for outdoor events. Takes an event's location and time window, fetches forecast data, and classifies whether it's **Safe**, **Risky**, or **Unsafe** to proceed.

## Tech Stack

**Backend:** Java 17, Spring Boot 3.4, RestClient, Lombok, Maven
**Frontend:** React, Vite, Tailwind CSS
**Weather Data:** [OpenMeteo API](https://open-meteo.com) (free, no API key)
**Docs:** Swagger UI at `/swagger-ui.html`

## Getting Started

```bash
git clone https://github.com/rizwanpatel-gif/Aspora.git
cd Aspora
```

**Backend** (requires Java 17+):
```bash
.\mvnw.ps1 spring-boot:run
```

**Frontend** (requires Node 18+):
```bash
cd frontend
npm install
npm run dev
```

Backend runs on `http://localhost:8080`, frontend on `http://localhost:3000`.

**Tests:**
```bash
.\mvnw.ps1 test
```

## API

### `POST /event-forecast`

```json
{
  "name": "Cricket Match",
  "location": {
    "latitude": 19.076,
    "longitude": 72.8777
  },
  "start_time": "2026-03-15T14:00:00",
  "end_time": "2026-03-15T18:00:00"
}
```

**Response:**

```json
{
  "classification": "Risky",
  "summary": "Weather conditions may impact the event. Proceed with caution.",
  "reason": ["Rain probability is 70% at 15:00"],
  "event_window_forecast": [
    { "time": "14:00", "rain_prob": 40, "wind_kmh": 12, "temperature_c": 32, "weather_code": 3 },
    { "time": "15:00", "rain_prob": 70, "wind_kmh": 18, "temperature_c": 30, "weather_code": 61 }
  ]
}
```

## Classification Rules

| Classification | Condition |
|---|---|
| **Unsafe** | Thunderstorm (WMO 95, 96, 99), heavy rain (WMO 65, 67, 75, 77, 82, 85, 86), or wind > 50 km/h |
| **Risky** | Rain probability > 60%, moderate rain (WMO 61, 63, 66, 80, 81), or wind 30–50 km/h |
| **Safe** | None of the above |

Rules are deterministic and use [WMO weather codes](https://open-meteo.com/en/docs). Worst-case takes precedence — if both Unsafe and Risky conditions exist, the event is classified as Unsafe.

## Assumptions

- OpenMeteo forecasts are available up to 16 days ahead. Events beyond that range return limited data.
- `timezone=auto` is used so forecast times match the event's local timezone.
- No persistence — each request fetches fresh forecast data.
- Hourly granularity — sub-hour precision is not available from the API.

## Deployment

| Service | Platform | Config |
|---|---|---|
| Backend | Render (Docker) | Set `CORS_ALLOWED_ORIGINS` env var to your frontend URL |
| Frontend | Vercel | Set root directory to `frontend`, add `VITE_API_URL` env var pointing to backend |
