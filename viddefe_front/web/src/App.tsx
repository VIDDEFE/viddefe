import Router from './router'
import './App.css'
import 'leaflet/dist/leaflet.css'
import { Toaster } from 'sonner'
import { NotificationToast } from './components/shared'

function App() {
  return (
    <>
    <Toaster
        richColors 
        position="top-right"
      />
    <NotificationToast />
    <Router/>
    </>
  )
}

export default App
