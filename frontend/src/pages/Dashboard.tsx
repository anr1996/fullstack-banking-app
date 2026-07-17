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

    // The state for the transfer form
    const [fromAccountId, setFromAccountId] = useState<number | ''>('');
    const [toAccountId, setToAccountId] = useState<number | ''>('');
    const [transferAmount, setTransferAmount] = useState<string>('');
    const [transferDescription, setTransferDescription] = useState<string>('');
    const [transferError, setTransferError] = useState<string>('');
    const [transferSuccess, setTransferSuccess] = useState<string>('');

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    /**
     * The useEffect runs after the component mounts.
     * It will check authentication and fetch the account data.
     */
    useEffect(() => {
        // If no token exists, it redirects to the login immediately.
        if (!isLoggedIn()) {
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
     * This will handle the transfer form submission.
     * It sends money from one account to another via the backend.
     * 
     * @param e (The form submit event.)
     */
    async function handleTransfer(e: React.FormEvent) {
        // Prevent the browser from reloading the page.
        e.preventDefault();

        // Clear the previous messages.
        setTransferError('');
        setTransferSuccess('');

        // Validate that source and destination are different.
        if (fromAccountId === toAccountId) {
            setTransferError('You cannot transfer to the same account.');
            return;
        }

        // Validate that the amount is positive.
        const amountInCents = Math.round(parseFloat(transferAmount) * 100);
        if (isNaN(amountInCents) || amountInCents <= 0) {
            setTransferError('Please enter a valid positive amount.');
            return;
        }

        try {
            // Send the transfer request to the backend.
            // Amount is sent in cents as a Long.
            await apiFetch('/transfers', {
                method: 'POST',
                body: JSON.stringify({
                    fromAccountId: Number(fromAccountId),
                    toAccountId: Number(toAccountId),
                    amount: amountInCents,
                    description: transferDescription || 'transfer',
                }),
            });

            // Show the sucess message.
            setTransferSuccess('Transfer completed successfully.');

            // Reset form fields.
            setFromAccountId('');
            setToAccountId('');
            setTransferAmount('');
            setTransferDescription('');

            // Refresh account the account list to show the updated balances.
            const updatedAccounts = await apiFetch('/accounts');
            setAccounts(updatedAccounts);

            // If an account was selected, refresh its transactions too.
            if (selectedAccount) {
                const updatedTransactions = await apiFetch(`/accounts/${selectedAccount}/transactions`);
                setTransactions(updatedTransactions);
            }
        } catch (error: any) {
            setTransferError(error.message || 'Transfer failed.');
        }
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
                                className={selectedAccount === account.id ? 'selected' : ''}
                            >
                                <strong>{account.type}</strong> - {account.accountNumber}
                                <br />
                                Balance: ${(account.balance / 100).toFixed(2)}
                            </li>
                        ))}
                    </ul>
                )}
            </section>

            {/** Transfer form (only show if user has at least 2 accounts). */}
            {accounts.length >= 2 && (
                <section>
                    <h2>Transfer Money</h2>
                    <form onSubmit={handleTransfer}>
                        <div>
                            <label> From Account</label>
                            <select
                                value={fromAccountId}
                                onChange={(e) => setFromAccountId(Number(e.target.value))}
                                required
                            >
                                <option value="">Select account</option>
                                {accounts.map((account) => (
                                    <option key={account.id} value={account.id}>
                                        {account.type} - {account.accountNumber}
                                        (${(account.balance / 100).toFixed(2)})
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label>To Account</label>
                            <select
                            value={toAccountId}
                            onChange={(e) => setToAccountId(Number(e.target.value))}
                            required
                            >
                                <option value="">Select account</option>
                                {accounts.map((account) => (
                                    <option key={account.id} value={account.id}>
                                        {account.type} - {account.accountNumber}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label>Amount ($)</label>
                            <input
                                type="number"
                                step="0.01"
                                min="0.01"
                                value={transferAmount}
                                onChange={(e) => setTransferAmount(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <label>Description (optional)</label>
                            <input
                                type="text"
                                value={transferDescription}
                                onChange={(e) => setTransferDescription(e.target.value)}
                            />
                        </div>

                            {transferError && <p className="error">{transferError}</p>}
                            {transferSuccess && <p className="success">{transferSuccess}</p>}

                            <button type="submit">Transfer</button>
                    </form>
                </section>
            )}

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