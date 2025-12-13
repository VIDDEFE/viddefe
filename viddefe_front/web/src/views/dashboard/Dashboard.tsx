import { useState } from "react";
import { Card, Button, PageHeader } from "../../components/shared";
import { FiBarChart2, FiUsers, FiCalendar } from "react-icons/fi";
import { MdChurch } from "react-icons/md";

interface StatCard {
  title: string;
  value: number;
  icon: string;
  bgColor: string;
}

export default function Dashboard() {
  const [stats] = useState<StatCard[]>([
    { title: "Total Iglesias", value: 5, icon: "church", bgColor: "bg-blue-400" },
    { title: "Total Personas", value: 245, icon: "people", bgColor: "bg-green-400" },
    { title: "Servicios Este Mes", value: 12, icon: "services", bgColor: "bg-red-400" },
    { title: "Grupos Activos", value: 8, icon: "groups", bgColor: "bg-yellow-400" },
  ]);

  return (
    <div className="flex flex-col gap-8">
      <PageHeader
        title="Dashboard"
        subtitle="Bienvenido al sistema de gestión de iglesias"
      />

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6">
        {stats.map((stat, idx) => (
          <Card
            key={idx}
            className="flex flex-col items-start gap-3 p-5 sm:p-6 shadow-sm border border-primary-100"
          >
            <div className={`text-2xl sm:text-3xl p-3 rounded-lg text-white ${stat.bgColor}`}>
              {stat.icon === 'church' && <MdChurch size={28} className="text-white" />}
              {stat.icon === 'people' && <FiUsers size={28} className="text-white" />}
              {stat.icon === 'services' && <FiCalendar size={28} className="text-white" />}
              {stat.icon === 'groups' && <FiUsers size={28} className="text-white" />}
              {stat.icon === 'dashboard' && <FiBarChart2 size={28} className="text-white" />}
            </div>

            <h3 className="text-base sm:text-lg font-semibold text-primary-900">
              {stat.title}
            </h3>

            <p className="text-2xl sm:text-3xl font-bold text-primary-800">
              {stat.value}
            </p>
          </Card>
        ))}
      </div>

      {/* Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Quick actions */}
        <Card className="p-5 sm:p-6 flex flex-col gap-4">
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Acciones Rápidas
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Button variant="primary">Nueva Iglesia</Button>
            <Button variant="primary">Nueva Persona</Button>
            <Button variant="primary">Nuevo Servicio</Button>
            <Button variant="primary">Nuevo Grupo</Button>
          </div>
        </Card>

        {/* Recent activity */}
        <Card className="p-5 sm:p-6 flex flex-col gap-4">
          <h2 className="text-lg sm:text-xl font-semibold text-primary-900">
            Actividad Reciente
          </h2>

          <ul className="list-disc pl-5 space-y-2 text-primary-800 text-sm sm:text-base">
            <li>Se agregó a Juan Pérez como miembro</li>
            <li>Nuevo servicio programado para el domingo</li>
            <li>Grupo de oración creado</li>
            <li>Evento especial registrado</li>
          </ul>
        </Card>
      </div>
    </div>
  );
}
