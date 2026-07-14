/**
 * The Base API client for the banking backend.
 * All HTTP requests go through this module.
 */

const API_BASE = '';

/**
 * This will perform an authenticated fetch request.
 * It attaches the JWT token from localStorage if present.
 * 
 * @param url (The endpoint path (e.g., '/accounts').)
 * @param options (This will fetch options (method, body, headers).)
 * @returns (Returns the parsed JSON response.)
 */
export async function apiFetch(url: string, options: RequestInit = {}) {
    const token = localStorage.getItem('token');

    const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        ...((options.headers as Record<string, string>) || {}),
    };

    if(token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE}${url}`, {
        ...options,
        headers,
    })

    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || `HTTP ${response.status}`);
    }

    // This handles empty responses (e.g., 403 with no body).
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

/**
 * This will store the JWT token in localStorage.
 * 
 * @param token (the JWT string.)
 */
export function setToken(token: string) {
    localStorage.setItem('token', token);
}

/**
 * This will remove the JWT token from localStorage.
 */
export function clearToken() {
    localStorage.removeItem('token');
}

/**
 * This will check if a user is currently logged in.
 * 
 * @return (Returns true if a token exists.)
 */
export function isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
}