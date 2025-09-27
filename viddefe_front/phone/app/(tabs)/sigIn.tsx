import colors from "@/constants/colors";
import * as React from "react";
import { StyleSheet, View } from "react-native";
import { Button, Text, TextInput } from "react-native-paper";
import AuthLayout from "../../components/ui/AuthForm";
import { Colors } from "../../constants/theme";

export default function SignInScreen() {
  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [error, setError] = React.useState(""); // 👈 estado de error
  const [loading, setLoading] = React.useState(false); // 👈 opcional: para mostrar loading

  // Función fake que simula llamada a API
  const fakeLoginApi = async (email: string, password: string) => {
    return new Promise<{ success: boolean; message?: string }>((resolve) => {
      setTimeout(() => {
        if (email === "test@correo.com" && password === "123456") {
          resolve({ success: true });
        } else {
          resolve({ success: false, message: "Email o contraseña incorrectos" });
        }
      }, 1500); // simula retraso
    });
  };

  const handleLogin = async () => {
    setError(""); // resetear error
    setLoading(true);
    const response = await fakeLoginApi(email, password);
    setLoading(false);

    if (response.success) {
      console.log("🔑 Login exitoso", { email, password });
    } else {
      setError(response.message || "Ocurrió un error desconocido");
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: "center", padding: 10, backgroundColor: Colors.light.background }}>
      <AuthLayout title="Inicia sesión">
        <View style={styles.container}>
          <Text style={styles.subtitle}>
             Bienvenido de vuelta, ¡te hemos extrañado!
          </Text>

          <TextInput
            label="Email"
            value={email}
            onChangeText={setEmail}
            autoCapitalize="none"
            keyboardType="email-address"
            mode="outlined"
            style={styles.input}
            outlineColor={colors.primary[200]}
            activeOutlineColor={Colors.light.primary}
          />

          <TextInput
            label="Contraseña"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            mode="outlined"
            style={styles.input}
            outlineColor={colors.primary[200]}
            activeOutlineColor={Colors.light.primary}
          />

          {error ? <Text style={styles.errorText}>{error}</Text> : null}

          <Button
            mode="contained"
            onPress={handleLogin}
            style={styles.button}
            labelStyle={styles.buttonLabel}
            loading={loading} // 👈 opcional: botón muestra loading
          >
            Ingresar
          </Button>

          <View>
            <Button
              mode="text"
              onPress={() => console.log("👉 Ir a registro")}
              textColor={Colors.light.secondary}
              style={{ flexWrap: "wrap", alignSelf: "center", maxWidth: 250 }}
              labelStyle={{ textAlign: "center", fontSize: 13, flexWrap: "wrap" }}
            >
              Registra tu iglesia
            </Button>
            <Button
              mode="text"
              onPress={() => console.log("👉 Olvidaste tu contraseña")}
              textColor={Colors.light.secondary}
              style={{ flexWrap: "wrap", alignSelf: "center", maxWidth: 250 }}
              labelStyle={{ textAlign: "center", fontSize: 13, flexWrap: "wrap" }}
            >
              ¿Olvidaste tu contraseña?
            </Button>
          </View>
        </View> 
      </AuthLayout>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    width: "100%",
    gap: 16,
    marginTop: 10,
  },
  subtitle: {
    fontSize: 12,
    color: Colors.light.primary,
    marginBottom: 12,
    textAlign: "center",
  },
  input: {
    backgroundColor: "#fff",
    borderRadius: 10
  },
  button: {
    marginTop: 10,
    borderRadius: 8,
    paddingVertical: 6,
    backgroundColor: Colors.light.primary
  },
  buttonLabel: {
    fontSize: 16,
    fontWeight: "600",
  },
  errorText: {
    color: "#ff4d4f",
    fontSize: 13,
    textAlign: "center",
    marginTop: -8,
    marginBottom: 8,
  },
});
