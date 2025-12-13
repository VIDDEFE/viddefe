import { useAppContext } from '../../context/AppContext';
import { FiLogOut, FiChevronDown } from 'react-icons/fi';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function NavBar() {
  const { user, logout } = useAppContext();
  const navigate = useNavigate();
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/signin');
  };

  if (!user) return null;

  return (
    <nav className="bg-white border-b border-primary-100 shadow-sm px-6 py-4 flex items-center justify-between h-20">
      {/* Left side - Search or title */}
      <div className="hidden md:flex items-center">
        <h2 className="text-lg font-semibold text-primary-900">Bienvenido, {user.name}</h2>
      </div>

      {/* Right side - User Profile */}
      <div className="ml-auto flex items-center gap-4">
        <div className="relative">
          <button
            onClick={() => setIsProfileOpen(!isProfileOpen)}
            className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-primary-50 transition-colors"
          >
            <img
              src={user.avatar}
              alt={user.name}
              className="w-10 h-10 rounded-full border-2 border-primary-200"
            />
            <div className="hidden sm:flex flex-col items-start">
              <span className="text-sm font-medium text-primary-900">{user.name}</span>
              <span className="text-xs text-primary-600">{user.email}</span>
            </div>
            <FiChevronDown size={16} className="text-primary-700" />
          </button>

          {/* Dropdown menu */}
          {isProfileOpen && (
            <div className="absolute right-0 mt-2 w-48 bg-white border border-primary-100 rounded-lg shadow-lg z-50">
              <div className="px-4 py-3 border-b border-primary-100">
                <p className="text-sm font-medium text-primary-900">{user.name}</p>
                <p className="text-xs text-primary-600">{user.email}</p>
              </div>

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
