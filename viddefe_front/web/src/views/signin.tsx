import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { Button, Form, Input, Card } from '../components/shared';
import { useAppContext } from '../context/AppContext';
import { authService } from '../services/authService';
import { validateEmail } from '../utils';

export default function SignIn() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useAppContext();
  const [email, setEmail] = useState('');
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!email || !password) {
      setError('Por favor completa todos los campos');
      return;
    }

    if (!validateEmail(email)) {
      setError('Por favor ingresa un correo válido');
      return;
    }

    setLoading(true);
    try {
      const response = await authService.signIn({
        email,
        password,
      });

      // Guardar token y usuario en contexto y localStorage
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(response.user));
      
      setUser(response.user);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err?.message || 'Error al iniciar sesión. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-primary-100 flex items-center justify-center p-4">
      <Card className="w-full max-w-md shadow-lg">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-primary-800 mb-2">VIDDEFE</h1>
          <p className="text-primary-600">Gestión de Iglesias</p>
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

          <Input
            label="Correo Electrónico"
            type="email"
            placeholder="tu@correo.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={loading}
          />

          <Input
            label="Contraseña"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={loading}
          />

          <Button 
            variant="primary" 
            className="w-full"
            disabled={loading}
          >
            {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
          </Button>
        </Form>

        <div className="mt-6 text-center text-sm text-primary-600">
          <p>¿No tienes una cuenta? <Link to="/signup" className="text-primary-700 font-medium hover:underline">Regístrate aquí</Link></p>
        </div>

        {/* Demo hint */}
        <div className="mt-6 p-3 bg-primary-50 border border-primary-100 rounded-lg text-xs text-primary-700">
          <p className="font-semibold mb-1">Información:</p>
          <p>Prueba registrándote primero en la sección de registro.</p>
        </div>
      </Card>
    </div>
  );
}