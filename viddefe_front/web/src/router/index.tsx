import { Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from '../components/layout';
import SignIn from '../views/signin';
import Dashboard from '../views/dashboard/Dashboard';
import Churches from '../views/churches/Churches';
import People from '../views/people/People';
import Services from '../views/services/Services';
import Groups from '../views/groups/Groups';
import Events from '../views/events/Events';

export default function Router() {
  return (   
    <Routes>
      <Route path="/signin" element={<SignIn />} />
      
      <Route element={<Layout />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/churches" element={<Churches />} />
        <Route path="/people" element={<People />} />
        <Route path="/services" element={<Services />} />
        <Route path="/groups" element={<Groups />} />
        <Route path="/events" element={<Events />} />
      </Route>

      <Route path="/" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
