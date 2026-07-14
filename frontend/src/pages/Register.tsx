import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiFetch, setToken } from '../api/client';

/**
 * The register page component
 * This will create a new user account and store the JWT token.
 */
export default function Register() {
    // useNavigate lets us redirect to the dashboard after successful registration.
    const navigate = useNavigate();

    // useState holds the form field values. React re-renders on every change.
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    /**
     * This will handle form submission
     * It sends registration data to the backend and stores the returned token.
     * 
     * @param e (The form submit event.)
     */
    async function handleSubmit(e: React.FormEvent) {
        // This prevents the browser from reloading the page.
        e.preventDefault();

        // This will clear any previous error.
        setError('');

        try {
            // This will send POST /auth/register with name, email, and password.
            // The backend returns { token: "dcidGe..."} on success.
            const data = await apiFetch('/auth/register', {
                method: 'POST',
                body: JSON.stringify({name, email, password }),
            });

            // This will store the JWT token for authenticated future requests.
            setToken(data.token);

            // This will redirect to the dashboard.
            navigate('/dashboard');
        } catch (error: any) {
            // The display server errors (e.g., duplicate email).
            setError(error.message || 'Registration failed');
        }
    } 

    return (
        <div className="register-container">
            <h1>Create Account</h1>

            {/** onSubmit will intercept the browser's default form submission. */}
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Name</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Email</label>
                    <input
                         type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                 <div>
                    <label>Password</label>
                    <input
                         type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                {/** This will only show an error if it exists. */}
                {error && <p className="error">{error}</p>}

                <button type="submit">Register</button>
            </form>

            <p>
                Already have an account? <a href="/login">Login</a>
            </p>
        </div>
    );
}