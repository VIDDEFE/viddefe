// screens/Auth/SignUpScreen.tsx
import AuthForm from "@/components/AuthFormComponent";
import PersonalDataForm from "@/components/PersonalDataForm";
import useGeoData from "@/hooks/StatesGeo/StatesGeo";
import { Person } from "@/types/People";
import React from "react";
import { Platform, StyleSheet } from "react-native";
import { Button, Text, TextInput, useTheme } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";

export default function SignUpScreen() {
  const theme = useTheme();
  const [step, setStep] = React.useState(0);

  const [person, setPerson] = React.useState<Person | null>(null);
  const [password, setPassword] = React.useState("");
  const [showDatePicker, setShowDatePicker] = React.useState(false);

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

  const handleNext = () => {
    if (step < steps.length - 1) setStep(step + 1);
    else {
      console.log("Registrar usuario", person, password);
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

        <Button mode="contained" onPress={handleNext} style={{ marginTop: 16 }}>
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
