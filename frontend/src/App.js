import React from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import ErrorBoundary from './components/common/ErrorBoundary';
import Login from './components/auth/Login';
import Dashboard from './components/Dashboard';
import './App.css';

const AppContent = () => {
  const { user } = useAuth();

  return (
      <div className="App">
        {user ? <Dashboard /> : <Login />}
      </div>
  );
};

const App = () => {
  return (
      <ErrorBoundary>
        <AuthProvider>
          <AppContent />
        </AuthProvider>
      </ErrorBoundary>
  );
};

export default App;