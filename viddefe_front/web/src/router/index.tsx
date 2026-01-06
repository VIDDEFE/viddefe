import { Routes, Route, Navigate } from 'react-router-dom';
import { useAppContext } from '../context/AppContext';
import { Layout } from '../components/layout';
import SignIn from '../views/signin';
import SignUp from '../views/signup';
import Dashboard from '../views/dashboard/Dashboard';
import Churches from '../views/churches/Churches';
import ChurchDetail from '../views/churches/ChurchDetail';
import MyChurch from '../views/churches/MyChurch';
import People from '../views/people/People';
import Services from '../views/services/Services';
import Groups from '../views/groups/Groups';
import HomeGroupDetail from '../views/groups/HomeGroupDetail';
import MyGroup from '../views/groups/MyGroup';
import Events from '../views/events/Events';
import Worships from '../views/worships/Worships';
import WorshipDetail from '../views/worships/WorshipDetail';
import MeetingDetail from '../views/groups/MeetingDetail';
import Account from '../views/account/Account';
import type { JSX } from 'react';

function ProtectedRoute({ element }: { element: JSX.Element }) {
  const { user } = useAppContext();
  return user ? element : <Navigate to="/signin" replace />;
}

export default function Router() {
  const { user } = useAppContext();

  return (   
    <Routes>
      <Route path="/signin" element={<SignIn />} />
      <Route path="/signup" element={<SignUp />} />
      
      <Route element={<Layout />}>
        <Route path="/dashboard" element={<ProtectedRoute element={<Dashboard />} />} />
        <Route path="/churches" element={<ProtectedRoute element={<Churches />} />} />
        <Route path="/churches/:id" element={<ProtectedRoute element={<ChurchDetail />} />} />
        <Route path="/my-church" element={<ProtectedRoute element={<MyChurch />} />} />
        <Route path="/people" element={<ProtectedRoute element={<People />} />} />
        <Route path="/services" element={<ProtectedRoute element={<Services />} />} />
        <Route path="/groups" element={<ProtectedRoute element={<Groups />} />} />
        <Route path="/groups/:id" element={<ProtectedRoute element={<HomeGroupDetail />} />} />
        <Route path="/my-group" element={<ProtectedRoute element={<MyGroup />} />} />
        <Route path="/events" element={<ProtectedRoute element={<Events />} />} />
        <Route path="/worships" element={<ProtectedRoute element={<Worships />} />} />
        <Route path="/worships/:id" element={<ProtectedRoute element={<WorshipDetail />} />} />
        <Route path="/group-meetings/:id" element={<ProtectedRoute element={<MeetingDetail />} />} />
        <Route path="/account" element={<ProtectedRoute element={<Account />} />} />
      </Route>

      <Route path="/" element={<Navigate to={user ? "/dashboard" : "/signin"} replace />} />
    </Routes>
  );
}
