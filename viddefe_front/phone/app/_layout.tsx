// app/_layout.tsx (RootLayout)
import { paperDarkTheme, paperLightTheme } from "@/constants/theme";
import { AuthProvider, useAuth } from "@/context/AuthContext";
import { useColorScheme } from "@/hooks/use-color-scheme";
import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import { PaperProvider } from "react-native-paper";

// 👇 Encapsulamos la lógica de auth
function AuthStack() {
  const { user, loading } = useAuth();

  if (loading) return null; // o un splash/loading screen

  // 🔹 Si no hay usuario → manda a SignIn
  if (!user) {
    return (
      <Stack>
        <Stack.Screen
          name="Auth/sign-in"
          options={{ headerShown: false }}
        />
        <Stack.Screen
          name="Auth/sign-up"
          options={{ headerShown: false }}
        />
      </Stack>
    );
  }

  // 🔹 Si hay usuario → app normal (tabs, modals, etc.)
  return (
    <Stack>
      <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      <Stack.Screen
        name="modal"
        options={{ presentation: "modal", title: "Modal" }}
      />
    </Stack>
  );
}

export default function RootLayout() {
  const colorScheme = useColorScheme();
  const theme = colorScheme === "dark" ? paperDarkTheme : paperLightTheme;

  return (
    <AuthProvider>
      <PaperProvider theme={theme}>
        <AuthStack />
        <StatusBar style={colorScheme === "dark" ? "light" : "dark"} />
      </PaperProvider>
    </AuthProvider>
  );
}
