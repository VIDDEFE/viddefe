import { useEffect, useRef, useState } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import type { ChurchSummary } from "../../models";
import { FiMapPin, FiUser, FiMap, FiX, FiExternalLink } from "react-icons/fi";

// Posición por defecto (Colombia)
const DEFAULT_CENTER = { lat: 4.1517, lng: -73.6386 };
const DEFAULT_ZOOM = 6;

// Crear un icono personalizado para las iglesias
const createChurchIcon = (isSelected: boolean = false) => {
  const color = isSelected ? '#059669' : '#3B82F6';
  const size = isSelected ? 40 : 32;
  
  return L.divIcon({
    className: 'custom-church-marker',
    html: `
      <div style="
        width: ${size}px;
        height: ${size}px;
        background: ${color};
        border-radius: 50% 50% 50% 0;
        transform: rotate(-45deg);
        border: 3px solid white;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.3s ease;
      ">
        <span style="
          transform: rotate(45deg);
          font-size: ${isSelected ? '18px' : '14px'};
        ">⛪</span>
      </div>
    `,
    iconSize: [size, size],
    iconAnchor: [size / 2, size],
    popupAnchor: [0, -size],
  });
};

interface ChurchesMapProps {
  churches: ChurchSummary[];
  height?: number;
  onChurchSelect?: (church: ChurchSummary) => void;
}

