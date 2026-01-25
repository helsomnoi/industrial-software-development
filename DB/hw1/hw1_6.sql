-- #6  Топ-5 клиентов по сумме всех операций (оборот) за 2025 год
SELECT clients.client_id, clients.full_name, SUM(transactions.amount) as total_turnover
FROM clients
INNER JOIN accounts ON clients.client_id = accounts.client_id
INNER JOIN transactions ON transactions.account_id = accounts.account_id
WHERE EXTRACT (YEAR FROM transactions.txn_date) = 2025
GROUP BY clients.client_id, clients.full_name
ORDER BY total_turnover DESC
LIMIT 5;
