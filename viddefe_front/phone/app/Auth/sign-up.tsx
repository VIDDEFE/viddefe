// screens/Auth/SignUpScreen.tsx
import { signUp } from "@/api/Auth/Sign";
import { addPeople } from "@/api/People";
import AuthForm from "@/components/AuthFormComponent";
import PersonalDataForm from "@/components/PersonalDataForm";
import SuccessScreen from "@/components/ui/SuccessScreen";
import { useRedirectTo } from "@/hooks/redirec-to";
import useGeoData from "@/hooks/StatesGeo/StatesGeo";
import { Person } from "@/types/People";
import { User } from "@/types/User";
import React, { useEffect } from "react";
import { KeyboardAvoidingView, Platform, ScrollView, StyleSheet } from "react-native";
import { Button, TextInput, useTheme } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";

export default function SignUpScreen() {
  const theme = useTheme();
  const [step, setStep] = React.useState(0);

  const [person, setPerson] = React.useState<Person | null>(null);
  const [showDatePicker, setShowDatePicker] = React.useState(false);

  const [user, setUser ] = React.useState<User|null>();
  const redirectTo = useRedirectTo();

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
        <>
        <TextInput
          label="Correo Electrónico"
          value={user?.email}
          onChangeText={(text)=> setUser((prev) => prev ? { ...prev, email: text } : { peopleId: "", password: "", email: text, rolUserId: 2 })}
          mode="outlined"
          outlineColor={theme.colors.outline}
          activeOutlineColor={theme.colors.primary}
          style={styles.input}
          />
        <TextInput
          label="Contraseña"
          value={user?.password}
          onChangeText={(text)=> setUser((prev) => prev ? { ...prev, password: text } : { peopleId: "", password: text, email: "", rolUserId: 2 })}
          passwordRules={"minlength: 8; maxlength: 16; required: lower; required: upper; required: digit;"}
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
        <SuccessScreen message="Registro Completado!" />
      ),
    },
  ];

  // Avanzar al siguiente paso
  const handleNext = async () => {
    try {
      setStep((prev) => prev + 1)

      // Paso 0: Datos personales → validamos antes de continuar
      if (!person?.firstName || !person?.lastName || !person?.phone || !selectedState || !selectedCity) {
        console.log("Faltan datos personales");
        return; // no avanza si falta algo
      }
      
      console.log("aqui toy   2 ", step)
      if (step === 0) {
        if (!person || !selectedState?.id) return;
        const response = await addPeople(person, selectedState.id, 1);
        if(!response || !response.id){ 
          console.log("response in if: ",response)
          throw new Error(`La persona ${person.firstName} ${person.lastName} no pudo ser añadida`)
        }
        
        setUser((prev) => prev && response?.id ? { ...prev, peopleId: response.id as string } : null)
        return; // aquí ya no hay "next", termina
      }
      console.log("aqui toy   3 ", step)

      if(step === 1){
        console.log("user antes de signup: ", user)
        if(!user || !user.password || !user.password) throw new Error(`Datos insuficientes para crear el usuario`)
        await signUp(user); 
        setStep((prev) => prev + 1)

    }
    
      // Avanzar al siguiente paso si no es el último

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

  useEffect(() => {
    if (step === 2) {
      const cancel = redirectTo("Auth/sign-in", 2500);
      return cancel;
    }
  }, [step]);

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

              {step !== steps.length - 1 && (
              <Button mode="contained" onPress={handleNext} style={{ marginTop: 16 }}>
                Siguiente
              </Button>
              )}

            {step > 0 && step !== steps.length - 1 && (
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
