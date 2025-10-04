// components/StateCityPicker.tsx  
import { Cities, States } from "@/types/StatesGeo";
import { Picker } from "@react-native-picker/picker";
import React from "react";
import { StyleSheet, View } from "react-native";
import { useTheme } from "react-native-paper";

type Props = {
  states: States[];
  selectedState: States | null;
  onStateChange: (state: States) => void;
  cities: Cities[];
  selectedCity: Cities | null;
  onCityChange: (city: Cities) => void;
  theme: ReturnType<typeof useTheme>;
};

export default function StateCityPicker({
  states,
  selectedState,
  onStateChange,
  cities,
  selectedCity,
  onCityChange,
  theme
}: Props) {
  return (
    <>
      <View
        style={[
          style.wrapper,
          {
            borderColor: theme.colors.outline,
            backgroundColor: theme.colors.background,
          },
        ]}
      >
        <Picker
          mode="dropdown"
          style={{ color: theme.colors.onSurface }}
          dropdownIconRippleColor={theme.colors.primary}
          dropdownIconColor={theme.colors.secondary}
          selectedValue={selectedState?.id?.toString() ?? null}
          onValueChange={(value) => {
            const state = states.find((s) => s.id === Number(value));
            if (state) onStateChange(state);
          }}
        >
          <Picker.Item
            label="Seleccione un estado"
            value={null}
            style={{ color: theme.colors.secondary }}
          />
          {states?.map((s) => (
            <Picker.Item
              key={s.id.toString()}
              label={s.name}
              value={s.id.toString()}
              style={{ color: theme.colors.secondary }}
            />
          ))}
        </Picker>
      </View>

      {/* Picker de Ciudades */}
      {selectedState && (
        <View
          style={[
            style.wrapper,
            {
              borderColor: theme.colors.outline,
              backgroundColor: theme.colors.background,
            },
          ]}
        >
          <Picker
            mode="dropdown"
            dropdownIconRippleColor={theme.colors.primary}
            style={{ color: theme.colors.onSurface }}
            dropdownIconColor={theme.colors.secondary}
            selectedValue={selectedCity?.id?.toString() ?? null}
            onValueChange={(value) => {
              const city = cities?.find((c) => c.id.toString() === value);
              if (city) onCityChange(city);
            }}
          >
            <Picker.Item
              label="Seleccione una ciudad"
              value={null}
              style={{ color: theme.colors.secondary }}
            />
            {cities?.map((c) => (
              <Picker.Item
                key={c.id.toString()}
                label={c.name}
                value={c.id.toString()}
                style={{ color: theme.colors.secondary }}
              />
            ))}
          </Picker>
        </View>
      )}
    </>
  );
}

const style = StyleSheet.create({
  wrapper: {
    borderWidth: 1,
    borderRadius: 5,
    marginBottom: 12,
    height: 50
  },
});
