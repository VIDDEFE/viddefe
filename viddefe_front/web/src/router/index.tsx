import { Routes, Route } from 'react-router-dom';
import SignIn from '../views/signin';

export default function Router() {
  return (   
      <Routes>
        <Route path="/sigin" element={<SignIn />} />
      </Routes>
  );
}
