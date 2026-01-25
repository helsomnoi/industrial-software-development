-- #7 Определить “активность клиента” по количеству операций за последние 90 дней
WITH clients_activity AS (
    SELECT clients.client_id, clients.full_name, COUNT(transactions.transaction_id) as operation_count
    FROM clients
    LEFT JOIN accounts ON clients.client_id = accounts.client_id
    LEFT JOIN transactions ON accounts.account_id = transactions.account_id
        AND transactions.txn_date >= CURRENT_DATE - INTERVAL '90 days'
    GROUP BY clients.client_id, clients.full_name
)
SELECT clients_activity.client_id, clients_activity.full_name, clients_activity.operation_count,
    CASE
        WHEN operation_count = 0 THEN 'inactive'
        WHEN operation_count BETWEEN 1 AND 5 THEN 'low'
        WHEN operation_count BETWEEN 6 AND 20 THEN 'medium'
        WHEN operation_count > 20 THEN 'high'
    END as activity_level
FROM clients_activity
ORDER BY client_id;
