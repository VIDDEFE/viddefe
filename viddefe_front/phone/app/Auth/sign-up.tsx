// screens/Auth/SignUpScreen.tsx
import { addPeople } from "@/api/People";
import AuthForm from "@/components/AuthFormComponent";
import PersonalDataForm from "@/components/PersonalDataForm";
import useGeoData from "@/hooks/StatesGeo/StatesGeo";
import { Person } from "@/types/People";
import React from "react";
import { KeyboardAvoidingView, Platform, ScrollView, StyleSheet } from "react-native";
import { Button, Text, TextInput, useTheme } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";

export default function SignUpScreen() {
  const theme = useTheme();
  const [step, setStep] = React.useState(0);

  const [person, setPerson] = React.useState<Person | null>(null);
  const [password, setPassword] = React.useState("");
  const [showDatePicker, setShowDatePicker] = React.useState(false);

  const [user, setUser ] = React.useState();

  const {
    states,
    cities,
    selectedState,
    setSelectedState,
    selectedCity,
    setSelectedCity,
  } = useGeoData();

  const handleDateChange = (_event: any, selectedDate?: Date) => {
    setShowDatePicker(Platform.OS === "ios"); // en iOS puede quedarse abierto
    if (selectedDate) {
      setPerson((prev) => ({ ...prev, birthDate: selectedDate } as Person));
    }
  };

  const steps = [
    {
      title: "Datos Personales",
      content: (
        <PersonalDataForm
          person={person}
          setPerson={setPerson}
          showDatePicker={showDatePicker}
          setShowDatePicker={setShowDatePicker}
          handleDateChange={handleDateChange}
          theme={theme}
          states={states}
          cites={cities}
          selectedState={selectedState}
          selectedCity={selectedCity}
          setSelectedState={setSelectedState}
          setSelectedCity={setSelectedCity}
        />
      ),
    },
    {
      title: "Credenciales",
      content: (
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
      ),
    },
    {
      title: "Confirmación",
      content: (
        <>
          <Text style={{ color: theme.colors.onSurface, marginBottom: 8 }}>
            Nombre: {person?.firstName} {person?.lastName}
          </Text>
          <Text style={{ color: theme.colors.onSurface }}>
            Celular: {person?.phone}
          </Text>
          <Text style={{ color: theme.colors.onSurface }}>
            Fecha de nacimiento:{" "}
            {person?.birthDate &&
              new Date(person.birthDate).toLocaleDateString()}
          </Text>
        </>
      ),
    },
  ];

  // Avanzar al siguiente paso
  const handleNext = async () => {
    try {
      // Paso 0: Datos personales → validamos antes de continuar
      if (step === 0) {
        if (!person?.firstName || !person?.lastName || !person?.phone || !selectedState || !selectedCity) {
          console.log("Faltan datos personales");
          return; // no avanza si falta algo
        }
      }

      // Paso 1: Credenciales → validamos antes de continuar
      if (step === 1) {
        if (!password || password.length < 6) {
          console.log("La contraseña debe tener al menos 6 caracteres");
          return;
        }
      }

      // Paso 2: Confirmación → ejecutamos la API
      if (step === 2) {
        if (!person || !selectedState?.id) return;
        const response = await addPeople(person, selectedState.id, 1);
        console.log("Usuario registrado:", response);
        return; // aquí ya no hay "next", termina
      }

      // Avanzar al siguiente paso si no es el último
      setStep((prev) => prev + 1);

    } catch (error) {
      console.error("Error en registro:", error);
      // Opcional: retroceder si quieres
      // setStep((prev) => prev - 1);
    }
  };

  // Retroceder al paso anterior
  const handleBack = () => {
    if (step > 0) {
      setStep((prev) => prev - 1);
    }
  };


  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: theme.colors.background }}>
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
        keyboardVerticalOffset={80} // ajusta según tu header/nav
      >
        <ScrollView
          contentContainerStyle={{ flexGrow: 1, paddingBottom: 20 }}
          showsVerticalScrollIndicator={false}
        >
          <AuthForm
            title={steps[step].title}
            image={require("@/assets/images/viddefe_logo.png")}
          >
            {steps[step].content}

            <Button mode="contained" onPress={handleNext} style={{ marginTop: 16 }}>
              {step === steps.length - 1 ? "Registrar" : "Siguiente"}
            </Button>

            {step > 0 && (
              <Button onPress={handleBack} style={{ marginTop: 8 }}>
                Atrás
              </Button>
            )}
          </AuthForm>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  input: {
    marginBottom: 12,
  },
});
