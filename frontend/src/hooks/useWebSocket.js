import { useEffect, useCallback } from 'react';
import webSocketService from '../services/websocket';

export const useWebSocket = (events) => {
    useEffect(() => {
        const unsubscribers = [];

        // Subscribe to events
        Object.entries(events).forEach(([event, handler]) => {
            const unsubscribe = webSocketService.subscribe(event, handler);
            unsubscribers.push(unsubscribe);
        });

        // Cleanup
        return () => {
            unsubscribers.forEach(unsubscribe => unsubscribe());
        };
    }, [events]);

    const sendMessage = useCallback((type, data) => {
        webSocketService.send(type, data);
    }, []);

    return { sendMessage };
};