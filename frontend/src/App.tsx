import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import type { ReactNode } from 'react';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import { isLoggedIn } from './api/client';

/**
 * This will guard a route by checking authentication at render time.
 * Because this is a component, React calls it on every route match.
 * Therefore, the check re-runs instead of being frozen when App first renders.
 * 
 * @param children (The protected content to render when authenticated.)
 */
function ProtectedRoute({children}: {children: ReactNode}) {
  if (!isLoggedIn()) {
    return <Navigate to="/login" replace/>;
  }
  return <>{children}</>
}

/**
 * This will decide where the root path sends the user.
 * It is a component so the check runs on navigation.
 */
function RootRedirect() {
  return <Navigate to={isLoggedIn() ? '/dashboard' : '/login'} replace/>;
}

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

       {/** The protected route: redirect to login if it is not authenticated. */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />

        {/** The default redirect: authenticated users will go to the dashboard, others to
         * the login.
        */}
        <Route path="/" element={<RootRedirect />} />
      </Routes>
    </BrowserRouter>
  );
}