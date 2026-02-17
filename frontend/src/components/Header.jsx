import { CloudRain } from 'lucide-react'

export default function Header() {
  return (
    <div className="text-center mb-8">
      <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-indigo-500/15 border border-indigo-500/20 mb-4">
        <CloudRain className="w-7 h-7 text-indigo-400" />
      </div>
      <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-white">
        Event Weather Guard
      </h1>
      <p className="mt-2 text-sm text-white/40">
        Will it rain during your event?
      </p>
    </div>
  )
}
