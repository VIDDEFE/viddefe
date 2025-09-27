
/**
 * Below are the colors that are used in the app. The colors are defined in the light and dark mode.
 * Using the custom palette (primary, secondary, neutral).
 */

import { Platform } from 'react-native';
import colors from './colors';

export const Colors = {
  light: {
    text: colors.neutral[900],
    background: '#ffffff',
    tint: colors.primary[500],
    icon: colors.neutral[500],
    tabIconDefault: colors.neutral[400],
    tabIconSelected: colors.primary[500],
    primary: colors.primary[500],
    secondary: colors.secondary[400],
  },
  dark: {
    text: colors.neutral[50],
    background: colors.neutral[950],
    tint: colors.primary[300],
    icon: colors.neutral[400],
    tabIconDefault: colors.neutral[600],
    tabIconSelected: colors.primary[300],
    primary: colors.primary[300],
    secondary: colors.secondary[300],
  },
};

export const Fonts = Platform.select({
  ios: {
    // Clásicos y limpios
    sans: 'system-ui',
    serif: 'Times New Roman',
    rounded: 'SF Pro Rounded',
    mono: 'Menlo',

    // Extras para títulos
    display: 'Avenir Next',
  },
  default: {
    sans: 'sans-serif',
    serif: 'serif',
    rounded: 'sans-serif-rounded',
    mono: 'monospace',

    // Extras para títulos
    display: 'sans-serif-medium',
  },
  web: {
    sans: "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
    serif: "Georgia, 'Times New Roman', serif",
    rounded: "'SF Pro Rounded', 'Hiragino Maru Gothic ProN', Meiryo, 'MS PGothic', sans-serif",
    mono: "SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace",

    // Fuentes más “cheveres” para títulos
    display: "'Poppins', 'Montserrat', 'Raleway', 'Nunito', sans-serif",
  },
});