import { useEffect, useRef } from "react";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

type Position = { lat: number; lng: number } | null;

const defaultIcon = new L.Icon({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});

export default function MapPicker({
  position,
  onChange,
  height = 300,
}: {
  position: Position;
  onChange: (p: Position) => void;
  height?: number;
}) {
  console.log("MapPicker render with position:", position);
  const mapRef = useRef<L.Map | null>(null);
  const markerRef = useRef<L.Marker | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  // init map ONCE
  useEffect(() => {
    if (!containerRef.current || mapRef.current) return;

    const map = L.map(containerRef.current);
    mapRef.current = map;

    map.setView(
      position ? [position.lat, position.lng] : [4.5726, -74.190],
      position ? 13 : 6
    );

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);

    map.whenReady(() => {
      if (position) {
        createOrUpdateMarker(position);
      }
    });

    map.on("click", (e: L.LeafletMouseEvent) => {
      const pos = { lat: e.latlng.lat, lng: e.latlng.lng };
      onChange(pos);
      createOrUpdateMarker(pos);
    });
  }, []);

  // sync external position
  useEffect(() => {
    if (!position || !mapRef.current) return;
    createOrUpdateMarker(position);
  }, [position]);

  function createOrUpdateMarker(p: Position) {
    if (!mapRef.current) return;

    if (!markerRef.current) {
      markerRef.current = L.marker([p?.lat, p?.lng], {
        draggable: true,
        icon: defaultIcon,
      }).addTo(mapRef.current);

      markerRef.current.on("dragend", () => {
        const { lat, lng } = markerRef.current!.getLatLng();
        onChange({ lat, lng });
      });
    } else {
      markerRef.current.setLatLng([p?.lat, p?.lng]);
    }
  }

  return (
    <div
      ref={containerRef}
      style={{ height }}
      className="w-full rounded-lg overflow-hidden"
    />
  );
}
