import {
  DarkTheme as NavigationDarkTheme,
  DefaultTheme as NavigationDefaultTheme,
} from "@react-navigation/native";
import { adaptNavigationTheme, MD3DarkTheme, MD3LightTheme } from "react-native-paper";
import colors from "./colors";

// Tipografía multiplataforma
/*export const Fonts = Platform.select({
  ios: {
    sans: "system-ui",
    serif: "ui-serif",
    rounded: "ui-rounded",
    mono: "ui-monospace",
  },
  android: {
    sans: "normal",
    serif: "serif",
    rounded: "sans-serif-rounded",
    mono: "monospace",
  },
  web: {
    sans: "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
    serif: "Georgia, 'Times New Roman', serif",
    rounded: "'SF Pro Rounded', 'Hiragino Maru Gothic ProN', Meiryo, sans-serif",
    mono: "SFMono-Regular, Menlo, Monaco, Consolas, monospace",
  },
});*/

// Adaptamos Navigation a Paper
const { LightTheme, DarkTheme } = adaptNavigationTheme({
  reactNavigationLight: NavigationDefaultTheme,
  reactNavigationDark: NavigationDarkTheme,
});

// Tema claro
export const paperLightTheme = {
  ...MD3LightTheme,
  roundness: 8,
  colors: {
    ...MD3LightTheme.colors,
    ...LightTheme.colors,
    primary: colors.primary[700],
    onPrimary: colors.neutral[50],
    secondary: colors.neutral[400],
    onSecondary: colors.neutral[50],
    background: colors.primary[50],
    onBackground: colors.primary[900],
    surface: colors.primary[50],
    onSurface: colors.primary[900],
    outline: colors.primary[400],
  },
};

// Tema oscuro optimizado
export const paperDarkTheme = {
  ...MD3DarkTheme,
  roundness: 8,
  colors: {
    ...MD3DarkTheme.colors,
    ...DarkTheme.colors,
    primary: colors.primary[500],      // azul vibrante (acento)
    onPrimary: colors.neutral[50],     // texto blanco/gris claro sobre azul
    secondary: colors.neutral[500],    // gris medio como color secundario
    onSecondary: colors.neutral[50],   // texto claro sobre gris
    background: colors.neutral[950],   // negro casi puro
    onBackground: colors.neutral[50],  // texto blanco
    surface: colors.neutral[900],      // gris muy oscuro (tarjetas, contenedores)
    onSurface: colors.neutral[100],    // texto gris claro
    outline: colors.neutral[400],      // gris medio para bordes
  },
};
