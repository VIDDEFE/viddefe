import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Button, Form, Input, Card, Stepper, DropDown } from '../components/shared';
import { authService, type PersonRequest } from '../services/authService';
import { validateEmail } from '../utils';
import { useStates } from '../hooks/useStateCities';
import { FiArrowLeft } from 'react-icons/fi';

type Step = 1 | 2;

export default function SignUp() {
  const navigate = useNavigate();
  const [step, setStep] = useState<Step>(1);
  const [peopleId, setPeopleId] = useState<string>('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Step 1 - Person data
  const [personData, setPersonData] = useState({
    cc: '',
    firstName: '',
    lastName: '',
    phone: '',
    avatar: '',
    birthDate: '',
    typePersonId: 0,
    stateId: 0,
    churchId: '',
  });

  const [selectedStateId, setSelectedStateId] = useState<number | undefined>(undefined);
  const { data: states } = useStates();

  // Step 2 - User credentials
  const [userData, setUserData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    roleId: 2,
  });

  const handlePersonChange = (field: keyof typeof personData, value: any) => {
    setPersonData((prev) => ({
      ...prev,
      [field]: field === 'stateId' || field === 'typePersonId' ? Number(value) : value,
    }));
  };

  const handleUserChange = (field: keyof typeof userData, value: string | number) => {
    setUserData((prev) => ({ ...prev, [field]: field === 'roleId' ? Number(value) : value }));
  };

  const validateStep1 = (): boolean => {
    if (!personData.cc || !personData.firstName || !personData.lastName || !personData.phone || !personData.birthDate) {
      setError('Por favor completa todos los campos requeridos');
      return false;
    }
    if (!personData.stateId) {
      setError('Por favor selecciona un estado');
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
      const response = await authService.createPerson(personData as PersonRequest);
      setPeopleId(response.id);
      setStep(2);
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
      await authService.signUp({
        email: userData.email,
        password: userData.password,
        peopleId,
        roleId: userData.roleId,
      });

      navigate('/signin', {
        state: { message: 'Registro exitoso. Por favor inicia sesión.' },
      });
    } catch (err: any) {
      setError(err?.message || 'Error al registrarse. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleBackToStep1 = () => {
    setStep(1);
    setError('');
  };

  return (
    <div className="min-h-screen from-primary-50 to-primary-100 flex items-center justify-center p-4">
      <Card className="w-full max-w-2xl shadow-lg">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-primary-800 mb-2">VIDDEFE</h1>
          <p className="text-primary-600">Crear Nueva Cuenta</p>
        </div>

        <Stepper steps={['Información Personal', 'Credenciales']} currentStep={step - 1} />

        {step === 1 ? (
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

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Número de Cédula"
                placeholder="12345678"
                value={personData.cc}
                onChange={(e) => handlePersonChange('cc', e.target.value)}
                disabled={loading}
              />
              <Input
                label="Nombres"
                placeholder="Juan"
                value={personData.firstName}
                onChange={(e) => handlePersonChange('firstName', e.target.value)}
                disabled={loading}
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Apellidos"
                placeholder="Pérez García"
                value={personData.lastName}
                onChange={(e) => handlePersonChange('lastName', e.target.value)}
                disabled={loading}
              />
              <Input
                label="Teléfono"
                placeholder="+569 1234 5678"
                value={personData.phone}
                onChange={(e) => handlePersonChange('phone', e.target.value)}
                disabled={loading}
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Fecha de Nacimiento"
                type="date"
                value={personData.birthDate}
                onChange={(e) => handlePersonChange('birthDate', e.target.value)}
                disabled={loading}
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <DropDown
                label="Tipo de Persona"
                options={[
                  { value: '1', label: 'Oveja' },
                  { value: '2', label: 'Voluntario' },
                  { value: '3', label: 'Pastor' },
                ]}
                value={String(personData.typePersonId)}
                onChangeValue={(val) => handlePersonChange('typePersonId', val)}
                searchKey="label"

              />

              <DropDown
                label="Departamento/Estado"
                options={(states ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
                value={personData.stateId ? String(personData.stateId) : ''}
                onChangeValue={(val) => {
                  const id = val ? Number(val) : 0;
                  setSelectedStateId(id);
                  handlePersonChange('stateId', id);
                }}
                searchKey="label"
              />
            </div>

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
                disabled={loading}
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
