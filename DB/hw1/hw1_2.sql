-- #2 Вывести ФИО клиента, тип счета, валюту и статус счета
SELECT clients.client_id, clients.full_name, accounts.account_type, accounts.currency, accounts.status
FROM clients
INNER JOIN accounts ON clients.client_id = accounts.client_id;
