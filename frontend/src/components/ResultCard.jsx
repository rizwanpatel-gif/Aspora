import { ShieldCheck, ShieldAlert, ShieldX, Droplets, Wind, Thermometer } from 'lucide-react'

const classificationConfig = {
  Safe: {
    icon: ShieldCheck,
    color: 'text-emerald-400',
    bg: 'bg-emerald-500/10',
    border: 'border-emerald-500/20',
    badge: 'bg-emerald-500/20 text-emerald-300',
  },
  Risky: {
    icon: ShieldAlert,
    color: 'text-amber-400',
    bg: 'bg-amber-500/10',
    border: 'border-amber-500/20',
    badge: 'bg-amber-500/20 text-amber-300',
  },
  Unsafe: {
    icon: ShieldX,
    color: 'text-red-400',
    bg: 'bg-red-500/10',
    border: 'border-red-500/20',
    badge: 'bg-red-500/20 text-red-300',
  },
}

export default function ResultCard({ data }) {
  const config = classificationConfig[data.classification] || classificationConfig.Safe
  const Icon = config.icon

  return (
    <div className="animate-slide-up mt-6 space-y-4">
      <div className={`glass-strong p-5 ${config.border} border`}>
        <div className="flex items-center gap-3 mb-3">
          <div className={`p-2.5 rounded-xl ${config.bg}`}>
            <Icon className={`w-6 h-6 ${config.color}`} />
          </div>
          <div>
            <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${config.badge}`}>
              {data.classification}
            </span>
            <p className="text-white/80 text-sm mt-1">{data.summary}</p>
          </div>
        </div>

        {data.reason?.length > 0 && (
          <div className="mt-4 space-y-2">
            {data.reason.map((r, i) => (
              <div key={i} className="flex items-start gap-2 text-sm text-white/60">
                <span className={`mt-1.5 w-1.5 h-1.5 rounded-full shrink-0 ${config.color.replace('text-', 'bg-')}`} />
                {r}
              </div>
            ))}
          </div>
        )}
      </div>

      {data.event_window_forecast?.length > 0 && (
        <div className="glass-strong p-5">
          <h3 className="text-xs font-medium text-white/40 uppercase tracking-wider mb-3">
            Hourly Breakdown
          </h3>
          <div className="space-y-2">
            {data.event_window_forecast.map((hour, i) => (
              <HourRow key={i} hour={hour} />
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

function HourRow({ hour }) {
  const rainColor =
    hour.rain_prob > 60
      ? 'text-amber-400'
      : hour.rain_prob > 30
        ? 'text-blue-300'
        : 'text-white/50'

  return (
    <div className="flex items-center gap-3 py-2.5 px-3 rounded-xl bg-white/[0.03] hover:bg-white/[0.06] transition-colors">
      <span className="text-sm font-semibold text-white/80 w-14 shrink-0">
        {hour.time}
      </span>

      <div className="flex items-center gap-4 flex-1 text-xs">
        <div className={`flex items-center gap-1 ${rainColor}`}>
          <Droplets className="w-3.5 h-3.5" />
          {hour.rain_prob}%
        </div>
        <div className="flex items-center gap-1 text-white/50">
          <Wind className="w-3.5 h-3.5" />
          {hour.wind_kmh} km/h
        </div>
        {hour.temperature_c !== undefined && (
          <div className="flex items-center gap-1 text-white/50">
            <Thermometer className="w-3.5 h-3.5" />
            {hour.temperature_c}°C
          </div>
        )}
      </div>

      <div className="w-16 h-1.5 rounded-full bg-white/5 shrink-0 overflow-hidden">
        <div
          className={`h-full rounded-full transition-all duration-500 ${
            hour.rain_prob > 60
              ? 'bg-amber-400'
              : hour.rain_prob > 30
                ? 'bg-blue-400'
                : 'bg-white/20'
          }`}
          style={{ width: `${hour.rain_prob}%` }}
        />
      </div>
    </div>
  )
}
