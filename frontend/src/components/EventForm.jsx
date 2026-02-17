import { useState, useRef, useEffect } from 'react'
import { Search, MapPin, Calendar, Loader2, X } from 'lucide-react'

export default function EventForm({ onSubmit, loading }) {
  const [form, setForm] = useState({
    name: '',
    latitude: '',
    longitude: '',
    start_time: '',
    end_time: '',
  })

  const [locationQuery, setLocationQuery] = useState('')
  const [suggestions, setSuggestions] = useState([])
  const [showSuggestions, setShowSuggestions] = useState(false)
  const [searching, setSearching] = useState(false)
  const [selectedCity, setSelectedCity] = useState('')
  const debounceRef = useRef(null)
  const wrapperRef = useRef(null)

  useEffect(() => {
    const handleClick = (e) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setShowSuggestions(false)
      }
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [])

  const searchLocation = (query) => {
    setLocationQuery(query)
    setSelectedCity('')

    if (debounceRef.current) clearTimeout(debounceRef.current)

    if (query.length < 2) {
      setSuggestions([])
      setShowSuggestions(false)
      return
    }

    debounceRef.current = setTimeout(async () => {
      setSearching(true)
      try {
        const res = await fetch(
          `https://geocoding-api.open-meteo.com/v1/search?name=${encodeURIComponent(query)}&count=5&language=en`
        )
        const data = await res.json()
        setSuggestions(data.results || [])
        setShowSuggestions(true)
      } catch {
        setSuggestions([])
      } finally {
        setSearching(false)
      }
    }, 300)
  }

  const selectLocation = (place) => {
    const label = [place.name, place.admin1, place.country].filter(Boolean).join(', ')
    setSelectedCity(label)
    setLocationQuery(label)
    setForm((prev) => ({
      ...prev,
      latitude: place.latitude.toString(),
      longitude: place.longitude.toString(),
    }))
    setSuggestions([])
    setShowSuggestions(false)
  }

  const clearLocation = () => {
    setLocationQuery('')
    setSelectedCity('')
    setForm((prev) => ({ ...prev, latitude: '', longitude: '' }))
    setSuggestions([])
  }

  const update = (field) => (e) =>
    setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = (e) => {
    e.preventDefault()
    onSubmit({
      name: form.name,
      location: {
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude),
      },
      start_time: form.start_time + ':00',
      end_time: form.end_time + ':00',
    })
  }

  const isValid =
    form.name && form.latitude && form.longitude && form.start_time && form.end_time

  return (
    <form onSubmit={handleSubmit} className={`glass p-6 sm:p-8 ${loading ? 'loading-pulse' : ''}`}>
      <div className="mb-5">
        <label className="flex items-center gap-2 text-xs font-medium text-white/50 uppercase tracking-wider mb-2">
          <Calendar className="w-3.5 h-3.5" />
          Event Name
        </label>
        <input
          type="text"
          placeholder="e.g. Football Match"
          value={form.name}
          onChange={update('name')}
          required
        />
      </div>

      <div className="mb-5">
        <label className="flex items-center gap-2 text-xs font-medium text-white/50 uppercase tracking-wider mb-2">
          <MapPin className="w-3.5 h-3.5" />
          Location
        </label>

        <div className="relative mb-3" ref={wrapperRef}>
          <div className="relative">
            <input
              type="text"
              placeholder="Search city... e.g. Mumbai, London"
              value={locationQuery}
              onChange={(e) => searchLocation(e.target.value)}
              onFocus={() => suggestions.length > 0 && setShowSuggestions(true)}
            />
            {searching && (
              <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-white/30 animate-spin" />
            )}
            {selectedCity && !searching && (
              <button
                type="button"
                onClick={clearLocation}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-white/30 hover:text-white/60 transition-colors cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          {showSuggestions && suggestions.length > 0 && (
            <div className="absolute z-20 w-full mt-1 rounded-2xl overflow-hidden bg-[#1e1b4b] border border-indigo-500/30 shadow-lg shadow-indigo-500/10">
              {suggestions.map((place) => (
                <button
                  key={place.id}
                  type="button"
                  onClick={() => selectLocation(place)}
                  className="w-full text-left px-4 py-3 hover:bg-indigo-500/20 transition-colors cursor-pointer border-b border-indigo-500/10 last:border-0"
                >
                  <div className="text-sm text-white/90">{place.name}</div>
                  <div className="text-xs text-white/40">
                    {[place.admin1, place.country].filter(Boolean).join(', ')}
                    <span className="ml-2 text-white/20">
                      {place.latitude.toFixed(2)}, {place.longitude.toFixed(2)}
                    </span>
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="grid grid-cols-2 gap-3">
          <input
            type="number"
            step="any"
            placeholder="Latitude"
            value={form.latitude}
            onChange={update('latitude')}
            required
          />
          <input
            type="number"
            step="any"
            placeholder="Longitude"
            value={form.longitude}
            onChange={update('longitude')}
            required
          />
        </div>
        {selectedCity && (
          <p className="text-xs text-indigo-400/70 mt-1.5 flex items-center gap-1">
            <MapPin className="w-3 h-3" />
            {selectedCity}
          </p>
        )}
      </div>

      <div className="mb-6">
        <label className="flex items-center gap-2 text-xs font-medium text-white/50 uppercase tracking-wider mb-2">
          <Calendar className="w-3.5 h-3.5" />
          Event Window
        </label>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <span className="text-[11px] text-white/30 mb-1 block">Start</span>
            <input
              type="datetime-local"
              value={form.start_time}
              onChange={update('start_time')}
              required
            />
          </div>
          <div>
            <span className="text-[11px] text-white/30 mb-1 block">End</span>
            <input
              type="datetime-local"
              value={form.end_time}
              onChange={update('end_time')}
              required
            />
          </div>
        </div>
      </div>

      <button
        type="submit"
        disabled={!isValid || loading}
        className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl font-semibold text-sm
          bg-indigo-500 hover:bg-indigo-400 text-white
          disabled:opacity-30 disabled:cursor-not-allowed
          transition-all duration-200 cursor-pointer"
      >
        {loading ? (
          <>
            <Loader2 className="w-4 h-4 animate-spin" />
            Analyzing Weather...
          </>
        ) : (
          <>
            <Search className="w-4 h-4" />
            Check Weather Risk
          </>
        )}
      </button>
    </form>
  )
}
