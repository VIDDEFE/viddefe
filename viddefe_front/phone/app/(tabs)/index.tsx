import * as React from 'react';
import {
  Image,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  View,
  useWindowDimensions,
} from 'react-native';
import {
  Button,
  DefaultTheme,
  Provider as PaperProvider,
  Text,
  TextInput,
  configureFonts,
} from 'react-native-paper';

export default function App() {
  const { width } = useWindowDimensions();
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [message, setMessage] = React.useState('');

  const handleLogin = () => {
    if (email === 'demo@demo.com' && password === '1234') {
      setMessage('✅ Login correcto');
    } else {
      setMessage('❌ Usuario o contraseña inválidos');
    }
  };

  const isLargeScreen = width >= 768;

  const theme = {
    ...DefaultTheme,
    colors: {
      ...DefaultTheme.colors,
      primary: '#3b82f6', // azul agradable
      background: '#f0f2f5',
    },
    fonts: configureFonts({}),
  };

  return (
    <PaperProvider theme={theme}>
      <KeyboardAvoidingView
        style={styles.root}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <ScrollView
          contentContainerStyle={[
            styles.scrollContainer,
            isLargeScreen && styles.scrollContainerLarge,
          ]}
          keyboardShouldPersistTaps="handled"
        >
          <View
            style={[
              styles.card,
              isLargeScreen && styles.cardLarge,
            ]}
          >
            <View style={styles.header}>
              <Image
                style={styles.logo}
                source={require('../../assets/images/viddefe-logo.png')}
              />
              <Text style={styles.title}>Iniciar Seión</Text>
            </View>

            <TextInput
              label="Email"
              value={email}
              onChangeText={setEmail}
              style={styles.input}
              autoCapitalize="none"
              keyboardType="email-address"
              mode="outlined"
            />

            <TextInput
              label="Contraseña"
              value={password}
              onChangeText={setPassword}
              style={styles.input}
              secureTextEntry
              mode="outlined"
            />

            <Button
              mode="contained"
              onPress={handleLogin}
              style={styles.button}
            >
              Ingresar
            </Button>

            {message ? (
              <Text style={styles.message}>{message}</Text>
            ) : null}
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </PaperProvider>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: '#f0f2f5',
  },
  scrollContainer: {
    flexGrow: 1,
    justifyContent: 'center',
    paddingHorizontal: 20,
  },
  scrollContainerLarge: {
    paddingVertical: 40,
  },
  card: {
    backgroundColor: 'white',
    borderRadius: 16,
    padding: 24,
    // sombra universal (RN + Web)
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 4,
  },
  cardLarge: {
    maxWidth: 420,
    alignSelf: 'center',
  },
  header: {
    flexDirection: 'column',
    alignItems: 'center',   // centra horizontalmente
    justifyContent: 'center', // centra verticalmente dentro del contenedor
    marginBottom: 28,
  },
  title: {
    fontSize: 26,
    fontWeight: '600',
    textAlign: 'center',
    color: '#111',
  },
  input: {
    marginBottom: 16,
  },
  button: {
    marginTop: 8,
    paddingVertical: 6,
  },
  message: {
    marginTop: 20,
    textAlign: 'center',
    fontSize: 16,
  },
  logo: {
    width: 350,
    height: 200,
    marginBottom: 12,
  },
});
