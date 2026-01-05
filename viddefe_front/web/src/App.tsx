import Router from './router'
import './App.css'
import 'leaflet/dist/leaflet.css'
import { Toaster } from 'sonner'

function App() {
  return (
    <>
    <Toaster
        richColors 
        position="top-right"
      />
    <Router/>
    </>
  )
}

export default App
