import { useState, useEffect } from 'react';
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
    const[isLoading, setIsLoading] =useState(false); // Added: loading stated.

    useEffect(() => {
        let intervalId: ReturnType <typeof setInterval>;

        const checkBackend = async () => {
            try {
                const response = await fetch('/health', {
                    method: 'GET',
                    headers: {'Accept': 'application/json'}
                });

                if(response.ok) {
                    console.log('Backend is ready.');
                    setError('') // Clear the "starting up" message.
                    clearInterval(intervalId); // stop the polling.
                }
            } catch (error) {
                console.warn('Backend is not ready yet, retrying...');
                setError('Service is temporarily unavailable. Please wait a moment.');
            }
        };

        // Check immediately, then every 3 seconds.
        checkBackend();
        intervalId = setInterval(checkBackend, 3000);

        // Cleanup when the component unmounts.
        return () => clearInterval(intervalId);
    }, []);

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

        setIsLoading(true);

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
              // 1. Log the exact error to the console so we can debug it.
            console.error('login failed with error:', error);

            let errorMessage = 'Login failed. Please try again.';

            // Added: specific error messages.
            if (error.message) {
                if (error.message.includes('400')) {
                errorMessage = 'Invalid email or password format. Please check your input.'
                } else if (error.message.includes('401')) {
                    errorMessage = 'Invalid credentials. Please check your email and password.'
                } else if (error.message.includes('403')) {
                    // This will catch the "HTTP 403" and show a message instead.
                    errorMessage = "Invalid credentials. Please check your email and password."
                } else if (error.message.includes('Failed to fetch') || 
                           error.message.includes('502') ||
                           error.message.includes('503')) {
                  errorMessage = 'The server is starting up. Please wait a few seconds and try again.';
                } else {
                    errorMessage = error.message;
                }
            }

            setError(errorMessage);
        }     finally {
                setIsLoading(false);
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
                    pattern="[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,}$"
                    title="Please enter a valid email address (e.g., user@example.com)"
                    disabled={isLoading} // Added: Disable during request.
                    />
                </div>
                <div>
                    <label>Password</label>
                    <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    disabled={isLoading} // Added: Disable during request.
                    />
                </div>

                {/** The conditional rendering: only show the error paragraph if error is not
                 * empty.
                 */}
                {error && <p className="error">{error}</p>}
                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Logging in...' : 'Login'}
                    </button>
            </form>
            <p>
                no account? <a href="/register">Register</a>
            </p>

            {/** Added: forgot password link placeholder for now. */}
            <p>
                <a href="/forgot-password" style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                    Forgot password?
                </a>
            </p>
        </div>
    )
}