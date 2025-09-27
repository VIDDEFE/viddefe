import { Colors } from "@/constants/theme";
import * as React from "react";
import { Image, StyleSheet, View } from "react-native";
import { Text } from "react-native-paper";

type Props = {
  title: string;
  children: React.ReactNode;
};

export default function AuthLayout({ title, children }: Props) {
  return (
    <View style={styles.container}>
      <View style={styles.card}>
        <View style={styles.header}>
          <Image
            style={styles.logo}
            source={require("../../assets/images/viddefe-logo.png")}
          />
          <Text variant="headlineLarge" style={styles.title}>
            {title}
          </Text>
        </View>

        {children}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { 
    flex: 1, 
    justifyContent: "center", 
    padding: 20,
   },
  card: {
    backgroundColor: "#fff",
    padding: 20,
    borderRadius: 15,
    elevation: 20,
  },
  header: { alignItems: "center" },
  logo: {
    width: 300,
    height: 200,
    marginBottom: 10,
  },
  title: { 
    textAlign: "center",
    marginBottom: 10,
    color: Colors.light.primary,
   },
});
