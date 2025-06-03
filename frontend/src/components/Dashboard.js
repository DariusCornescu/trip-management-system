import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { tripAPI, reservationAPI, userAPI } from '../services/api';
import Header from './common/Header';
import TripList from './trips/TripList';
import TripForm from './trips/TripForm';
import ReservationModal from './reservations/ReservationModal';
import UserManagement from './admin/UserManagement';

const Dashboard = () => {
    const { user } = useAuth();

    const [trips, setTrips] = useState([]);
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showTripForm, setShowTripForm] = useState(false);
    const [editingTrip, setEditingTrip] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedTrip, setSelectedTrip] = useState(null);
    const [activeTab, setActiveTab] = useState('trips');

    useEffect(() => {
        if (activeTab === 'trips') {
            loadTrips();
        } else if (activeTab === 'users') {
            loadUsers();
        }
    }, [activeTab]);

    const showMessage = (msg, isError = false) => {
        if (isError) {
            setError(msg);
            setTimeout(() => setError(null), 5000);
        } else {
            alert(msg);
            setError(null);
        }
    };

    const loadTrips = async () => {
        setLoading(true);
        try {
            const data = await tripAPI.getAllTrips();
            setTrips(Array.isArray(data) ? data : []);
        } catch (err) {
            showMessage('Failed to load trips. Is the API server running on port 8080?', true);
            console.error('Load trips error:', err);
        } finally {
            setLoading(false);
        }
    };

    const loadUsers = async () => {
        setLoading(true);
        try {
            const data = await userAPI.getAllUsers();
            setUsers(Array.isArray(data) ? data : []);
        } catch (err) {
            showMessage('Failed to load users. Is the API server running on port 8080?', true);
            console.error('Load users error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateOrUpdate = async (tripData) => {
        setLoading(true);
        try {
            if (editingTrip) {
                await tripAPI.updateTrip(tripData.id, tripData);
                showMessage('Trip updated successfully!');
            } else {
                await tripAPI.createTrip(tripData.id, tripData);
                showMessage('Trip created successfully!');
            }
            setShowTripForm(false);
            setEditingTrip(null);
            loadTrips();
        } catch (err) {
            showMessage('Failed to save trip. Check your input and ensure the backend is running.', true);
            console.error('Save trip error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleUserCreateOrUpdate = async (userData) => {
        setLoading(true);
        try {
            if (userData.isEditing) {
                const response = await userAPI.updateUser(userData.id, userData.username, userData.password);
                if (response && response.success !== false) {
                    showMessage('User updated successfully!');
                } else {
                    showMessage(response?.message || 'Failed to update user', true);
                    return;
                }
            } else {
                const response = await userAPI.createUser(userData.id, userData.username, userData.password);
                if (response && response.success !== false) {
                    showMessage('User created successfully!');
                } else {
                    showMessage(response?.message || 'Failed to create user', true);
                    return;
                }
            }
            loadUsers();
        } catch (err) {
            showMessage('Failed to save user. Check your input and ensure the backend is running.', true);
            console.error('Save user error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleUserDelete = async (userId) => {
        if (window.confirm('Delete this user?')) {
            setLoading(true);
            try {
                await userAPI.deleteUser(userId);
                showMessage('User deleted successfully!');
                loadUsers();
            } catch (err) {
                showMessage('Failed to delete user.', true);
                console.error('Delete user error:', err);
            } finally {
                setLoading(false);
            }
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Delete this trip?')) {
            setLoading(true);
            try {
                await tripAPI.deleteTrip(id);
                showMessage('Trip deleted successfully!');
                loadTrips();
            } catch (err) {
                showMessage('Delete failed.', true);
                console.error('Delete trip error:', err);
            } finally {
                setLoading(false);
            }
        }
    };

    const handleEdit = (trip) => {
        setEditingTrip(trip);
        setShowTripForm(true);
    };

    const handleSearch = async () => {
        if (!searchTerm.trim()) return loadTrips();

        setLoading(true);
        try {
            const data = await tripAPI.searchByAttraction(searchTerm);
            setTrips(Array.isArray(data) ? data : []);
        } catch (err) {
            showMessage('Search failed.', true);
            console.error('Search error:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleMakeReservation = async (tripId, name, phone, tickets) => {
        try {
            const res = await reservationAPI.makeReservation(tripId, name, phone, tickets);
            if (res.success) {
                showMessage('Reservation confirmed!');
                setSelectedTrip(null);
                loadTrips();
            } else {
                showMessage('Reservation failed: ' + res.message, true);
            }
        } catch (err) {
            showMessage('Reservation error. Try again.', true);
            console.error('Reservation error:', err);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') handleSearch();
    };

    if (showTripForm) {
        return (
            <div className="min-h-screen bg-gray-100">
                <Header />
                <main className="max-w-4xl mx-auto py-6 px-4">
                    <TripForm
                        tripToEdit={editingTrip}
                        onSubmit={handleCreateOrUpdate}
                        onCancel={() => {
                            setShowTripForm(false);
                            setEditingTrip(null);
                        }}
                    />
                </main>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-100">
            <Header />
            <main className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex">
                        <span className="mr-2">⚠️</span>{error}
                    </div>
                )}

                <div className="mb-6">
                    <nav className="flex space-x-8">
                        <button
                            onClick={() => setActiveTab('trips')}
                            className={`py-2 px-1 border-b-2 font-medium text-sm ${
                                activeTab === 'trips'
                                    ? 'border-blue-500 text-blue-600'
                                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                            }`}
                        >
                            {user.isAdmin ? '🚀 Trip Management' : '🏝️ Available Trips'}
                        </button>
                        {user.isAdmin && (
                            <button
                                onClick={() => setActiveTab('users')}
                                className={`py-2 px-1 border-b-2 font-medium text-sm ${
                                    activeTab === 'users'
                                        ? 'border-blue-500 text-blue-600'
                                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                                }`}
                            >
                                👥 User Management
                            </button>
                        )}
                    </nav>
                </div>

                {activeTab === 'trips' && (
                    <>
                        <div className="flex justify-between items-center mb-8 gap-4 flex-wrap">
                            {user.isAdmin && (
                                <button
                                    onClick={() => setShowTripForm(true)}
                                    className="bg-green-500 hover:bg-green-600 text-white py-2 px-4 rounded-lg transition duration-200"
                                >
                                    ➕ Create New Trip
                                </button>
                            )}

                            <div className="flex gap-2 max-w-md">
                                <input
                                    type="text"
                                    placeholder="Search attractions…"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    onKeyPress={handleKeyPress}
                                    className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
                                <button
                                    onClick={handleSearch}
                                    className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition duration-200"
                                >🔍</button>
                                <button
                                    onClick={() => {
                                        setSearchTerm('');
                                        loadTrips();
                                    }}
                                    className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg transition duration-200"
                                >Clear</button>
                            </div>
                        </div>

                        <TripList
                            trips={trips}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                            onMakeReservation={setSelectedTrip}
                            isAdmin={user.isAdmin}
                            loading={loading}
                        />
                    </>
                )}

                {activeTab === 'users' && user.isAdmin && (
                    <UserManagement
                        users={users}
                        loading={loading}
                        onCreateOrUpdate={handleUserCreateOrUpdate}
                        onDelete={handleUserDelete}
                        onRefresh={loadUsers}
                    />
                )}
            </main>

            {selectedTrip && (
                <ReservationModal
                    trip={selectedTrip}
                    onClose={() => setSelectedTrip(null)}
                    onSubmit={handleMakeReservation}
                />
            )}
        </div>
    );
};

export default Dashboard;