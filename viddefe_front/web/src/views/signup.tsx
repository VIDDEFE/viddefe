import { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { Button, Form, Input, Card, Stepper, DropDown, PersonForm, initialPersonPastorFormData, type PersonFormData } from '../components/shared';
import MapPicker from '../components/shared/MapPicker';
import { authService, type PersonRequest, type ChurchRequest, type SignUpStep, type PersonResponse } from '../services/authService';
import { validateEmail } from '../utils';
import { useStates, useCities } from '../hooks';
import { FiArrowLeft } from 'react-icons/fi';

type Step = 1 | 2 | 3;

// Estado de reanudación desde signin
interface ResumeState {
  resumeProcess: boolean;
  nextStep: SignUpStep;
  peopleId: string;
  userId?: string;
  email?: string;
  person?: PersonResponse;
}

export default function SignUp() {
  const navigate = useNavigate();
  const location = useLocation();
  const [step, setStep] = useState<Step>(1);
  const [peopleId, setPeopleId] = useState<string>('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Track de pasos completados (para deshabilitar botón de volver)
  const [completedSteps, setCompletedSteps] = useState<Set<Step>>(new Set());

  // Step 1 - Person data
  const [personData, setPersonData] = useState<PersonFormData>(initialPersonPastorFormData);

  const { data: states } = useStates();

  // Step 2 - User credentials
  const [userData, setUserData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    roleId: 2,
  });

  // Step 3 - Church data
  const [churchData, setChurchData] = useState<ChurchRequest>({
    name: '',
    phone: '',
    email: '',
    pastorId: '',
    foundationDate: '',
    latitude: 0,
    longitude: 0,
    cityId: 0,
  });
  const [churchStateId, setChurchStateId] = useState<number | undefined>(undefined);
  const [churchCityId, setChurchCityId] = useState<number | undefined>(undefined);
  const { data: churchCities } = useCities(churchStateId);

  // Efecto para manejar la reanudación del proceso desde signin
  useEffect(() => {
    const state = location.state as ResumeState | null;
    if (state?.resumeProcess && state.nextStep) {
      // Determinar el paso basado en nextStep
      if (state.nextStep === 'CREATION_USER') {
        setStep(2);
        setPeopleId(state.peopleId);
        // Marcar paso 1 como completado
        setCompletedSteps(new Set([1]));
      } else if (state.nextStep === 'CREATION_CHURCH') {
        setStep(3);
        setPeopleId(state.peopleId);
        // Pre-llenar email si está disponible
        if (state.email) {
          setUserData(prev => ({ ...prev, email: state.email! }));
        }
        // Marcar pasos 1 y 2 como completados
        setCompletedSteps(new Set([1, 2]));
        // Configurar pastorId para la iglesia
        setChurchData(prev => ({ ...prev, pastorId: state.peopleId }));
      }

      // Limpiar el state para evitar re-ejecutar en navegaciones futuras
      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  const handleUserChange = (field: keyof typeof userData, value: string | number) => {
    setUserData((prev) => ({ ...prev, [field]: field === 'roleId' ? Number(value) : value }));
  };

  const validateStep1 = (): boolean => {
    if (!personData.cc || !personData.firstName || !personData.lastName || !personData.phone || !personData.birthDate) {
      setError('Por favor completa todos los campos requeridos');
      return false;
    }
    if (!personData.stateId) {
      setError('Por favor selecciona un departamento');
      return false;
    }
    if (!personData.typePersonId) {
      setError('Por favor selecciona el tipo de persona');
      return false;
    }
    return true;
  };

  const validateStep2 = (): boolean => {
    if (!userData.email || !userData.password || !userData.confirmPassword) {
      setError('Por favor completa todos los campos');
      return false;
    }

    if (!validateEmail(userData.email)) {
      setError('Por favor ingresa un correo válido');
      return false;
    }

    if (userData.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres');
      return false;
    }

    if (userData.password !== userData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return false;
    }

    return true;
  };

  const handleNextStep = async () => {
    setError('');

    if (!validateStep1()) return;

    setLoading(true);
    try {
      // Step 1: Crear pastor usando el nuevo endpoint
      const response = await authService.signUpPastor(personData as PersonRequest);
      
      if (response.nextStep === 'CREATION_USER') {
        setPeopleId(response.data.id);
        setCompletedSteps(prev => new Set([...prev, 1]));
        setStep(2);
      }
    } catch (err: any) {
      setError(err?.message || 'Error al crear el perfil. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!validateStep2()) return;

    setLoading(true);
    try {
      // Step 2: Crear usuario usando el nuevo endpoint
      const response = await authService.signUpUser({
        email: userData.email,
        password: userData.password,
        peopleId,
        roleId: userData.roleId,
      });

      // Marcar paso 2 como completado
      setCompletedSteps(prev => new Set([...prev, 2]));

      // Si el siguiente paso es crear iglesia
      if (response.nextStep === 'CREATION_CHURCH') {
        setChurchData(prev => ({ ...prev, pastorId: peopleId }));
        setStep(3);
        setLoading(false);
        return;
      }

      // Si está completo
      if (response.nextStep === 'DONE' || response.completed === true) {
        navigate('/signin', {
          state: { message: 'Registro exitoso. Por favor inicia sesión.' },
        });
      }
    } catch (err: any) {
      setError(err?.message || 'Error al registrarse. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateChurch = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!churchData.name) {
      setError('Por favor ingresa el nombre de la iglesia');
      return;
    }

    setLoading(true);
    try {
      // Step 3: Crear iglesia usando el nuevo endpoint
      const response = await authService.signUpChurch({
        name: churchData.name,
        phone: churchData.phone || '',
        email: churchData.email || '',
        pastorId: peopleId,
        foundationDate: churchData.foundationDate,
        cityId: churchCityId || 0,
        latitude: churchData.latitude || 0,
        longitude: churchData.longitude || 0,
      });

      if (response.nextStep === 'DONE') {
        navigate('/signin', {
          state: { message: 'Registro exitoso. Iglesia creada. Por favor inicia sesión.' },
        });
      }
    } catch (err: any) {
      setError(err?.message || 'Error al crear la iglesia. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleBackToStep1 = () => {
    // Solo permitir volver si el paso 1 no está completado
    if (completedSteps.has(1)) return;
    setStep(1);
    setError('');
  };

  // Verificar si se puede volver atrás
  const canGoBack = (fromStep: Step): boolean => {
    const previousStep = (fromStep - 1) as Step;
    return !completedSteps.has(previousStep);
  };

  return (
    <div className="min-h-screen from-primary-50 to-primary-100 flex items-center justify-center p-4">
      <Card className="w-full max-w-2xl shadow-lg">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-primary-800 mb-2">VIDDEFE</h1>
          <p className="text-primary-600">Crear Nueva Cuenta</p>
        </div>

        <Stepper steps={['Información Personal', 'Credenciales', 'Crear Iglesia']} currentStep={step - 1} />

        {step === 3 ? (
          // Step 3: Create Church
          <Form onSubmit={handleCreateChurch} className="gap-4">
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
                {error}
              </div>
            )}

            <div className="p-3 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm mb-2">
              <p className="font-semibold">¡Usuario creado exitosamente!</p>
              <p>Como pastor, puedes crear tu iglesia ahora o hacerlo después.</p>
            </div>

            <Input
              label="Nombre de la Iglesia"
              placeholder="Iglesia Vida en Fe"
              value={churchData.name}
              onChange={(e) => setChurchData({ ...churchData, name: e.target.value })}
              disabled={loading}
            />

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Email de la Iglesia"
                type="email"
                placeholder="contacto@iglesia.com"
                value={churchData.email}
                onChange={(e) => setChurchData({ ...churchData, email: e.target.value })}
                disabled={loading}
              />
              <Input
                label="Teléfono"
                placeholder="+569 1234 5678"
                value={churchData.phone}
                onChange={(e) => setChurchData({ ...churchData, phone: e.target.value })}
                disabled={loading}
              />
            </div>

            <Input
              label="Fecha de Fundación"
              type="date"
              value={churchData.foundationDate}
              onChange={(e) => setChurchData({ ...churchData, foundationDate: e.target.value })}
              disabled={loading}
            />

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <DropDown
                label="Departamento"
                options={(states ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
                value={churchStateId ? String(churchStateId) : ''}
                onChangeValue={(val) => {
                  const id = val ? Number(val) : undefined;
                  setChurchStateId(id);
                  setChurchCityId(undefined);
                }}
                searchKey="label"
              />

              <DropDown
                label="Ciudad"
                options={(churchCities ?? []).map((c) => ({
                  value: String(c.cityId),
                  label: c.name,
                }))}
                value={churchCityId ? String(churchCityId) : ''}
                onChangeValue={(val) => {
                  const id = val ? Number(val) : undefined;
                  setChurchCityId(id);
                }}
                searchKey="label"
              />
            </div>

            <div>
              <label className="font-semibold text-primary-900 mb-2 text-base block">
                Ubicación (click en el mapa para colocar marcador)
              </label>
              <MapPicker
                mode='operate'
                position={churchData.latitude && churchData.longitude ? { lat: churchData.latitude, lng: churchData.longitude } : null}
                onChange={(p) => setChurchData(prev => ({ ...prev, latitude: p?.lat ?? 0, longitude: p?.lng ?? 0 }))}
                height={250}
              />
              <div className="grid grid-cols-2 gap-3 mt-3">
                <Input
                  label="Latitud"
                  placeholder="Latitud"
                  value={churchData.latitude ? String(churchData.latitude) : ''}
                  onChange={(e) => setChurchData(prev => ({ ...prev, latitude: parseFloat(e.target.value || '0') }))}
                />
                <Input
                  label="Longitud"
                  placeholder="Longitud"
                  value={churchData.longitude ? String(churchData.longitude) : ''}
                  onChange={(e) => setChurchData(prev => ({ ...prev, longitude: parseFloat(e.target.value || '0') }))}
                />
              </div>
            </div>

            <div className="flex gap-3">
              <Button
                variant="primary"
                className="flex-1"
                disabled={loading}
                type="submit"
              >
                {loading ? 'Creando...' : 'Crear Iglesia'}
              </Button>
            </div>
          </Form>
        ) : step === 1 ? (
          // Step 1: Personal Information
          <Form onSubmit={(e) => {
            e.preventDefault();
            handleNextStep();
          }} className="gap-4">
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
                {error}
              </div>
            )}

            <PersonForm
              value={personData}
              onChange={setPersonData}
              disabled={loading}
              showTypeSelector={true}
              disabledTypePerson={true}
            />

            <Button
              variant="primary"
              className="w-full"
              disabled={loading}
              type="submit"
            >
              {loading ? 'Procesando...' : 'Siguiente'}
            </Button>

            <div className="text-center text-sm text-primary-600">
              <p>¿Ya tienes una cuenta? <Link to="/signin" className="text-primary-700 font-medium hover:underline">Inicia sesión</Link></p>
            </div>
          </Form>
        ) : (
          // Step 2: User Credentials
          <Form onSubmit={handleSubmit} className="gap-4">
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
                {error}
              </div>
            )}

            <Input
              label="Correo Electrónico"
              type="email"
              placeholder="tu@correo.com"
              value={userData.email}
              onChange={(e) => handleUserChange('email', e.target.value)}
              disabled={loading}
            />

            <Input
              label="Contraseña"
              type="password"
              placeholder="Mínimo 8 caracteres"
              value={userData.password}
              onChange={(e) => handleUserChange('password', e.target.value)}
              disabled={loading}
            />

            <Input
              label="Confirmar Contraseña"
              type="password"
              placeholder="Confirma tu contraseña"
              value={userData.confirmPassword}
              onChange={(e) => handleUserChange('confirmPassword', e.target.value)}
              disabled={loading}
            />

            <DropDown
              label="Rol"
              options={[
                { value: '2', label: 'Usuario' },
                { value: '1', label: 'Administrador' },
              ]}
              value={String(userData.roleId)}
              onChangeValue={(val) => handleUserChange('roleId', val)}
              searchKey="label"
            />

            <div className="flex gap-3">
              <Button
                variant="secondary"
                className="flex-1"
                onClick={handleBackToStep1}
                disabled={loading || !canGoBack(2)}
                type="button"
              >
                <FiArrowLeft className="inline mr-2" />
                Atrás
              </Button>
              <Button
                variant="primary"
                className="flex-1"
                disabled={loading}
                type="submit"
              >
                {loading ? 'Registrando...' : 'Crear Cuenta'}
              </Button>
            </div>

            <div className="p-3 bg-primary-50 border border-primary-100 rounded-lg text-xs text-primary-700">
              <p className="font-semibold mb-1">Requisitos de contraseña:</p>
              <ul className="list-disc pl-4 space-y-1">
                <li>Mínimo 8 caracteres</li>
                <li>Las contraseñas deben coincidir</li>
              </ul>
            </div>
          </Form>
        )}
      </Card>
    </div>
  );
}
