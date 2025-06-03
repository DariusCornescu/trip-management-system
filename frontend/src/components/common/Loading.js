import React from 'react';

const Loading = ({ message = "Loading..." }) => {
    return (
        <div className="flex items-center justify-center py-12">
            <div className="text-center">
                <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mb-4"></div>
                <p className="text-xl text-gray-600">{message}</p>
            </div>
        </div>
    );
};

export default Loading;