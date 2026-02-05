import { useEffect, useRef, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import type { ChurchSummary } from "../../models";
import type { MapBounds } from "../../services/churchService";
import { useNearbyChurches } from "../../hooks/useChurches";
import { FiMapPin, FiUser, FiMap, FiX, FiExternalLink, FiLoader, FiRefreshCw, FiEye } from "react-icons/fi";

// Posición por defecto (Colombia)
const DEFAULT_CENTER = { lat: 4.1517, lng: -73.6386 };
const DEFAULT_ZOOM = 12;

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
  churchId: string; // ID de la iglesia padre para buscar hijas cercanas
  height?: number;
  onChurchSelect?: (church: ChurchSummary) => void;
}

export default function ChurchesMap({ 
  churchId,
  height = 400,
  onChurchSelect 
}: Readonly<ChurchesMapProps>) {
  const navigate = useNavigate();
  const containerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<L.Map | null>(null);
  const markersRef = useRef<Map<string, L.Marker>>(new Map());
  const circlesRef = useRef<Map<string, L.Circle>>(new Map());
  const [selectedChurch, setSelectedChurch] = useState<ChurchSummary | null>(null);
  const [isCardVisible, setIsCardVisible] = useState(false);
  const [mapBounds, setMapBounds] = useState<MapBounds | null>(null);
  const initialBoundsSet = useRef(false);

  // Hook para obtener iglesias cercanas según los bounds del mapa
  const { 
    data: churches = [], 
    isFetching,
    refetch,
    error 
  } = useNearbyChurches(churchId, mapBounds);

  // Detectar si el error es por zoom muy lejano
  const isZoomTooLarge = error?.message?.toLowerCase().includes('zoom') || 
                         error?.message?.toLowerCase().includes('large');

  // Función para actualizar los bounds
  const updateBounds = useCallback(() => {
    if (!mapRef.current) return;
    
    const bounds = mapRef.current.getBounds();
    const newBounds: MapBounds = {
      southLat: bounds.getSouth(),
      westLng: bounds.getWest(),
      northLat: bounds.getNorth(),
      eastLng: bounds.getEast(),
    };
    
    setMapBounds(newBounds);
  }, []);

  // Debounce para evitar muchas llamadas al mover el mapa
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const debouncedUpdateBounds = useCallback(() => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current);
    }
    debounceRef.current = setTimeout(() => {
      updateBounds();
    }, 500); // 500ms de debounce
  }, [updateBounds]);

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

    // Capturar bounds iniciales
    map.whenReady(() => {
      updateBounds();
      initialBoundsSet.current = true;
    });

    // Actualizar bounds cuando el mapa se mueve o hace zoom
    map.on('moveend', debouncedUpdateBounds);
    map.on('zoomend', debouncedUpdateBounds);

    return () => {
      if (debounceRef.current) {
        clearTimeout(debounceRef.current);
      }
      map.off('moveend', debouncedUpdateBounds);
      map.off('zoomend', debouncedUpdateBounds);
      map.remove();
      mapRef.current = null;
      markersRef.current.clear();
      circlesRef.current.clear();
    };
  }, [debouncedUpdateBounds, updateBounds]);

  // ─────────────────────────────────────────────
  // Sync churches markers and circles
  // ─────────────────────────────────────────────
  useEffect(() => {
    if (!mapRef.current) return;

    // Limpiar marcadores anteriores
    markersRef.current.forEach(marker => marker.remove());
    markersRef.current.clear();
    
    // Limpiar círculos anteriores
    circlesRef.current.forEach(circle => circle.remove());
    circlesRef.current.clear();

    if (!churches?.length) return;

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
          dashArray: isSelected ? undefined : '5, 10',
        }).addTo(mapRef.current!);
        
        circlesRef.current.set(church.id, circle);
      }
    });
  }, [churches, selectedChurch?.id]);

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
        
        circle.setStyle({
          color: circleColor,
          fillColor: circleColor,
          fillOpacity: circleOpacity,
          weight: isSelected ? 3 : 1.5,
          dashArray: isSelected ? undefined : '5, 10',
        });
        circle.setRadius(isSelected ? 1500 : 1000);
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

      {/* Contador de iglesias y estado de carga */}
      <div className="absolute top-4 left-4 bg-white/95 backdrop-blur-sm rounded-lg px-3 py-2 shadow-lg border border-neutral-200">
        <div className="flex items-center gap-2">
          {isFetching ? (
            <FiLoader className="w-4 h-4 text-primary-600 animate-spin" />
          ) : isZoomTooLarge ? (
            <FiMapPin className="w-4 h-4 text-amber-500" />
          ) : (
            <FiMapPin className="w-4 h-4 text-primary-600" />
          )}
          <span className={`text-sm font-medium ${isZoomTooLarge ? 'text-amber-600' : 'text-neutral-700'}`}>
            {isZoomTooLarge 
              ? '🔍 Acerca el mapa para ver iglesias' 
              : `${churches?.length || 0} iglesia${churches?.length !== 1 ? 's' : ''}`
            }
          </span>
          {!isZoomTooLarge && (
            <button
              onClick={() => refetch()}
              disabled={isFetching}
              className="ml-2 p-1 hover:bg-neutral-100 rounded transition-colors disabled:opacity-50"
              title="Recargar"
            >
              <FiRefreshCw className={`w-3.5 h-3.5 text-neutral-500 ${isFetching ? 'animate-spin' : ''}`} />
            </button>
          )}
        </div>
      </div>

      {/* Indicador de zona de búsqueda */}
      <div className="absolute top-4 left-1/2 -translate-x-1/2 bg-white/95 backdrop-blur-sm rounded-lg px-3 py-1.5 shadow-lg border border-neutral-200">
        <p className="text-xs text-neutral-500">
          📍 Mueve el mapa para buscar en otras zonas
        </p>
      </div>

      {/* Leyenda */}
      <div className="absolute bottom-4 left-4 bg-white/95 backdrop-blur-sm rounded-lg px-3 py-2 shadow-lg border border-neutral-200">
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
                  Radio aproximado: 1.5 km alrededor de la iglesia
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

              {/* Botón Ver más */}
              <button
                onClick={() => navigate(`/churches/${selectedChurch.id}`)}
                className="w-full mt-2 py-2.5 px-4 bg-primary-600 hover:bg-primary-700 text-white rounded-lg flex items-center justify-center gap-2 transition-colors font-medium text-sm"
              >
                <FiEye className="w-4 h-4" />
                Ver más información
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Indicador de carga pequeño */}
      {isFetching && (
        <div className="absolute bottom-4 right-4 bg-white/95 backdrop-blur-sm rounded-lg px-3 py-2 shadow-lg border border-neutral-200 z-10">
          <div className="flex items-center gap-2">
            <FiLoader className="w-4 h-4 text-primary-600 animate-spin" />
            <span className="text-xs text-neutral-600">Buscando...</span>
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