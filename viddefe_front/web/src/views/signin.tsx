import AuthForm from "../components/auth/AuthForm";

export default function SignIn() {
    return (
        <AuthForm
            title="Iniciar Sesión"
            onSubmit={(e) => {return}}
            submitLabel="Entrar"
            footer={<div>¿No tienes una cuenta? <a href="/signup">Regístrate</a></div>}
        >
            <p>hola causa</p>
        </AuthForm>
    )
}