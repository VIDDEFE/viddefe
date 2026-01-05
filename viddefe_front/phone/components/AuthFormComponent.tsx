import React from "react";
import { Image, ImageSourcePropType, StyleSheet, View } from "react-native";
import { Surface, Text, useTheme } from "react-native-paper";

type Props = {
  title: string;
  children: React.ReactNode;
  image?: ImageSourcePropType;
};

export default function AuthForm({ title, children, image }: Props) {
  const theme = useTheme();

  return (
    <View style={styles.wrapper}>
      <Surface
        style={[styles.container, { backgroundColor: theme.colors.surface }]}
        elevation={2}
      >
        {image && (
          <Image source={image} style={styles.image} resizeMode="contain" />
        )}
        <Text
          variant="headlineMedium"
          style={[styles.title, { color: theme.colors.onSurface }]}
        >
          {title}
        </Text>
        <View style={styles.content}>{children}</View>
      </Surface>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
    justifyContent: "center",
    padding: 16,
  },
  container: {
    borderRadius: 12,
    padding: 20,
  },
  image: {
    width: "100%",
    alignSelf: "center",
    height: 250,
  },
  title: {
    textAlign: "center",
    marginBottom: 16,
  },
  content: {
    gap: 16,
  },
});
