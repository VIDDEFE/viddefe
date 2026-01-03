import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { Button, Form, Input, Card } from '../components/shared';
import { useAppContext } from '../context/AppContext';
import { type SignInIncompleteData, type SignInResponse } from '../services/authService';
import { validateEmail } from '../utils';
import { FiMail, FiPhone, FiLock, FiLoader } from 'react-icons/fi';

type LoginMethod = 'email' | 'phone';

export default function SignIn() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser, setPermissions, login } = useAppContext();
  const [loginMethod, setLoginMethod] = useState<LoginMethod>('email');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);
  
  useEffect(() => {
    // Mostrar mensaje de éxito si viene del signup
    const state = location.state as { message?: string } | null;
    if (state?.message) {
      setSuccessMessage(state.message);
      setTimeout(() => setSuccessMessage(''), 5000);
    }
  }, [location]);

  const validateForm = (): boolean => {
    if (loginMethod === 'email') {
      if (!email.trim()) {
        setError('Por favor ingresa tu correo electrónico');
        return false;
      }
      if (!validateEmail(email)) {
        setError('Por favor ingresa un correo válido');
        return false;
      }
    } else {
      if (!phone.trim()) {
        setError('Por favor ingresa tu número de teléfono');
        return false;
      }
      // Validar formato básico de teléfono
      if (!/^[+]?[\d\s-]{8,}$/.test(phone.trim())) {
        setError('Por favor ingresa un número de teléfono válido');
        return false;
      }
    }

    if (!password) {
      setError('Por favor ingresa tu contraseña');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) return;

    const identifier = loginMethod === 'email' ? email.trim() : phone.trim();

    setLoading(true);
    try {
      const response: SignInResponse = await login(identifier, password, loginMethod);

      // Si el proceso no está completo, redirigir a signup con el paso correspondiente
      if (!response.completed && response.nextStep !== 'DONE') {
        const incompleteData = response.data as SignInIncompleteData;
        
        navigate('/signup', {
          state: {
            resumeProcess: true,
            nextStep: response.nextStep,
            peopleId: incompleteData.peopleId,
            userId: incompleteData.userId,
            email: incompleteData.email || (loginMethod === 'email' ? email : ''),
            person: incompleteData.person,
          },
        });
        return;
      }

      // Proceso completo - continuar con login normal
      // Cargar permisos desde meta si existen
      const rawResponse = response as any;
      if (rawResponse.meta?.permissions) {
        setPermissions(rawResponse.meta.permissions);
      }

      navigate('/');

    } catch (err: any) {
      setError(err?.message || 'Error al iniciar sesión. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-linear-to-br from-primary-50 to-primary-100 flex items-center justify-center p-4">
      <Card className="w-full max-w-md shadow-lg">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-primary-800 mb-2">VIDDEFE</h1>
          <p className="text-primary-600">Gestión de Iglesias</p>
        </div>

        {/* Selector de método de login */}
        <div className="mb-6">
          <div className="grid grid-cols-2 gap-2 p-1 bg-neutral-100 rounded-xl">
            <button
              type="button"
              onClick={() => setLoginMethod('email')}
              className={`flex items-center justify-center gap-2 px-4 py-3 rounded-lg font-medium transition-all duration-200 ${
                loginMethod === 'email'
                  ? 'bg-white text-primary-700 shadow-md'
                  : 'text-neutral-600 hover:text-primary-600'
              }`}
            >
              <FiMail className="w-4 h-4" />
              Correo
            </button>
            <button
              type="button"
              onClick={() => setLoginMethod('phone')}
              className={`flex items-center justify-center gap-2 px-4 py-3 rounded-lg font-medium transition-all duration-200 ${
                loginMethod === 'phone'
                  ? 'bg-white text-primary-700 shadow-md'
                  : 'text-neutral-600 hover:text-primary-600'
              }`}
            >
              <FiPhone className="w-4 h-4" />
              WhatsApp
            </button>
          </div>
        </div>

        <Form onSubmit={handleSubmit} className="gap-4">
          {successMessage && (
            <div className="p-3 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm">
              {successMessage}
            </div>
          )}

          {error && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {error}
            </div>
          )}

          {/* Campo de email */}
          {loginMethod === 'email' && (
            <div>
              <label className="flex items-center gap-2 text-sm font-medium text-neutral-700 mb-2">
                <FiMail className="w-4 h-4" />
                Correo Electrónico
              </label>
              <Input
                type="email"
                placeholder="tu@correo.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={loading}
              />
            </div>
          )}

          {/* Campo de teléfono */}
          {loginMethod === 'phone' && (
            <div>
              <label className="flex items-center gap-2 text-sm font-medium text-neutral-700 mb-2">
                <FiPhone className="w-4 h-4" />
                Número de WhatsApp
              </label>
              <Input
                type="tel"
                placeholder="+57 300 123 4567"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                disabled={loading}
              />
              <p className="text-xs text-neutral-500 mt-1">
                Incluye el código de país (+57 para Colombia)
              </p>
            </div>
          )}

          <div>
            <label className="flex items-center gap-2 text-sm font-medium text-neutral-700 mb-2">
              <FiLock className="w-4 h-4" />
              Contraseña
            </label>
            <Input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
            />
          </div>

          <Button 
            variant="primary" 
            className="w-full mt-2"
            disabled={loading}
          >
            {loading ? (
              <span className="flex items-center justify-center gap-2">
                <FiLoader className="w-4 h-4 animate-spin" />
                Iniciando sesión...
              </span>
            ) : (
              'Iniciar Sesión'
            )}
          </Button>
        </Form>

        <div className="mt-6 text-center text-sm text-primary-600">
          <p>¿No tienes una cuenta? <Link to="/signup" className="text-primary-700 font-medium hover:underline">Regístrate aquí</Link></p>
        </div>

        {/* Info hint */}
        <div className="mt-6 p-3 bg-primary-50 border border-primary-100 rounded-lg text-xs text-primary-700">
          <p className="font-semibold mb-1">💡 Información:</p>
          <p>Puedes iniciar sesión con tu correo electrónico o tu número de WhatsApp registrado.</p>
        </div>
      </Card>
    </div>
  );
}