import { useParams, useNavigate } from 'react-router-dom';
import { Button, PageHeader } from '../../components/shared';
import ChurchDetailLayout from '../../components/churches/ChurchDetailLayout';
import { useChurch } from '../../hooks';
import { FiArrowLeft } from 'react-icons/fi';

export default function ChurchDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const { data: church, isLoading, error } = useChurch(id);

  const handleGoBack = () => navigate('/churches');

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-75">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto" />
            <p className="mt-4 text-neutral-600">Cargando información de la iglesia...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !church) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col items-center justify-center min-h-75">
          <div className="text-center">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="text-red-600 text-2xl">!</span>
            </div>
            <h2 className="text-xl font-semibold text-neutral-800 mb-2">Error al cargar iglesia</h2>
            <p className="text-neutral-600 mb-4">{(error as Error)?.message || 'No se pudo encontrar la iglesia solicitada'}</p>
            <Button variant="secondary" onClick={handleGoBack}>
              <span className="flex items-center gap-2"><FiArrowLeft size={16}/>Volver</span>
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4">
      <PageHeader
        title={church.name}
        subtitle={`${church.city?.name || 'Ciudad no especificada'}, ${church.states?.name || ''}`}
        actions={
          <Button variant="secondary" onClick={handleGoBack}>
            <span className="flex items-center gap-2"><FiArrowLeft size={16}/>Volver a Iglesias</span>
          </Button>
        }
      />

      {/* 
        showQuickActions=false → las acciones rápidas NO se muestran 
        en la vista de iglesias hijas 
      */}
      <ChurchDetailLayout church={church} showQuickActions={false} />
    </div>
  );
}