export default function ChurchesMap({ 
  churches, 
  height = 400,
  onChurchSelect 
}: ChurchesMapProps) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<L.Map | null>(null);
  const markersRef = useRef<Map<string, L.Marker>>(new Map());
  const circlesRef = useRef<Map<string, L.Circle>>(new Map()); // Nueva referencia para círculos
  const [selectedChurch, setSelectedChurch] = useState<ChurchSummary | null>(null);
  const [isCardVisible, setIsCardVisible] = useState(false);

  // ─────────────────────────────────────────────
  // Init map
  // ─────────────────────────────────────────────
  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const map = L.map(containerRef.current, {
      zoomControl: true,
      scrollWheelZoom: true,
    });
    mapRef.current = map;

    map.setView([DEFAULT_CENTER.lat, DEFAULT_CENTER.lng], DEFAULT_ZOOM);

    // Estilo de mapa más moderno
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);

    return () => {
      map.remove();
      mapRef.current = null;
      markersRef.current.clear();
      circlesRef.current.clear();
    };
  }, []);

  // ─────────────────────────────────────────────
  // Sync churches markers and circles
  // ─────────────────────────────────────────────
  useEffect(() => {
    if (!mapRef.current || !churches?.length) return;

    // Limpiar marcadores anteriores
    markersRef.current.forEach(marker => marker.remove());
    markersRef.current.clear();
    
    // Limpiar círculos anteriores
    circlesRef.current.forEach(circle => circle.remove());
    circlesRef.current.clear();

    // Crear bounds para ajustar el zoom
    const bounds = L.latLngBounds([]);

    churches.forEach((church) => {
      if (church.latitude && church.longitude) {
        const isSelected = selectedChurch?.id === church.id;
        
        // Crear marcador
        const marker = L.marker([church.latitude, church.longitude], {
          icon: createChurchIcon(isSelected),
        });

        marker.on('click', () => {
          handleChurchClick(church);
        });

        marker.addTo(mapRef.current!);
        markersRef.current.set(church.id, marker);
        
        // Crear círculo alrededor de la iglesia
        const circleColor = isSelected ? '#059669' : '#3B82F6';
        const circleOpacity = isSelected ? 0.35 : 0.3;
        const circleRadius = isSelected ? 1500 : 1000; // Radio en metros
        
        const circle = L.circle([church.latitude, church.longitude], {
          radius: circleRadius,
          color: circleColor,
          fillColor: circleColor,
          fillOpacity: circleOpacity,
          weight: isSelected ? 3 : 1.5,
          stroke: true,
          dashArray: isSelected ? null : '5, 10',
        }).addTo(mapRef.current!);
        
        circlesRef.current.set(church.id, circle);
        
        bounds.extend([church.latitude, church.longitude]);
      }
    });

    // Ajustar el mapa para mostrar todos los marcadores
    if (bounds.isValid()) {
      mapRef.current.fitBounds(bounds, { 
        padding: [50, 50],
        maxZoom: 14 
      });
    }
  }, [churches]);

  // ─────────────────────────────────────────────
  // Update selected marker icon and circle
  // ─────────────────────────────────────────────
  useEffect(() => {
    markersRef.current.forEach((marker, id) => {
      const isSelected = selectedChurch?.id === id;
      marker.setIcon(createChurchIcon(isSelected));
      
      // Actualizar círculo también
      const circle = circlesRef.current.get(id);
      if (circle) {
        const circleColor = isSelected ? '#059669' : '#3B82F6';
        const circleOpacity = isSelected ? 0.35 : 0.3;
        const circleRadius = isSelected ? 1000 : 500;
        
        circle.setStyle({
          color: circleColor,
          fillColor: circleColor,
          fillOpacity: circleOpacity,
          weight: isSelected ? 3 : 1.5,
          radius: circleRadius,
          dashArray: isSelected ? null : '5, 10',
        });
      }
    });
  }, [selectedChurch]);

  // ─────────────────────────────────────────────
  // Handle church click
  // ─────────────────────────────────────────────
  const handleChurchClick = (church: ChurchSummary) => {
    setSelectedChurch(church);
    setIsCardVisible(true);
    
    // Centrar mapa en la iglesia seleccionada
    if (mapRef.current && church.latitude && church.longitude) {
      mapRef.current.flyTo([church.latitude, church.longitude], 14, {
        duration: 0.8
      });
    }

    onChurchSelect?.(church);
  };

  const handleCloseCard = () => {
    setIsCardVisible(false);
    setTimeout(() => setSelectedChurch(null), 300);
  };

  const getPastorName = (church: ChurchSummary): string => {
    if (church.pastor && typeof church.pastor === 'object') {
      return `${church.pastor.firstName} ${church.pastor.lastName}`;
    }
    return 'Sin pastor asignado';
  };

  return (
    <div className="relative w-full rounded-xl overflow-hidden shadow-lg border border-neutral-200 z-0" style={{ height }}>
      {/* Mapa */}
      <div ref={containerRef} className="w-full h-full z-0" />

      {/* Contador de iglesias */}
      <div className="absolute top-4 left-4  bg-white/95 backdrop-blur-sm rounded-lg px-3 py-2 shadow-lg border border-neutral-200">
        <div className="flex items-center gap-2">
          <FiMapPin className="w-4 h-4 text-primary-600" />
          <span className="text-sm font-medium text-neutral-700">
            {churches?.length || 0} iglesia{churches?.length !== 1 ? 's' : ''}
          </span>
        </div>
      </div>

      {/* Leyenda */}
      <div className="absolute bottom-4 left-4  bg-white/95 backdrop-blur-sm rounded-lg px-3 py-2 shadow-lg border border-neutral-200">
        <div className="flex items-center gap-4 text-xs text-neutral-600">
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full bg-blue-500" />
            <span>Iglesia</span>
          </div>
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full bg-emerald-500" />
            <span>Seleccionada</span>
          </div>
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full border-2 border-blue-500 bg-indigo-100" />
            <span>Área de influencia</span>
          </div>
        </div>
      </div>

      {/* Card de información flotante */}
      <div 
        className={`absolute top-4 right-4 w-80 transition-all duration-300 ease-out ${
          isCardVisible && selectedChurch 
            ? 'opacity-100 translate-x-0' 
            : 'opacity-0 translate-x-4 pointer-events-none'
        }`}
      >
        {selectedChurch && (
          <div className="bg-white rounded-xl shadow-xl border border-neutral-200 overflow-hidden">
            {/* Header con gradiente */}
            <div className="bg-linear-to-r from-primary-600 to-indigo-600 px-4 py-3 text-white">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-2">
                  <span className="text-2xl">⛪</span>
                  <div>
                    <h3 className="font-bold text-lg leading-tight">{selectedChurch.name}</h3>
                    <p className="text-white/80 text-xs">
                      {selectedChurch.city?.name}, {selectedChurch.states?.name}
                    </p>
                  </div>
                </div>
                <button 
                  onClick={handleCloseCard}
                  className="p-1 hover:bg-white/20 rounded-full transition-colors"
                >
                  <FiX className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Contenido */}
            <div className="p-4 space-y-3">
              {/* Pastor */}
              <div className="flex items-center gap-3 p-2 bg-neutral-50 rounded-lg">
                <div className="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
                  <FiUser className="w-5 h-5 text-primary-600" />
                </div>
                <div>
                  <p className="text-xs text-neutral-500">Pastor</p>
                  <p className="font-medium text-neutral-800">{getPastorName(selectedChurch)}</p>
                </div>
              </div>

              {/* Ubicación */}
              <div className="flex items-center gap-3 p-2 bg-neutral-50 rounded-lg">
                <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                  <FiMap className="w-5 h-5 text-blue-600" />
                </div>
                <div>
                  <p className="text-xs text-neutral-500">Ubicación</p>
                  <p className="font-medium text-neutral-800 text-sm">
                    {selectedChurch.city?.name || 'Ciudad no especificada'}
                  </p>
                  <p className="text-xs text-neutral-500">
                    {selectedChurch.states?.name || 'Departamento no especificado'}
                  </p>
                </div>
              </div>

              {/* Información del círculo */}
              <div className="p-3 bg-blue-50 rounded-lg border border-blue-100">
                <div className="flex items-center gap-2 mb-2">
                  <span className="w-3 h-3 rounded-full bg-blue-500"></span>
                  <p className="text-sm font-medium text-blue-800">Área de influencia</p>
                </div>
                <p className="text-xs text-blue-600">
                  Radio aproximado: {selectedChurch.id === selectedChurch?.id ? '1.5 km' : '1 km'} alrededor de la iglesia
                </p>
              </div>

              {/* Coordenadas */}
              <div className="text-xs text-neutral-400 flex items-center justify-between px-2">
                <span>📍 {selectedChurch.latitude?.toFixed(4)}, {selectedChurch.longitude?.toFixed(4)}</span>
                <button 
                  onClick={() => {
                    const url = `https://www.google.com/maps?q=${selectedChurch.latitude},${selectedChurch.longitude}`;
                    window.open(url, '_blank');
                  }}
                  className="flex items-center gap-1 text-primary-600 hover:text-primary-700 transition-colors"
                >
                  <FiExternalLink className="w-3 h-3" />
                  <span>Google Maps</span>
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Mensaje cuando no hay iglesias */}
      {(!churches || churches.length === 0) && (
        <div className="absolute inset-0 flex items-center justify-center bg-neutral-50/80 z-0">
          <div className="text-center p-6">
            <span className="text-5xl mb-4 block">🗺️</span>
            <h3 className="text-lg font-semibold text-neutral-700 mb-2">Sin iglesias para mostrar</h3>
            <p className="text-sm text-neutral-500">No hay iglesias hijas registradas aún</p>
          </div>
        </div>
      )}

      {/* Estilos para los marcadores */}
      <style>{`
        .custom-church-marker {
          background: transparent !important;
          border: none !important;
        }
        .custom-church-marker:hover > div {
          transform: rotate(-45deg) scale(1.1);
        }
        
        /* Asegurar que los círculos estén debajo de los marcadores */
        .leaflet-overlay-pane {
          z-index: 400 !important;
        }
        .leaflet-marker-pane {
          z-index: 600 !important;
        }
      `}</style>
    </div>
  );
}