import { useEffect, useMemo, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import type { HomeGroup } from '../../models';
import { FiEdit2, FiTrash2, FiMapPin, FiUsers } from 'react-icons/fi';

interface GroupsMapProps {
  groups: HomeGroup[];
  height?: number;
  onGroupSelect?: (group: HomeGroup | null) => void;
  onEditGroup?: (group: HomeGroup) => void;
  onDeleteGroup?: (group: HomeGroup) => void;
}

// Crear un ícono personalizado para grupos de hogar
const createGroupIcon = (isSelected: boolean = false) => {
  const color = isSelected ? '#3B82F6' : '#8B5CF6'; // Azul si seleccionado, violeta por defecto
  const size = isSelected ? 36 : 30;

  return L.divIcon({
    className: 'custom-group-icon',
    html: `
      <div style="
        width: ${size}px;
        height: ${size}px;
        background-color: ${color};
        border: 3px solid white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 3px 8px rgba(0,0,0,0.3);
        transition: all 0.2s ease;
      ">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white" width="${size - 12}px" height="${size - 12}px">
          <path d="M12 5.5A3.5 3.5 0 0 1 15.5 9a3.5 3.5 0 0 1-3.5 3.5A3.5 3.5 0 0 1 8.5 9A3.5 3.5 0 0 1 12 5.5M5 8c.56 0 1.08.15 1.53.42c-.15 1.43.27 2.85 1.13 3.96C7.16 13.34 6.16 14 5 14a3 3 0 0 1-3-3a3 3 0 0 1 3-3m14 0a3 3 0 0 1 3 3a3 3 0 0 1-3 3c-1.16 0-2.16-.66-2.66-1.62a5.536 5.536 0 0 0 1.13-3.96c.45-.27.97-.42 1.53-.42M5.5 18.25c0-2.07 2.91-3.75 6.5-3.75s6.5 1.68 6.5 3.75V20h-13v-1.75M0 20v-1.5c0-1.39 1.89-2.56 4.45-2.9c-.59.68-.95 1.62-.95 2.65V20H0m24 0h-3.5v-1.75c0-1.03-.36-1.97-.95-2.65c2.56.34 4.45 1.51 4.45 2.9V20Z"/>
        </svg>
      </div>
    `,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2],
  });
};

