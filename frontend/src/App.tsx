import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import { isLoggedIn } from './api/client';

/**
 * The App component.
 * This defines all routes and handles authentication-based redirects.
 */
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/** Public routes - always accessible. */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/** The protected route - redirect to login if it is not authenticated. */}
        <Route
          path="/dashboard"
          element={isLoggedIn() ? <Dashboard /> : <Navigate to="/login" />}
        />

        {/** The default redirect: authenticated users will go to the dashboard, others to the login.
       * 
       */}
        <Route
          path="/"
          element={<Navigate to={isLoggedIn() ? '/dashboard' : '/login'} />}
        />
      </Routes>
    </BrowserRouter>
  );
}