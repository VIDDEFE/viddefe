import { useEffect, useRef } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

export type Position = { lat: number; lng: number } | null;
type Mode = 'operate' | 'view';

const DEFAULT_POSITION: Position = { lat: 4.1517, lng: -73.6386 };

// Icono estándar para 'operate'
const defaultIcon = new L.Icon({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});

type Props = {
  position?: Position;
  onChange: (p: Position) => void;
  height?: number;
  isLoading?: boolean;
  mode?: Mode;
};

export default function MapPicker({
  position = null,
  onChange,
  height = 300,
  isLoading = false,
  mode = 'view',
}: Props) {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<L.Map | null>(null);
  const markerRef = useRef<L.Marker | null>(null);
  const circleRef = useRef<L.Circle | null>(null);
  const smallCircleRef = useRef<L.Circle | null>(null);
  const mapReadyRef = useRef(false);

  // ─────────────────────────────────────────────
  // Init map (ONCE)
  // ─────────────────────────────────────────────
  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const map = L.map(containerRef.current);
    mapRef.current = map;

    const initial = position ?? DEFAULT_POSITION;
    map.setView([initial?.lat, initial?.lng], position ? 14 : 12);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);

    map.whenReady(() => {
      mapReadyRef.current = true;
      if (position) createOrUpdateCircle(position);
      if (position && mode === 'operate') createOrUpdateMarker(position);
    });

    // Solo hacer clickable si estamos en modo 'operate'
    if (mode === 'operate') {
      map.on("click", (e: L.LeafletMouseEvent) => {
        const pos: Position = { lat: e.latlng.lat, lng: e.latlng.lng };
        onChange(pos);
        createOrUpdateMarker(pos);
        createOrUpdateCircle(pos);
      });
    }

    return () => {
      map.remove();
      mapRef.current = null;
      markerRef.current = null;
      circleRef.current = null;
      mapReadyRef.current = false;
    };
  }, []);

  // ─────────────────────────────────────────────
  // Sync external position
  // ─────────────────────────────────────────────
  useEffect(() => {
    if (!position || !mapRef.current || !mapReadyRef.current) return;

    createOrUpdateCircle(position);
    if (mode === 'operate') createOrUpdateMarker(position);

    mapRef.current.setView([position.lat, position.lng], mapRef.current.getZoom() || 13);
  }, [position?.lat, position?.lng]);

  // ─────────────────────────────────────────────
  // Marker logic
  // ─────────────────────────────────────────────
  function createOrUpdateMarker(p: Position) {
    if (!p || !mapRef.current || !mapReadyRef.current) return;
    if (mode !== 'operate') return;

    if (!markerRef.current) {
      markerRef.current = L.marker([p.lat, p.lng], {
        draggable: true,
        icon: defaultIcon,
      }).addTo(mapRef.current);

      markerRef.current.on("dragend", () => {
        const { lat, lng } = markerRef.current!.getLatLng();
        const newPos = { lat, lng };
        onChange(newPos);
        createOrUpdateCircle(newPos);
      });
    } else {
      markerRef.current.setLatLng([p.lat, p.lng]);
    }
  }

  // ─────────────────────────────────────────────
  // Circle logic (solo para modo view)
  // ─────────────────────────────────────────────

  function createOrUpdateCircle(p: Position) {
    if (!p || !mapRef.current || !mapReadyRef.current) return;

    // Círculo grande
    if (!circleRef.current) {
      circleRef.current = L.circle([p.lat, p.lng], {
        radius: 1000, // 1 km
        color: '#3B82F6',
        fillColor: '#3B82F6',
        fillOpacity: 0.2,
        weight: 2,
      }).addTo(mapRef.current);
    } else {
      circleRef.current.setLatLng([p.lat, p.lng]);
    }

    // Círculo pequeño en el centro
    if (!smallCircleRef.current) {
      smallCircleRef.current = L.circle([p.lat, p.lng], {
        radius: 30, // 30 metros como “punto central”
        color: '#3B82F6',
        fillColor: '#3B82F6',
        fillOpacity: 0.3,
        weight: 1,
      }).addTo(mapRef.current);
    } else {
      smallCircleRef.current.setLatLng([p.lat, p.lng]);
    }
  }


  // ─────────────────────────────────────────────
  // Actualizar cuando cambia el modo
  // ─────────────────────────────────────────────
  useEffect(() => {
    if (!mapRef.current || !mapReadyRef.current) return;

    if (mode === 'view' && circleRef.current && markerRef.current) {
      // eliminar el marker en view
      markerRef.current.remove();
      markerRef.current = null;
    }
  }, [mode]);

  return (
    <div className="relative w-full rounded-lg overflow-hidden" style={{ height }}>
      {isLoading && (
        <div className="absolute inset-0 flex items-center justify-center bg-white/70 z-10">
          <span className="animate-spin border-4 border-primary-500 border-t-transparent rounded-full w-10 h-10"></span>
        </div>
      )}
      <div ref={containerRef} className="w-full h-full" />
    </div>
  );
}
