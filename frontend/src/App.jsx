import { useState } from 'react'
import EventForm from './components/EventForm'
import ResultCard from './components/ResultCard'
import Header from './components/Header'

function App() {
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleSubmit = async (formData) => {
    setLoading(true)
    setError(null)
    setResult(null)

    try {
      const baseUrl = import.meta.env.VITE_API_URL || ''
      const res = await fetch(`${baseUrl}/event-forecast`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      })

      if (!res.ok) {
        const err = await res.json()
        throw new Error(err.error || 'Something went wrong')
      }

      const data = await res.json()
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="relative min-h-dvh overflow-hidden">

      <div className="bg-orb bg-orb-1" />
      <div className="bg-orb bg-orb-2" />
      <div className="bg-orb bg-orb-3" />

      <div className="relative z-10 mx-auto max-w-lg px-4 py-8 sm:py-12">
        <Header />

        <EventForm onSubmit={handleSubmit} loading={loading} />

        {error && (
          <div className="animate-slide-up mt-6 glass-strong p-4 border-l-4 border-red-500/60">
            <p className="text-red-300 text-sm">{error}</p>
          </div>
        )}

        {result && <ResultCard data={result} />}
      </div>
    </div>
  )
}

export default App
