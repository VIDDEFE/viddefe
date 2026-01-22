import { useNavigate } from 'react-router-dom';
import { useMyChurch } from '../../hooks';
import { Button, PageHeader } from '../../components/shared';
import ChurchDetailLayout from '../../components/churches/ChurchDetailLayout';
import { FiUsers, FiHome, FiCalendar } from 'react-icons/fi';

export default function MyChurch() {
  const navigate = useNavigate();
  const { data: myChurch, isLoading, error } = useMyChurch();

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando tu iglesia...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !myChurch) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-amber-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FiHome className="text-amber-600 text-2xl" />
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">
              No se encontró tu iglesia
            </h2>
            <p className="text-neutral-600 mb-4">
              {error?.message || 'No se pudo cargar la información de tu iglesia'}
            </p>
            <Button variant="secondary" onClick={() => navigate('/churches')}>
              Ver todas las iglesias
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Acciones rápidas habilitadas SOLO para "Mi Iglesia"
  const quickActions = [
    { icon: <FiUsers size={16} />, label: 'Administrar Miembros', onClick: () => navigate('/people'), disabled: false },
    { icon: <FiHome size={16} />, label: 'Ver Grupos', onClick: () => navigate('/groups'), disabled: false },
    { icon: <FiCalendar size={16} />, label: 'Ver Servicios', onClick: () => navigate('/services'), disabled: false },
    { icon: <FiCalendar size={16} />, label: 'Ver Eventos', onClick: () => navigate('/events'), disabled: false },
  ];

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title="Mi Iglesia"
        subtitle={`${myChurch.city?.name || 'Ciudad no especificada'}, ${myChurch.states?.name || ''}`}
        actions={
          <Button variant="secondary" onClick={() => navigate('/churches')}>
            Ver todas las iglesias
          </Button>
        }
      />

      {/* 
        showQuickActions=true → acciones rápidas VISIBLES y habilitadas 
        en la vista "Mi Iglesia" 
      */}
      <ChurchDetailLayout 
        church={myChurch} 
        showQuickActions={true}
        quickActions={quickActions}
      />
    </div>
  );
}
