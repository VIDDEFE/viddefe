import { useAppContext } from '../../context/AppContext';
import { FiLogOut, FiChevronDown, FiUser } from 'react-icons/fi';
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Avatar } from '../shared';

export default function NavBar() {
  const { user, logout } = useAppContext();
  const navigate = useNavigate();
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/signin');
  };

  if (!user) return null;

  const person = user.person;
  const fullName = `${person.firstName} ${person.lastName}`;

  return (
    <nav className="bg-white border-b border-primary-100 shadow-sm px-6 py-4 flex items-center justify-between h-20">
      {/* Left side - Search or title */}
      <div className="hidden md:flex items-center">
        <h2 className="text-lg font-semibold text-primary-900">Bienvenido, {fullName}</h2>
      </div>

      {/* Right side - User Profile */}
      <div className="ml-auto flex items-center gap-4">
        <div className="relative">
          <button
            onClick={() => setIsProfileOpen(!isProfileOpen)}
            className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-primary-50 transition-colors"
          >
            <Avatar 
              src={person.avatar} 
              name={fullName} 
              size="md"
            />
            <div className="hidden sm:flex flex-col items-start">
              <span className="text-sm font-medium text-primary-900">{fullName}</span>
              <span className="text-xs text-primary-600">{user.user}</span>
            </div>
            <FiChevronDown size={16} className="text-primary-700" />
          </button>

          {/* Dropdown menu */}
          {isProfileOpen && (
            <div className="absolute right-0 mt-2 w-48 bg-white border border-primary-100 rounded-lg shadow-lg z-50">
              <div className="px-4 py-3 border-b border-primary-100">
                <p className="text-sm font-medium text-primary-900">{fullName}</p>
                <p className="text-xs text-primary-600">{user.user}</p>
              </div>

              <Link
                to="/account"
                onClick={() => setIsProfileOpen(false)}
                className="w-full text-left px-4 py-3 text-sm text-primary-700 hover:bg-primary-50 flex items-center gap-2 transition-colors"
              >
                <FiUser size={16} />
                Mi Cuenta
              </Link>

              <button
                onClick={handleLogout}
                className="w-full text-left px-4 py-3 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2 transition-colors"
              >
                <FiLogOut size={16} />
                Cerrar Sesión
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
