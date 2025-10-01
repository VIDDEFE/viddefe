// components/StateCityPicker.tsx
import { Cities, States } from "@/types/StatesGeo";
import { Picker } from "@react-native-picker/picker";
import React from "react";
import { StyleSheet } from "react-native";
import { useTheme } from "react-native-paper";

type Props = {
  states: States[];
  selectedState: States | null;
  onStateChange: (state: States) => void;
  cities: Cities[];
  selectedCity: Cities | null;
  onCityChange: (city: Cities) => void;
};

export default function StateCityPicker({
  states,
  selectedState,
  onStateChange,
  cities,
  selectedCity,
  onCityChange,
}: Props) {
  const theme = useTheme();
  return (
    <>
      <Picker
        mode="dialog"
        dropdownIconRippleColor={theme.colors.primary}
        style={{...style.picker,borderColor:  theme.colors.outline}}
        selectedValue={selectedState?.id}
        onValueChange={(value) => {
          const state = states.find((s) => s.id === Number(value));
          if (state) onStateChange(state);
        }}
      >
        <Picker.Item label="Seleccione un estado" value={null} />
        {states?.map((s) => (
          <Picker.Item key={s.id.toString()} label={s.name} value={s.id.toString()} />
        ))}
      </Picker>

      {selectedState && (
        <Picker
          selectedValue={selectedCity?.id}
          onValueChange={(value) => {
            const city = cities?.find((c) => c.id === value);
            if (city) onCityChange(city);
          }}
        >
          <Picker.Item label="Seleccione una ciudad" value={null} />
          {cities?.map((c) => (
            <Picker.Item key={c.id.toString()} label={c.name} value={c.id.toString()} />
          ))}
        </Picker>
      )}
    </>
  );
}

const style = StyleSheet.create({
  picker: {
    borderWidth: 1,
    borderRadius: 4,
  }
})