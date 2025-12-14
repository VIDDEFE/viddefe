import { Routes, Route, Navigate } from 'react-router-dom';
import { useAppContext } from '../context/AppContext';
import { Layout } from '../components/layout';
import SignIn from '../views/signin';
import SignUp from '../views/signup';
import Dashboard from '../views/dashboard/Dashboard';
import Churches from '../views/churches/Churches';
import People from '../views/people/People';
import Services from '../views/services/Services';
import Groups from '../views/groups/Groups';
import Events from '../views/events/Events';
import type { JSX } from 'react';

function ProtectedRoute({ element }: { element: JSX.Element }) {
  const { isLoggedIn } = useAppContext();
  return isLoggedIn ? element : <Navigate to="/signin" replace />;
}

export default function Router() {
  const { isLoggedIn } = useAppContext();

  return (   
    <Routes>
      <Route path="/signin" element={<SignIn />} />
      <Route path="/signup" element={<SignUp />} />
      
      <Route element={<Layout />}>
        <Route path="/dashboard" element={<ProtectedRoute element={<Dashboard />} />} />
        <Route path="/churches" element={<ProtectedRoute element={<Churches />} />} />
        <Route path="/people" element={<ProtectedRoute element={<People />} />} />
        <Route path="/services" element={<ProtectedRoute element={<Services />} />} />
        <Route path="/groups" element={<ProtectedRoute element={<Groups />} />} />
        <Route path="/events" element={<ProtectedRoute element={<Events />} />} />
      </Route>

      <Route path="/" element={<Navigate to={isLoggedIn ? "/dashboard" : "/signin"} replace />} />
    </Routes>
  );
}
