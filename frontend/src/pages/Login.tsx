import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiFetch, setToken } from '../api/client';

/**
 * The login page component
 * This will authenticate the user and store the JWT token.
 */
export default function Login() {
    /**
     * useNavigate is a React Router hook that lets us redirect programmatically.
     * We will use it after a successful login to send the user to the dashboard.
     */
    const navigate = useNavigate();

    /**
     * useState creates reactive variables.
     * When setEmail/setPassword is called, React re-renders the component to show the new
     * values in the inputs.
     */
    const[email, setEmail] = useState('');
    const[password, setPassword] = useState('');
    const[error, setError] = useState('');

    /**
     * This will handle form submission
     * It prevents the browser's default page reload, then sends the credentials to the backend
     * via our API client
     * 
     * @param e (The form submit event.)
     */
    async function handleSubmit(e: React.FormEvent) {
        // This will stop the browser from reloading the page on form submit.
        e.preventDefault();

        // This clears any previous error message before attempting a new login.
        setError('');

        try {
            // The apiFetch sends the POST request to /auth/login with the email and password.
            // The backend returns { token: "sdfh..." } upon success.
            const data = await apiFetch('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email, password }),
            });

            // This will store the JWT token in localStorage so future requests are authenticated.
            setToken(data.token);

            // This will redirect to the dashboard page.
            // This is the client-side navigation (no page reload).
            navigate('/dashboard');
        } catch (error: any) {
            // If the server returns 401 or any error, display it to the user.
            setError(error.message || 'Login failed.');
        }
    }

    return (
        <div className="login-container">
            <h1>Bank Login</h1>

            {/** The onSubmit handler intercepts the browser's default form submission. */}
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Email</label>
                    {/** The input is "controlled".
                     * Its value comes from React state, and onChange updates that state on every
                     * keystroke.
                     */}
                    <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                    title="Please enter a valid email address (e.g., user@example.com)"
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

                {/** The conditional rendering: only show the error paragraph if error is not
                 * empty.
                 */}
                {error && <p className="error">{error}</p>}
                <button type="submit">Login</button>
            </form>
            <p>
                no account? <a href="/register">Register</a>
            </p>
        </div>
    )
}