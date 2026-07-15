import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiFetch, clearToken, isLoggedIn } from '../api/client';

/**
 * The account type definition.
 * This matches the JSON structure returned by the backend.
 */
interface Account {
    id: number;
    accountNumber: string;
    type: string;
    balance: number;
    status: string;
}

/**
 * The transaction type definition.
 * This matches the JSON structure returned by the backend.
 */
interface Transaction {
    id: number;
    amount: number;
    type: string;
    description: string;
    createdAt: string;
}

/**
 * The dashboard page component.
 * This will display the user's accounts, balances, and transaction history.
 * It is protected and redirects login if it is not authenticated.
 */
export default function Dashboard() {
    const navigate = useNavigate();

    // The state for data fetched from the API.
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [selectedAccount, setSelectedAccount] = useState<number | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    /**
     * The useEffect runs after the component mounts.
     * It will check authentication and fetch the account data.
     */
    useEffect(() => {
        // If no token exists, it redirects to the login immediately.
        if(!isLoggedIn()) {
            navigate('/login');
            return;
        }

        // This will fetch accounts from the backend.
        apiFetch('/accounts')
                .then((data) => {
                    setAccounts(data);
                    setLoading(false);
                })
                .catch((error) => {
                    setError(error.message);
                    setLoading(false);
                })
    }, [navigate]);

    /**
     * This will fetch the transaction history for the selected account.
     * 
     * @param accountId (The account ID to query.)
     */
    function loadTransactions(accountId: number) {
        setSelectedAccount(accountId);
        setTransactions([]);

        apiFetch(`/accounts/${accountId}/transactions`)
            .then((data) => setTransactions(data))
            .catch((error) => setError(error.message));
    }

    /**
     * This will log the user out by clearing the token and redirecting to login.
     */
    function handleLogout() {
        clearToken();
        navigate('/login');
    }

    if (loading) return <p>Loading...</p>;
    if (error) return <p className="error">{error}</p>;

    return (
        <div className="dashboard-container">
            <header>
                <h1>Bank DashBoard</h1>
                <button onClick={handleLogout}>Logout</button>
            </header>

            <section>
                <h2>Your Accounts</h2>
                {accounts.length === 0 ? (
                    <p>No accounts found.</p>
                ) : (
                    <ul>
                        {accounts.map((account) => (
                            <li
                            key={account.id}
                            onClick={() => loadTransactions(account.id)}
                            className={selectedAccount === account.id ? 'selected': ''}
                            >
                                <strong>{account.type}</strong> - {account.accountNumber}
                                <br />
                                Balance: ${(account.balance / 100).toFixed(2)}
                            </li>
                        ))}
                    </ul>
                )}
            </section>

            {selectedAccount && (
                <section>
                    <h2>Transaction History</h2>
                    {transactions.length === 0 ? (
                        <p>No transactions for this account.</p>
                    ) : (
                        <table>
                            <thead>
                                <tr>
                                    <th>Type</th>
                                    <th>Amount</th>
                                    <th>Description</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                {transactions.map((t) => (
                                    <tr key={t.id}>
                                        <td>{t.type}</td>
                                        <td>${(t.amount / 100).toFixed(2)}</td>
                                        <td>{t.description}</td>
                                        <td>{new Date(t.createdAt).toLocaleDateString()}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </section>
            )}
        </div>
    );
}