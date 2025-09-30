// screens/Auth/SignUpScreen.tsx
import AuthForm from "@/components/AuthFormComponent";
import React from "react";
import { StyleSheet } from "react-native";
import { Button, Text, TextInput, useTheme } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";

export default function SignUpScreen() {
  const theme = useTheme();
  const [step, setStep] = React.useState(0);

  // Datos
  const [name, setName] = React.useState("");
  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");

  const steps = [
    {
      title: "Datos Personales",
      content: (
        <>
          <TextInput
            label="Nombre"
            value={name}
            onChangeText={setName}
            mode="outlined"
            outlineColor={theme.colors.outline}
            activeOutlineColor={theme.colors.primary}
            style={styles.input}
          />
          <TextInput
            label="Correo"
            value={email}
            onChangeText={setEmail}
            mode="outlined"
            outlineColor={theme.colors.outline}
            activeOutlineColor={theme.colors.primary}
            style={styles.input}
          />
        </>
      ),
    },
    {
      title: "Credenciales",
      content: (
        <>
          <TextInput
            label="Contraseña"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            mode="outlined"
            outlineColor={theme.colors.outline}
            activeOutlineColor={theme.colors.primary}
            style={styles.input}
          />
        </>
      ),
    },
    {
      title: "Confirmación",
      content: (
        <>
          <Text style={{ color: theme.colors.onSurface, marginBottom: 8 }}>
            Nombre: {name}
          </Text>
          <Text style={{ color: theme.colors.onSurface }}>
            Email: {email}
          </Text>
        </>
      ),
    },
  ];

  const handleNext = () => {
    if (step < steps.length - 1) setStep(step + 1);
    else {
      // Aquí llamas a tu signUp del contexto
      console.log("Registrar usuario", { name, email, password });
    }
  };

  const handleBack = () => {
    if (step > 0) setStep(step - 1);
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: theme.colors.background }}>
      <AuthForm
        title={steps[step].title}
        image={require("@/assets/images/viddefe_logo.png")}
      >
        {steps[step].content}

        <Button
          mode="contained"
          onPress={handleNext}
          style={{ marginTop: 16 }}
        >
          {step === steps.length - 1 ? "Registrar" : "Siguiente"}
        </Button>

        {step > 0 && (
          <Button onPress={handleBack} style={{ marginTop: 8 }}>
            Atrás
          </Button>
        )}
      </AuthForm>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  input: {
    marginBottom: 12,
  },
});
