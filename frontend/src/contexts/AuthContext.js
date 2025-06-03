import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        try {
            const savedUser = localStorage.getItem('tripBookingUser');
            if (savedUser) {
                const userData = JSON.parse(savedUser);
                setUser(userData);
            }
        } catch (error) {
            console.error('Error loading saved user:', error);
            localStorage.removeItem('tripBookingUser');
        }
        setLoading(false);
    }, []);

    const login = (userData) => {
        setUser(userData);
        try {
            localStorage.setItem('tripBookingUser', JSON.stringify(userData));
        } catch (error) {
            console.error('Error saving user to localStorage:', error);
        }
    };

    const logout = () => {
        setUser(null);
        try {
            localStorage.removeItem('tripBookingUser');
        } catch (error) {
            console.error('Error removing user from localStorage:', error);
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="text-center">
                    <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mb-4"></div>
                    <p className="text-xl text-gray-600">Loading...</p>
                </div>
            </div>
        );
    }

    const value = {
        user,
        login,
        logout,
        isAdmin: user?.isAdmin || false,
        isAuthenticated: !!user
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};