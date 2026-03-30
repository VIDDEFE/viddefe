import { useEffect } from 'react';
import { useNotifications } from './useNotifications';
import { useAppContext } from '../context/AppContext';

/**
 * Hook to automatically manage SSE connection based on authentication state
 * 
 * Add this hook in a component that wraps your authenticated routes (e.g., main layout)
 * It will automatically connect when user logs in and disconnect when they log out
 * 
 * @example
 * function MainLayout() {
 *   useSSEConnection();
 *   return <Outlet />;
 * }
 */
export function useSSEConnection(): void {
  const { connect, disconnect } = useNotifications();
  const { user } = useAppContext();

  useEffect(() => {
    console.log('🎯 useSSEConnection: Checking user authentication state', { user });
    if (user?.userId) {
      console.log('🎯 useSSEConnection: User logged in, connecting to SSE');
      connect(user.userId);
    } else {
      console.log('🎯 useSSEConnection: User logged out, disconnecting from SSE');
      disconnect();
    }

    // Cleanup on unmount
    return () => {
      // Don't disconnect on unmount - keep connection alive
      // Only disconnect on logout (when user becomes null)
    };
  }, [user?.userId, connect, disconnect]);
}

export default useSSEConnection;
