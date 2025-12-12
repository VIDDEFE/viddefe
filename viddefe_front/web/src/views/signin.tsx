import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Form, Input, Card } from '../components/shared';
import { validateEmail } from '../utils';

export default function SignIn() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
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
    // Simulación de login
    setTimeout(() => {
      setLoading(false);
      navigate('/dashboard');
    }, 1000);
  };

  return (
    <div className="signin-container">
      <Card className="signin-card">
        <div className="signin-header">
          <h1>VIDDEFE</h1>
          <p>Gestión de Iglesias</p>
        </div>

        <Form onSubmit={handleSubmit}>
          {error && <div className="form-error-message">{error}</div>}

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
            className="signin-button w-full"
            disabled={loading}
          >
            {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
          </Button>
        </Form>

        <div className="signin-footer">
          <p>¿No tienes una cuenta? <a href="/signup" className="hover:text-secondary-400">Regístrate aquí</a></p>
        </div>
      </Card>
    </div>
  );
}