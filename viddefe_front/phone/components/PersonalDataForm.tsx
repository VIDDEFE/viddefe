import { Person } from "@/types/People";
import { Cities, States } from "@/types/StatesGeo";
import DateTimePicker from "@react-native-community/datetimepicker";
import { StyleSheet } from "react-native";
import { TextInput, useTheme } from "react-native-paper";
import StateCityPicker from "./StatesCitiesPicker";

type PersonalDataFormProps = {
  person: Person | null;
  setPerson: React.Dispatch<React.SetStateAction<Person | null>>;
  showDatePicker: boolean;
  setShowDatePicker: React.Dispatch<React.SetStateAction<boolean>>;
  handleDateChange: (_event: any, selectedDate?: Date) => void;
  theme: ReturnType<typeof useTheme>;
  states: States[];
  selectedState: States | null;
  setSelectedState: React.Dispatch<React.SetStateAction<States | null>>;
  cites: Cities[];
  selectedCity: Cities | null;
  setSelectedCity: React.Dispatch<React.SetStateAction<Cities | null>>;
};

export default function PersonalDataForm({
  person,
  setPerson,
  showDatePicker,
  setShowDatePicker,
  handleDateChange,
  theme,
  states,
  setSelectedState,
  cites,
  setSelectedCity,
  selectedState,
  selectedCity,
}: PersonalDataFormProps) {
  return (
    <>
      <TextInput
        label="Número de identificación"
        value={person?.cc.toString() || ""}
        onChangeText={(cc) =>
          setPerson((prev) => ({ ...prev, cc: Number(cc) } as Person))
        }
        mode="outlined"
        outlineColor={theme.colors.outline}
        activeOutlineColor={theme.colors.primary}
        style={styles.input}
        keyboardType="numeric"
      />

      <TextInput
        label="Primer Nombre"
        value={person?.firstName || ""}
        onChangeText={(text) =>
          setPerson((prev) => ({ ...prev, firstName: text } as Person))
        }
        mode="outlined"
        outlineColor={theme.colors.outline}
        activeOutlineColor={theme.colors.primary}
        style={styles.input}
      />

      <TextInput
        label="Primer Apellido"
        value={person?.lastName || ""}
        onChangeText={(text) =>
          setPerson((prev) => ({ ...prev, lastName: text } as Person))
        }
        mode="outlined"
        outlineColor={theme.colors.outline}
        activeOutlineColor={theme.colors.primary}
        style={styles.input}
      />

      <TextInput
        label="Número celular"
        value={person?.phone || ""}
        onChangeText={(text) =>
          setPerson((prev) => ({ ...prev, phone: text } as Person))
        }
        mode="outlined"
        outlineColor={theme.colors.outline}
        activeOutlineColor={theme.colors.primary}
        style={styles.input}
        keyboardType="phone-pad"
      />

      <TextInput
        label="Fecha de Nacimiento"
        value={
          person?.birthDate
            ? new Date(person.birthDate).toLocaleDateString()
            : ""
        }
        mode="outlined"
        outlineColor={theme.colors.outline}
        activeOutlineColor={theme.colors.primary}
        style={styles.input}
        right={
          <TextInput.Icon
            icon="calendar"
            onPressIn={() => setShowDatePicker(true)}
          />
        }
        onPressIn={() => setShowDatePicker(true)}
        editable={false} // evita teclear manualmente, solo selecciona con el calendario
      />

      {showDatePicker && (
        <DateTimePicker
          value={person?.birthDate ? new Date(person.birthDate) : new Date()}
          mode="date"
          display="calendar"
          onChange={handleDateChange}
        />
      )}

      {states && cites && (
        <StateCityPicker
          states={states}
          selectedState={selectedState}
          onStateChange={(state) => {
            setSelectedState(state);
          }}
          cities={cites}
          selectedCity={selectedCity}
          onCityChange={(city) => {
            setSelectedCity(city);
          }}
          theme={theme}
        />
      )}
    </>
  );
}

const styles = StyleSheet.create({
  input: {
    marginBottom: 12,
  },
});