export default function GroupsMap({
  groups,
  height = 500,
  onGroupSelect,
  onEditGroup,
  onDeleteGroup,
}: GroupsMapProps) {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapRef = useRef<L.Map | null>(null);
  const markersRef = useRef<Map<string, L.Marker>>(new Map());
  const circlesRef = useRef<Map<string, L.Circle>>(new Map());
  const [selectedGroup, setSelectedGroup] = useState<HomeGroup | null>(null);

  // Filtrar grupos con coordenadas válidas
  const validGroups = useMemo(
    () => groups.filter((g) => g.latitude && g.longitude),
    [groups]
  );

  // Calcular centro del mapa
  const mapCenter = useMemo(() => {
    if (validGroups.length === 0) {
      // Centro predeterminado (Colombia)
      return { lat: 4.6097, lng: -74.0817 };
    }

    const latSum = validGroups.reduce((sum, g) => sum + g.latitude, 0);
    const lngSum = validGroups.reduce((sum, g) => sum + g.longitude, 0);

    return {
      lat: latSum / validGroups.length,
      lng: lngSum / validGroups.length,
    };
  }, [validGroups]);

  // Inicializar mapa
  useEffect(() => {
    if (!mapContainerRef.current || mapRef.current) return;

    mapRef.current = L.map(mapContainerRef.current, {
      center: [mapCenter.lat, mapCenter.lng],
      zoom: 12,
      zoomControl: true,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(mapRef.current);

    return () => {
      if (mapRef.current) {
        mapRef.current.remove();
        mapRef.current = null;
      }
    };
  }, [mapCenter.lat, mapCenter.lng]);

  // Actualizar marcadores cuando cambian los grupos
  useEffect(() => {
    if (!mapRef.current) return;

    // Limpiar marcadores y círculos existentes
    markersRef.current.forEach((marker) => marker.remove());
    markersRef.current.clear();
    circlesRef.current.forEach((circle) => circle.remove());
    circlesRef.current.clear();

    // Crear nuevos marcadores y círculos
    validGroups.forEach((group) => {
      const isSelected = selectedGroup?.id === group.id;

      // Crear círculo de cobertura
      const circle = L.circle([group.latitude, group.longitude], {
        radius: 1000, // Radio de 1000 metros
        color: isSelected ? '#3B82F6' : '#8B5CF6',
        fillColor: isSelected ? '#3B82F6' : '#8B5CF6',
        fillOpacity: 0.15,
        weight: 2,
      }).addTo(mapRef.current!);

      circlesRef.current.set(group.id, circle);

      // Crear marcador
      const marker = L.marker([group.latitude, group.longitude], {
        icon: createGroupIcon(isSelected),
      })
        .addTo(mapRef.current!)
        .on('click', () => {
          setSelectedGroup(group);
          onGroupSelect?.(group);

          // Animar hacia el grupo seleccionado
          mapRef.current?.flyTo([group.latitude, group.longitude], 14, {
            duration: 0.5,
          });
        });

      markersRef.current.set(group.id, marker);
    });

    // Ajustar vista si hay grupos
    if (validGroups.length > 0 && !selectedGroup) {
      const bounds = L.latLngBounds(
        validGroups.map((g) => [g.latitude, g.longitude] as [number, number])
      );
      mapRef.current.fitBounds(bounds, { padding: [50, 50], maxZoom: 14 });
    }
  }, [validGroups, selectedGroup, onGroupSelect]);

  const handleCloseCard = () => {
    setSelectedGroup(null);
    onGroupSelect?.(null);
  };

  const openInGoogleMaps = () => {
    if (!selectedGroup) return;
    const url = `https://www.google.com/maps?q=${selectedGroup.latitude},${selectedGroup.longitude}`;
    window.open(url, '_blank');
  };

  return (
    <div className="relative rounded-lg overflow-hidden shadow-md border border-neutral-200 z-0">
      {/* Contenedor del mapa */}
      <div ref={mapContainerRef} style={{ height: `${height}px`, width: '100%' }} />

      {/* Contador de grupos */}
      <div className="absolute top-4 left-4 bg-white rounded-lg shadow-lg px-4 py-2 z-1000">
        <div className="flex items-center gap-2">
          <FiUsers className="w-5 h-5 text-violet-600" />
          <span className="text-sm font-medium text-neutral-700">
            {validGroups.length} grupo{validGroups.length !== 1 ? 's' : ''} en el mapa
          </span>
        </div>
      </div>

      {/* Leyenda */}
      <div className="absolute bottom-4 left-4 bg-white rounded-lg shadow-lg px-4 py-3 z-1000">
        <h4 className="text-xs font-semibold text-neutral-600 mb-2 uppercase tracking-wider">
          Leyenda
        </h4>
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded-full bg-violet-500 border-2 border-white shadow"></div>
            <span className="text-xs text-neutral-600">Grupo de Hogar</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded-full bg-blue-500 border-2 border-white shadow"></div>
            <span className="text-xs text-neutral-600">Grupo Seleccionado</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-4 h-4 rounded-full bg-violet-500/20 border border-violet-500"></div>
            <span className="text-xs text-neutral-600">Área de cobertura</span>
          </div>
        </div>
      </div>

      {/* Tarjeta de información del grupo seleccionado */}
      {selectedGroup && (
        <div className="absolute top-4 right-4 bg-white rounded-lg shadow-xl z-1000 w-80 overflow-hidden animate-in fade-in slide-in-from-right-4 duration-200">
          {/* Header */}
          <div className="bg-linear-to-r from-violet-600 to-violet-700 px-4 py-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
                  <FiUsers className="w-5 h-5 text-white" />
                </div>
                <h3 className="text-white font-semibold truncate max-w-45">
                  {selectedGroup.name}
                </h3>
              </div>
              <button
                onClick={handleCloseCard}
                className="text-white/80 hover:text-white transition-colors p-1 hover:bg-white/10 rounded"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-5 w-5"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fillRule="evenodd"
                    d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                    clipRule="evenodd"
                  />
                </svg>
              </button>
            </div>
          </div>

          {/* Content */}
          <div className="p-4 space-y-3">
            {/* Estrategia */}
            {selectedGroup.strategy && (
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Estrategia
                </span>
                <p className="text-sm text-neutral-800 mt-1">
                  <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-violet-100 text-violet-800">
                    {selectedGroup.strategy.name}
                  </span>
                </p>
              </div>
            )}

            {/* Líder */}
            {selectedGroup.leader && (
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Líder
                </span>
                <p className="text-sm text-neutral-800 mt-1 flex items-center gap-2">
                  <span className="w-6 h-6 bg-violet-100 rounded-full flex items-center justify-center text-xs font-semibold text-violet-700">
                    {selectedGroup.leader.firstName[0]}
                    {selectedGroup.leader.lastName[0]}
                  </span>
                  {selectedGroup.leader.firstName} {selectedGroup.leader.lastName}
                </p>
              </div>
            )}

            {/* Descripción */}
            {selectedGroup.description && (
              <div>
                <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Descripción
                </span>
                <p className="text-sm text-neutral-600 mt-1 line-clamp-2">
                  {selectedGroup.description}
                </p>
              </div>
            )}

            {/* Coordenadas */}
            <div>
              <span className="text-xs font-medium text-neutral-500 uppercase tracking-wider">
                Ubicación
              </span>
              <p className="text-xs text-neutral-500 mt-1 font-mono">
                {selectedGroup.latitude.toFixed(6)}, {selectedGroup.longitude.toFixed(6)}
              </p>
            </div>

            <div className="flex items-center gap-2 pt-2 border-t border-neutral-100">
              <button
                onClick={openInGoogleMaps}
                className="flex-1 flex items-center justify-center gap-1.5 px-3 py-2 bg-neutral-100 hover:bg-neutral-200 text-neutral-700 text-sm rounded-lg transition-colors"
              >
                <FiMapPin className="w-4 h-4" />
                <span>Google Maps</span>
              </button>

              {onEditGroup && (
                <button
                  onClick={() => onEditGroup(selectedGroup)}
                  className="flex items-center justify-center gap-1.5 px-3 py-2 bg-violet-100 hover:bg-violet-200 text-violet-700 text-sm rounded-lg transition-colors"
                  title="Editar grupo"
                >
                  <FiEdit2 className="w-4 h-4" />
                </button>
              )}

              {onDeleteGroup && (
                <button
                  onClick={() => onDeleteGroup(selectedGroup)}
                  className="flex items-center justify-center gap-1.5 px-3 py-2 bg-red-100 hover:bg-red-200 text-red-700 text-sm rounded-lg transition-colors"
                  title="Eliminar grupo"
                >
                  <FiTrash2 className="w-4 h-4" />
                </button>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Mensaje si no hay grupos */}
      {validGroups.length === 0 && (
        <div className="absolute inset-0 flex items-center justify-center bg-neutral-100/80 z-1000">
          <div className="text-center px-6 py-8 bg-white rounded-xl shadow-lg">
            <FiUsers className="w-16 h-16 text-neutral-300 mx-auto mb-4" />
            <h3 className="text-lg font-semibold text-neutral-700 mb-2">
              No hay grupos para mostrar
            </h3>
            <p className="text-sm text-neutral-500">
              Crea un grupo con ubicación para verlo en el mapa
            </p>
          </div>
        </div>
      )}

      {/* Estilos para el ícono personalizado */}
      <style>{`
        .custom-group-icon {
          background: transparent !important;
          border: none !important;
        }
      `}</style>
    </div>
  );
}
