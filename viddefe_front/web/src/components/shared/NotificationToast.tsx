import React, { useEffect } from 'react';
import { useNotifications } from '../../hooks/useNotifications';
import { toast } from 'sonner';
import {
  FiCheckCircle,
  FiAlertCircle,
  FiAlertTriangle,
  FiInfo,
} from 'react-icons/fi';

/**
 * NotificationToast - Automatically displays SSE notifications as toasts
 * 
 * Add this component to your main layout (inside AppProvider and Router)
 * It will listen for SSE notifications and display them as toasts
 * 
 * @example
 * <App>
 *   <Toaster />
 *   <NotificationToast />
 *   <Router />
 * </App>
 */
export const NotificationToast: React.FC = () => {
  const { lastNotification } = useNotifications();

  useEffect(() => {
    if (!lastNotification) return;

    const { title, message, type, icon } = lastNotification;
    const toastMessage = title ? `${title}: ${message}` : message;

    // Determine icon based on type
    const getIcon = () => {
      if (icon) {
        return <span className="text-lg">{icon}</span>;
      }

      switch (type) {
        case 'success':
          return <FiCheckCircle className="w-5 h-5" />;
        case 'error':
          return <FiAlertCircle className="w-5 h-5" />;
        case 'warning':
          return <FiAlertTriangle className="w-5 h-5" />;
        case 'info':
        default:
          return <FiInfo className="w-5 h-5" />;
      }
    };

    // Display toast with appropriate type
    const iconElement = getIcon();

    switch (type) {
      case 'success':
        toast.success(toastMessage, {
          icon: iconElement,
          duration: 4000,
        });
        break;
      case 'error':
        toast.error(toastMessage, {
          icon: iconElement,
          duration: 5000,
        });
        break;
      case 'warning':
        toast.warning(toastMessage, {
          icon: iconElement,
          duration: 4000,
        });
        break;
      case 'info':
      default:
        toast.info(toastMessage, {
          icon: iconElement,
          duration: 3000,
        });
        break;
    }
  }, [lastNotification]);

  return null;
};

export default NotificationToast;
