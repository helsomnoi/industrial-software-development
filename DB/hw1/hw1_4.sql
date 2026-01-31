-- #4 Найти клиентов, у которых больше 2 активных счетов
SELECT clients.client_id, clients.full_name, COUNT(accounts.account_id) as active_accounts
FROM clients
INNER JOIN accounts ON clients.client_id = accounts.client_id
WHERE accounts.status = 'active'
GROUP BY clients.client_id, clients.full_name
HAVING COUNT(accounts.account_id) > 2;
