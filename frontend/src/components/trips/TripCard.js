import React from 'react';

const TripCard = ({ trip, onEdit, onDelete, onMakeReservation, isAdmin }) => {
    const availabilityColor = trip.availableSeats === 0 ? 'text-red-600' :
        trip.availableSeats < 10 ? 'text-yellow-600' : 'text-green-600';

    return (
        <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300">
            <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                    <h3 className="text-xl font-bold text-gray-800">{trip.attractionName}</h3>
                    <span className={`text-sm font-semibold ${availabilityColor}`}>
            {trip.availableSeats} seats
          </span>
                </div>

                <div className="space-y-2 mb-4 text-gray-600">
                    <div className="flex items-center">
                        <span className="mr-2">🚌</span>
                        <span>{trip.transportCompany}</span>
                    </div>
                    <div className="flex items-center">
                        <span className="mr-2">🕒</span>
                        <span>{trip.departureTime}</span>
                    </div>
                    <div className="flex items-center">
                        <span className="mr-2">💰</span>
                        <span className="font-semibold text-green-600">${trip.price}</span>
                    </div>
                </div>

                <div className="flex gap-2 flex-wrap">
                    {!isAdmin && trip.availableSeats > 0 && (
                        <button
                            onClick={() => onMakeReservation(trip)}
                            className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition duration-200"
                        >
                            Book Now
                        </button>
                    )}

                    {isAdmin && (
                        <>
                            <button
                                onClick={() => onEdit(trip)}
                                className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition duration-200"
                            >
                                Edit
                            </button>
                            <button
                                onClick={() => onDelete(trip.id)}
                                className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition duration-200"
                            >
                                Delete
                            </button>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TripCard;