const API_BASE_URL = 'http://localhost:8080/api';

const apiCall = async (url, options = {}) => {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
    };

    const config = { ...defaultOptions, ...options };

    if (config.body && typeof config.body === 'object') {
        config.body = JSON.stringify(config.body);
    }

    try {
        const response = await fetch(url, config);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return await response.text();
    } catch (error) {
        console.error('API call failed:', error);
        throw error;
    }
};

// Authentication API
export const authAPI = {
    login: async (username, password) => {
        console.log('Attempting login with:', username);
        console.log('API URL:', `${API_BASE_URL}/auth/login`);

        try {
            const response = await apiCall(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                body: { username, password }
            });
            console.log('Login response:', response);
            return response;
        } catch (error) {
            console.error('Login API error:', error);
            throw error;
        }
    }
};

// Trip API
export const tripAPI = {
    getAllTrips: async () => {
        return await apiCall(`${API_BASE_URL}/trips`);
    },

    getTripById: async (id) => {
        return await apiCall(`${API_BASE_URL}/trips/${id}`);
    },

    createTrip: async (id, trip) => {
        return await apiCall(`${API_BASE_URL}/trips/${id}`, {
            method: 'POST',
            body: trip
        });
    },

    updateTrip: async (id, trip) => {
        return await apiCall(`${API_BASE_URL}/trips/${id}`, {
            method: 'PUT',
            body: trip
        });
    },

    deleteTrip: async (id) => {
        return await apiCall(`${API_BASE_URL}/trips/${id}`, {
            method: 'DELETE'
        });
    },

    searchByAttraction: async (attraction) => {
        const params = new URLSearchParams({ attraction });
        return await apiCall(`${API_BASE_URL}/trips/search/attraction?${params}`);
    },

    searchByAttractionAndTime: async (attraction, startTime, endTime) => {
        const params = new URLSearchParams({ attraction, startTime, endTime });
        return await apiCall(`${API_BASE_URL}/trips/search/time?${params}`);
    }
};

// Reservation API
export const reservationAPI = {
    makeReservation: async (tripId, customerName, customerPhone, tickets) => {
        return await apiCall(`${API_BASE_URL}/reservations`, {
            method: 'POST',
            body: { tripId, customerName, customerPhone, tickets }
        });
    },

    getReservationsByTrip: async (tripId) => {
        return await apiCall(`${API_BASE_URL}/reservations/trip/${tripId}`);
    },

    cancelReservation: async (id) => {
        return await apiCall(`${API_BASE_URL}/reservations/${id}`, {
            method: 'DELETE'
        });
    }
};

// User API (Admin only)
export const userAPI = {
    getAllUsers: async () => {
        return await apiCall(`${API_BASE_URL}/auth/users`);
    },

    createUser: async (id, username, password) => {
        return await apiCall(`${API_BASE_URL}/auth/users`, {
            method: 'POST',
            body: { id, username, password }
        });
    },

    updateUser: async (id, username, password) => {
        return await apiCall(`${API_BASE_URL}/auth/users/${id}`, {
            method: 'PUT',
            body: { username, password }
        });
    },

    deleteUser: async (id) => {
        return await apiCall(`${API_BASE_URL}/auth/users/${id}`, {
            method: 'DELETE'
        });
    },

    searchUsers: async (username) => {
        const params = new URLSearchParams({ username });
        return await apiCall(`${API_BASE_URL}/auth/users/search?${params}`);
    }
};