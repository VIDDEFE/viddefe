import { StackActions, useNavigation } from "@react-navigation/native";

export const useRedirectTo = () => {
  const navigation = useNavigation();

  return (path: string, delay = 2500) => {
    const timeout = setTimeout(() => {
      navigation.dispatch(StackActions.replace(path));
    }, delay);

    return () => clearTimeout(timeout); // función de cancelación opcional
  };
};
