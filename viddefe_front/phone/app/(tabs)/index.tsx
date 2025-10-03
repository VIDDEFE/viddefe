import React from "react";
import { StyleSheet } from "react-native";
import { Text, useTheme } from "react-native-paper";
import { SafeAreaView } from "react-native-safe-area-context";

export default function HomeScreen() {
  const theme = useTheme();

  return (
    <SafeAreaView style={styles.container} edges={["top", "left", "right"]}>
      <Text variant="titleLarge" style={{ color: theme.colors.primary }}>
        WELCOME
      </Text>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",     // centra horizontal
    padding: 16,
  },
});
