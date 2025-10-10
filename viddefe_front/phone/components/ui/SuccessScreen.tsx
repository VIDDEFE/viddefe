import LottieView from "lottie-react-native";
import React from "react";
import { StyleSheet, Text } from "react-native";
import Animated, { FadeIn, FadeOut } from "react-native-reanimated";

export default function SuccessScreen({ message }: { message?: string }) {
  return (
    <Animated.View
      entering={FadeIn.duration(400)}
      exiting={FadeOut.duration(300)}
      style={styles.container}
    >
      <LottieView
        source={require("@/assets/animations/success.json")}
        autoPlay
        loop={false}
        style={styles.animation}
      />
      <Text style={styles.text}>{message}</Text>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  animation: {
    width: 150,
    height: 150,
  },
  text: {
    fontSize: 20,
    color: "#2ecc71",
    fontWeight: "600",
    marginTop: 20,
  },
});
