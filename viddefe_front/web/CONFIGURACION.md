# ⚙️ Configuración del Proyecto VIDDEFE

## Ambiente Variables

Crea un archivo `.env` en la raíz del proyecto:

```env
# API Configuration
VITE_API_URL=http://localhost:3001/api

# App Configuration
VITE_APP_NAME=VIDDEFE
VITE_APP_VERSION=1.0.0

# Feature Flags
VITE_ENABLE_ANALYTICS=false
VITE_ENABLE_NOTIFICATIONS=true
```

### Variables de Desarrollo (.env.development)
```env
VITE_API_URL=http://localhost:3001/api
VITE_APP_ENV=development
VITE_DEBUG=true
```

### Variables de Producción (.env.production)
```env
VITE_API_URL=https://api.viddefe.com
VITE_APP_ENV=production
VITE_DEBUG=false
```

## Configuración de TypeScript

### tsconfig.json
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noImplicitThis": true,
    "alwaysStrict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "forceConsistentCasingInFileNames": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

## Configuración de Vite

### vite.config.ts (Configuración Recomendada)
```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:3001',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    minify: 'terser',
  },
})
```

## Configuración de ESLint

### .eslintrc.json (Recomendado)
```json
{
  "env": {
    "browser": true,
    "es2021": true,
    "node": true
  },
  "extends": [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "plugin:@typescript-eslint/recommended"
  ],
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "ecmaFeatures": {
      "jsx": true
    },
    "ecmaVersion": "latest",
    "sourceType": "module"
  },
  "plugins": [
    "react",
    "react-hooks",
    "@typescript-eslint"
  ],
  "rules": {
    "react/react-in-jsx-scope": "off",
    "@typescript-eslint/no-unused-vars": [
      "error",
      {
        "argsIgnorePattern": "^_"
      }
    ]
  },
  "settings": {
    "react": {
      "version": "detect"
    }
  }
}
```

## Configuración de Prettier

### .prettierrc (Recomendado)
```json
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "es5",
  "printWidth": 80,
  "arrowParens": "always",
  "endOfLine": "lf"
}
```

### .prettierignore
```
node_modules
dist
build
.git
.env*
*.md
```

## Scripts de Package.json

### package.json (Configuración recomendada)
```json
{
  "name": "viddefe-web",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives",
    "lint:fix": "eslint . --ext ts,tsx --fix",
    "preview": "vite preview",
    "type-check": "tsc --noEmit",
    "format": "prettier --write \"src/**/*.{ts,tsx,json,css,md}\"",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "coverage": "vitest --coverage"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.16.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "@typescript-eslint/eslint-plugin": "^6.0.0",
    "@typescript-eslint/parser": "^6.0.0",
    "@vitejs/plugin-react": "^4.0.0",
    "eslint": "^8.0.0",
    "eslint-plugin-react-hooks": "^4.6.0",
    "prettier": "^3.0.0",
    "typescript": "^5.0.0",
    "vite": "^4.4.0"
  }
}
```

## Estructura de Carpetas Recomendada

```
viddefe_front/
├── web/
│   ├── src/
│   │   ├── components/
│   │   ├── views/
│   │   ├── models/
│   │   ├── services/
│   │   ├── utils/
│   │   ├── hooks/
│   │   ├── context/
│   │   ├── constants/
│   │   ├── router/
│   │   ├── App.tsx
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.tsx
│   ├── public/
│   ├── .env
│   ├── .env.development
│   ├── .env.production
│   ├── .eslintrc.json
│   ├── .prettierrc
│   ├── .gitignore
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   ├── tsconfig.node.json
│   ├── package.json
│   └── index.html
├── backend/ (próximo)
└── docs/
    ├── ESTRUCTURA_PROYECTO.md
    ├── GUIA_DESARROLLO.md
    ├── EJEMPLOS_PRACTICOS.md
    └── CONFIGURACION.md
```

## Control de Versiones

### .gitignore (Recomendado)
```
# Dependencies
node_modules/
.pnp
.pnp.js

# Testing
coverage/
.nyc_output/

# Production
dist/
build/

# Misc
.DS_Store
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Logs
npm-debug.log*
yarn-debug.log*
yarn-error.log*
lerna-debug.log*

# IDE
.vscode/
.idea/
*.swp
*.swo
*~

# OS
Thumbs.db
```

## Docker Configuration (Opcional)

### Dockerfile
```dockerfile
FROM node:18-alpine as build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM node:18-alpine

WORKDIR /app

RUN npm install -g http-server

COPY --from=build /app/dist ./dist

EXPOSE 3000

CMD ["http-server", "dist", "-p", "3000"]
```

### docker-compose.yml
```yaml
version: '3.8'

services:
  viddefe-web:
    build: .
    ports:
      - "3000:3000"
    environment:
      - VITE_API_URL=http://localhost:3001/api
```

## CI/CD Configuration

### .github/workflows/deploy.yml (Opcional)
```yaml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-node@v2
        with:
          node-version: '18'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Lint
        run: npm run lint
      
      - name: Build
        run: npm run build
      
      - name: Deploy
        run: |
          # Tu comando de deployment aquí
          npm run preview
```

## Performance Tips

### Optimización de Imágenes
```tsx
// Usar lazy loading para imágenes
<img loading="lazy" src="image.jpg" alt="description" />
```

### Code Splitting
```tsx
// Usar React.lazy para code splitting
const Dashboard = React.lazy(() => import('./views/dashboard/Dashboard'));

<Suspense fallback={<LoadingSpinner />}>
  <Dashboard />
</Suspense>
```

### Memoización
```tsx
// Usar memo para componentes costosos
const MyComponent = React.memo(({ data }) => {
  return <div>{data}</div>;
});
```

## Security Configuration

### Cabeceras de Seguridad (Backend/Server)
```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
```

## Monitoring & Logging

### Sentry Configuration (Opcional)
```typescript
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: process.env.VITE_SENTRY_DSN,
  environment: process.env.VITE_APP_ENV,
  tracesSampleRate: 1.0,
});
```

---

## Checklist de Configuración

- [ ] Crear archivo .env
- [ ] Configurar variables de entorno
- [ ] Instalar dependencias
- [ ] Ejecutar en desarrollo
- [ ] Verificar rutas
- [ ] Verificar servicios API
- [ ] Configurar ESLint
- [ ] Configurar Prettier
- [ ] Crear archivo .gitignore
- [ ] Configurar CI/CD (opcional)
- [ ] Configurar monitoring (opcional)

---

Para más información, consulta la [Guía de Desarrollo](GUIA_DESARROLLO.md).
