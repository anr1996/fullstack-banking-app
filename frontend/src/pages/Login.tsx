import { useState, useEffect, useRef } from 'react';
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
    const[isLoading, setIsLoading] =useState(false); // Added: loading state.

    // Form-level errors: handles bad credentials, malformed input.
    const [formError, setFormError] = useState('');

    // Service availability. It is owned exclusively by the health check.
    const [serviceDown, setServiceDown] = useState(false);

    // The interval ID lives in a ref so it survives re-renders.
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);


    /**
     * This polls the health endpoint until the service responds, then stops.
     * It never writes to form the error state.
     */
    useEffect(() => {
        const stopPolling = () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
                intervalRef.current = null;
            }
        };

        const checkService = async () => {
            try {
                const response = await fetch('/health', {
                    method: 'GET',
                    headers: {Accept: 'application/json'},
                });

                if (response.ok) {
                    setServiceDown(false);
                    stopPolling();
                }
            } catch {
                setServiceDown(true);
            }
        }

        checkService();
        intervalRef.current = setInterval(checkService, 3000);

        return stopPolling;
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
        setFormError('');
        setIsLoading(true);

        try {
            // The apiFetch sends the POST request to /auth/login with the email and password.
            // The backend returns { token: "sdfh..." } upon success.
            const data = await apiFetch('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ email, password }),
            });

            // This will throw if the token is missing, so we never navigate on a bad login.
            setToken(data?.token);
            navigate('/dashboard', { replace: true });
        } catch (err: any) {
            console.error('login failed:', err);
            const message = err?.message ?? '';

            if (message.includes('401') || message.includes('403')) {
                setFormError('Invalid email or password.');
            } else if (message.includes('400')) {
                setFormError('Please check your email and password format.');
            } else if (
                message.includes('Failed to fetch') || 
                message.includes('502') || 
                message.includes('503')
            ) {
                setFormError("We could not reach the service. Please try again in a moment.")
            } else {
                setFormError(message || 'Login failed. Please try again.');
            }
        } finally {
            setIsLoading(false);
        }
    }          
    

    return (
        <div className="login-container">
            <h1>Bank Login</h1>

            {serviceDown && (
                <p className="warning">
                    The service is temporarily unavailable. Please wait a moment.
                </p>
            )}

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

               {formError && <p className="error">{formError}</p>}

                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Logging in...' : 'Login'}
                    </button>
            </form>
            <p>
                No account? <a href="/register">Register</a>
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