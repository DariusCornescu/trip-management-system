import React, { useState, useEffect } from 'react';

const TripForm = ({ tripToEdit, onSubmit, onCancel }) => {
    const [trip, setTrip] = useState({id: '', attractionName: '', transportCompany: '', departureTime: '', price: '',  availableSeats: '' });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (tripToEdit) { setTrip(tripToEdit); }
    }, [tripToEdit]);

    const validateForm = () => {
        const newErrors = {};
        if (!trip.id) newErrors.id = 'Trip ID is required';
        if (!trip.attractionName) newErrors.attractionName = 'Attraction name is required';
        if (!trip.transportCompany) newErrors.transportCompany = 'Transport company is required';
        if (!trip.departureTime) newErrors.departureTime = 'Departure time is required';
        if (!trip.price) newErrors.price = 'Price is required';
        if (!trip.availableSeats) newErrors.availableSeats = 'Available seats is required';
        if (trip.price && (isNaN(trip.price) || trip.price <= 0)) { newErrors.price = 'Price must be a positive number'; }
        if (trip.availableSeats && (isNaN(trip.availableSeats) || trip.availableSeats < 0)) { newErrors.availableSeats = 'Available seats must be a non-negative number'; }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!validateForm()) { return; }
        onSubmit({...trip, id: parseInt(trip.id), price: parseFloat(trip.price), availableSeats: parseInt(trip.availableSeats) });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setTrip(prev => ({ ...prev, [name]: value}));

        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
    };

    return (
        <div className="bg-white rounded-lg shadow-lg p-6 max-w-lg mx-auto">
            <h2 className="text-2xl font-bold text-gray-800 mb-6">
                {tripToEdit ? 'Edit Trip' : 'Create New Trip'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Trip ID *
                    </label>
                    <input
                        type="number"
                        name="id"
                        value={trip.id}
                        onChange={handleInputChange}
                        className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                            errors.id ? 'border-red-500' : 'border-gray-300'
                        }`}
                        disabled={!!tripToEdit}
                        required
                    />
                    {errors.id && <p className="text-red-500 text-xs mt-1">{errors.id}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Attraction Name *
                    </label>
                    <input
                        type="text"
                        name="attractionName"
                        value={trip.attractionName}
                        onChange={handleInputChange}
                        className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                            errors.attractionName ? 'border-red-500' : 'border-gray-300'
                        }`}
                        placeholder="e.g., Eiffel Tower"
                        required
                    />
                    {errors.attractionName && <p className="text-red-500 text-xs mt-1">{errors.attractionName}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Transport Company *
                    </label>
                    <input
                        type="text"
                        name="transportCompany"
                        value={trip.transportCompany}
                        onChange={handleInputChange}
                        className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                            errors.transportCompany ? 'border-red-500' : 'border-gray-300'
                        }`}
                        placeholder="e.g., Paris Tours"
                        required
                    />
                    {errors.transportCompany && <p className="text-red-500 text-xs mt-1">{errors.transportCompany}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Departure Time *
                    </label>
                    <input
                        type="text"
                        name="departureTime"
                        value={trip.departureTime}
                        onChange={handleInputChange}
                        className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                            errors.departureTime ? 'border-red-500' : 'border-gray-300'
                        }`}
                        placeholder="e.g., 10:00 - 2025-03-22"
                        required
                    />
                    {errors.departureTime && <p className="text-red-500 text-xs mt-1">{errors.departureTime}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Price ($) *
                    </label>
                    <input
                        type="number"
                        step="0.01"
                        name="price"
                        value={trip.price}
                        onChange={handleInputChange}
                        className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                            errors.price ? 'border-red-500' : 'border-gray-300'
                        }`}
                        placeholder="e.g., 150.00"
                        required
                    />
                    {errors.price && <p className="text-red-500 text-xs mt-1">{errors.price}</p>}
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                        Available Seats *
                    </label>
                    <input
                        type="number"
                        name="availableSeats"
                        value={trip.availableSeats}
                        onChange={handleInputChange}
                        className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                            errors.availableSeats ? 'border-red-500' : 'border-gray-300'
                        }`}
                        placeholder="e.g., 50"
                        required
                    />
                    {errors.availableSeats && <p className="text-red-500 text-xs mt-1">{errors.availableSeats}</p>}
                </div>

                <div className="flex gap-3 pt-4">
                    <button
                        type="submit"
                        className="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200"
                    >
                        {tripToEdit ? 'Update Trip' : 'Create Trip'}
                    </button>
                    <button
                        type="button"
                        onClick={onCancel}
                        className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200"
                    >
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default TripForm;