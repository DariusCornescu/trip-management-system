import React from 'react';
import TripCard from './TripCard';

const TripList = ({ trips, onEdit, onDelete, onMakeReservation, isAdmin, loading }) => {
    if (loading) {
        return (
            <div className="text-center py-12">
                <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                <p className="mt-2 text-gray-600">Loading trips...</p>
            </div>
        );
    }

    if (trips.length === 0) {
        return (
            <div className="text-center py-12">
                <div className="text-6xl mb-4">🏝️</div>
                <h3 className="text-xl font-medium text-gray-900 mb-2">No trips available</h3>
                <p className="text-gray-600">
                    There are no trips to display at the moment.
                </p>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {trips.map((trip) => (
                <TripCard
                    key={trip.id}
                    trip={trip}
                    onEdit={onEdit}
                    onDelete={onDelete}
                    onMakeReservation={onMakeReservation}
                    isAdmin={isAdmin}
                />
            ))}
        </div>
    );
};

export default TripList;