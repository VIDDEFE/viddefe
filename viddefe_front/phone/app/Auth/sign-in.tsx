import AuthForm from "@/components/AuthFormComponent";
import { useAuth } from "@/context/AuthContext";
import { useNavigation } from "@react-navigation/native";
import React from "react";
import { KeyboardAvoidingView, Platform, StyleSheet, TouchableOpacity, View } from "react-native";
import { Button, Text, TextInput, useTheme } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";

export default function SignInScreen() {
  const [email, setEmail] = React.useState("");
  const [password, setPassword] = React.useState("");
  const { signIn } = useAuth();
  const theme = useTheme();
  const navigation = useNavigation();

  return (
    <SafeAreaView
      style={{ flex: 1, backgroundColor: theme.colors.background }}
      edges={["top", "left", "right"]}
    >
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
      >
        <AuthForm
          title="Iniciar Sesión"
          image={require("@/assets/images/viddefe_logo.png")}
        >
          <TextInput
            label="Correo"
            value={email}
            onChangeText={setEmail}
            mode="outlined"
            outlineColor={theme.colors.outline}
            activeOutlineColor={theme.colors.primary}
            style={styles.input}
          />
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

          <Button
            mode="contained"
            onPress={() => signIn(email, password)}
            style={{ marginTop: 8 }}
          >
            Entrar
          </Button>

          <View
            style={{ flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}
          >
            <TouchableOpacity
              onPress={() => navigation.navigate("Auth/sign-up" as never)}
              style={{ marginTop: 8, padding: 8 }}
            >
              <Text
                style={{
                  color: theme.colors.secondary,
                  textAlign: "center",
                  flexWrap: "wrap",
                }}
              >
                ¿Tu congregación no está registrada? Inscríbela!
              </Text>
            </TouchableOpacity>
            <Button
              mode="text"
              textColor={theme.colors.secondary}
              onPress={ () =>
                console.log("Logica de recuperación de contraseña")
              }
            >
              ¿Olvidaste tu contraseña?
            </Button>
          </View>
        </AuthForm>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  input: {
    marginBottom: 12,
  },
});
