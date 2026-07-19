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
    const [isLoading, setIsLoading] = useState(false); // Added: Loading state.

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

        setIsLoading(true);

        try {
            // This will send POST /auth/register with name, email, and password.
            // The backend returns { token: "dcidGe..."} on success.
            // The backend will now auto-create a CHECKING account for this user.
            const data = await apiFetch('/auth/register', {
                method: 'POST',
                body: JSON.stringify({name, email, password }),
            });

            // This will store the JWT token for authenticated future requests.
            setToken(data.token);

            // This will redirect to the dashboard.
            navigate('/dashboard');
        } catch (error: any) {
            // Added: Specific error messages for registration.
            if (error.message.includes('409') || error.message.toLowerCase().includes('already')) {
                setError('This email is already registered. Please try logging in.');
            } else {
                // The display server errors (e.g., duplicate email).
                setError(error.message || 'Registration failed.');
            }
        } finally {
            // Clear the loading state regardless of success or failure.
            setIsLoading(false);
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
                        disabled={isLoading}
                    />
                </div>
                <div>
                    <label>Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                        title="Please enter a valid email address (e.g., user@example.com)"
                        disabled={isLoading}
                    />
                </div>
                 <div>
                    <label>Password</label>
                    <input
                         type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        minLength={8}
                        title="The password must be at least 8 characters."
                        disabled={isLoading}
                    />
                </div>

                {/** This will only show an error if it exists. */}
                {error && <p className="error">{error}</p>}

                <button type="submit" disabled={isLoading}>
                {isLoading ? 'Creating Account...' : 'Register'}    
                </button>
            </form>

            <p>
                Already have an account? <a href="/login">Login</a>
            </p>
        </div>
    );
}