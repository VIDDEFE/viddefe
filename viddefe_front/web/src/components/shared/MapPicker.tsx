import { useEffect, useRef } from "react";
import L from "leaflet";

import "leaflet/dist/leaflet.css";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

type Position = { lat: number; lng: number } | null;

export default function MapPicker({
  position,
  onChange,
  height = 300,
}: {
  position: Position;
  onChange: (p: Position) => void;
  height?: number;
}) {
  const mapRef = useRef<L.Map | null>(null);
  const markerRef = useRef<L.Marker | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!containerRef.current) return;

    // Initialize map once
    const map = L.map(containerRef.current).setView(
      position
        ? [position.lat, position.lng]
        : [4.5726, -74.190],
      6
    );

    mapRef.current = map;

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(map);

    // Add click handler → create/update marker
    map.on("click", (e: L.LeafletMouseEvent) => {
      const pos = { lat: e.latlng.lat, lng: e.latlng.lng };
      onChange(pos);
      updateMarker(pos);
    });

    return () => {
      map.remove();
    };
  }, []);

  // Update marker when position prop changes
  useEffect(() => {
    if (position && mapRef.current) updateMarker(position);
  }, [position]);

  function updateMarker(p: Position) {
    if (!p || !mapRef.current) return;

    // If marker exists → update position
    if (markerRef.current) {
      markerRef.current.setLatLng([p.lat, p.lng]);
      return;
    }

    // Create marker
    const m = L.marker([p.lat, p.lng], { draggable: true });
    markerRef.current = m;
    m.addTo(mapRef.current);

    // Drag event
    m.on("dragend", () => {
      const latlng = m.getLatLng();
      onChange({ lat: latlng.lat, lng: latlng.lng });
    });
  }

  return (
    <div
      ref={containerRef}
      style={{ height }}
      className="w-full rounded-lg overflow-hidden"
    />
  );
}
