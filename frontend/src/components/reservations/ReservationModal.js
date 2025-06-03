import React, { useState } from 'react';

const ReservationModal = ({ trip, onClose, onSubmit }) => {
    const [formData, setFormData] = useState({customerName: '', customerPhone: '', tickets: 1 });

    const [errors, setErrors] = useState({});

    const validateForm = () => {
        const newErrors = {};
        if (!formData.customerName.trim()) { newErrors.customerName = 'Customer name is required'; }
        if (!formData.customerPhone.trim()) { newErrors.customerPhone = 'Phone number is required';}
        if (formData.tickets <= 0 || formData.tickets > trip.availableSeats) { newErrors.tickets = `Tickets must be between 1 and ${trip.availableSeats}`; }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!validateForm()) { return; }
        onSubmit(trip.id, formData.customerName, formData.customerPhone, parseInt(formData.tickets));
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));

        if (errors[name]) {
            setErrors(prev => ({...prev, [name]: '' }));
        }
    };

    const handleBackdropClick = (e) => {
        if (e.target === e.currentTarget) { onClose(); }
    };

    return (
        <div
            className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
            onClick={handleBackdropClick}
        >
            <div className="bg-white rounded-lg max-w-md w-full p-6">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-2xl font-bold text-gray-800">Make Reservation</h2>
                    <button
                        onClick={onClose}
                        className="text-gray-500 hover:text-gray-700 text-2xl font-bold"
                    >
                        ×
                    </button>
                </div>

                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
                    <h3 className="font-semibold text-blue-800">{trip.attractionName}</h3>
                    <p className="text-blue-600 text-sm">Price: ${trip.price} per person</p>
                    <p className="text-blue-600 text-sm">Available: {trip.availableSeats} seats</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Your Name *
                        </label>
                        <input
                            type="text"
                            name="customerName"
                            value={formData.customerName}
                            onChange={handleInputChange}
                            className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                                errors.customerName ? 'border-red-500' : 'border-gray-300'
                            }`}
                            placeholder="Enter your full name"
                            required
                        />
                        {errors.customerName && <p className="text-red-500 text-xs mt-1">{errors.customerName}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Phone Number *
                        </label>
                        <input
                            type="tel"
                            name="customerPhone"
                            value={formData.customerPhone}
                            onChange={handleInputChange}
                            className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                                errors.customerPhone ? 'border-red-500' : 'border-gray-300'
                            }`}
                            placeholder="Enter your phone number"
                            required
                        />
                        {errors.customerPhone && <p className="text-red-500 text-xs mt-1">{errors.customerPhone}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Number of Tickets *
                        </label>
                        <select
                            name="tickets"
                            value={formData.tickets}
                            onChange={handleInputChange}
                            className={`w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                                errors.tickets ? 'border-red-500' : 'border-gray-300'
                            }`}
                            required
                        >
                            {[...Array(Math.min(trip.availableSeats, 10))].map((_, i) => (
                                <option key={i + 1} value={i + 1}>
                                    {i + 1} ticket{i > 0 ? 's' : ''}
                                </option>
                            ))}
                        </select>
                        {errors.tickets && <p className="text-red-500 text-xs mt-1">{errors.tickets}</p>}
                    </div>

                    <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
                        <div className="flex justify-between text-sm">
                            <span>Total Cost:</span>
                            <span className="font-semibold">${(trip.price * formData.tickets).toFixed(2)}</span>
                        </div>
                    </div>

                    <div className="flex gap-3 pt-4">
                        <button
                            type="submit"
                            className="flex-1 bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200"
                        >
                            Confirm Booking
                        </button>
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200"
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ReservationModal;