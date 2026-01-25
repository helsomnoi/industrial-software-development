-- #3 Вывести всех клиентов и количество их счетов (включая 0)
SELECT clients.client_id, clients.full_name, COUNT(accounts.account_id) as account_count
FROM clients
LEFT JOIN accounts ON clients.client_id = accounts.client_id -- Берем всех клиентов, даже без счетов COUNT([NULL])=0
GROUP BY clients.client_id
ORDER BY clients.client_id
