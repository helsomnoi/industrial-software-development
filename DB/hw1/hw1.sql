# Решение
-- #1 Найти активные счета в EUR, открытые после 2024-01-01, отсортировать по дате открытия
SELECT * FROM accounts
WHERE status='active'
	AND currency ILIKE 'EUR'
	AND opened_at > DATE '2024-01-01'
ORDER BY opened_at;

-- #2 Вывести ФИО клиента, тип счета, валюту и статус счета
SELECT clients.client_id, clients.full_name, accounts.account_type, accounts.currency, accounts.status
FROM clients
INNER JOIN accounts ON clients.client_id = accounts.client_id;

-- #3 Вывести всех клиентов и количество их счетов (включая 0)
SELECT clients.client_id, clients.full_name, COUNT(accounts.account_id) as account_count
FROM clients
LEFT JOIN accounts ON clients.client_id = accounts.client_id -- Берем всех клиентов, даже без счетов COUNT([NULL])=0
GROUP BY clients.client_id
ORDER BY clients.client_id

-- #4 Найти клиентов, у которых больше 2 активных счетов
SELECT clients.client_id, clients.full_name, COUNT(accounts.account_id) as active_accounts
FROM clients
INNER JOIN accounts ON clients.client_id = accounts.client_id
WHERE accounts.status = 'active'
GROUP BY clients.client_id, clients.full_name
HAVING COUNT(accounts.account_id) > 2;

-- #5 Найти счета, у которых сумма входящих операций (deposit + transfer_in) выше среднего по банку
WITH incoming_sums AS(
	SELECT account_id, SUM(amount) as total_incoming FROM transactions
	WHERE transactions.txn_type IN ('deposit', 'transfer_in')
	GROUP BY account_id
),
bank_avg AS(
	SELECT AVG(total_incoming) as avg_incoming FROM incoming_sums
)
SELECT accounts.account_id, accounts.client_id, incoming_sums.total_incoming, bank_avg.avg_incoming
FROM accounts
INNER JOIN incoming_sums ON accounts.account_id = incoming_sums.account_id
CROSS JOIN bank_avg -- каждая строка из левой таблицы с (одной строкой) правой
WHERE incoming_sums.total_incoming > bank_avg.avg_incoming;

-- #6  Топ-5 клиентов по сумме всех операций (оборот) за 2025 год
SELECT clients.client_id, clients.full_name, SUM(transactions.amount) as total_turnover
FROM clients
INNER JOIN accounts ON clients.client_id = accounts.client_id
INNER JOIN transactions ON transactions.account_id = accounts.account_id
WHERE EXTRACT (YEAR FROM transactions.txn_date) = 2025
GROUP BY clients.client_id, clients.full_name
ORDER BY total_turnover DESC
LIMIT 5;

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

-- #8 Найти кредиты, по которым сумма успешных платежей < 50% от principal
SELECT loans.loan_id, loans.principal, COALESCE(SUM(loan_payments.amount), 0) as total_paid
FROM loans
LEFT JOIN loan_payments ON loans.loan_id = loan_payments.loan_id
    AND loan_payments.status = 'success'
GROUP BY loans.loan_id, loans.principal
HAVING COALESCE(SUM(loan_payments.amount), 0) < loans.principal * 0.5;

-- #9 Показать все активные карты и кому они принадлежат (ФИО, account_id, срок действия)
SELECT cards.card_id, clients.full_name, accounts.account_id, cards.card_type, cards.issued_at, cards.expires_at
FROM cards
INNER JOIN accounts ON cards.account_id = accounts.account_id
INNER JOIN clients ON accounts.client_id = clients.client_id
WHERE cards.status = 'active'
	AND cards.expires_at > CURRENT_DATE
ORDER BY clients.full_name, cards.expires_at;

-- #10 Для каждого счета посчитать: количество операций и сумму списаний (withdrawal + transfer_out + fee)
SELECT accounts.account_id, clients.full_name, COUNT(transactions.transaction_id) as transaction_count,
	SUM(	-- 
		CASE 
			WHEN transactions.txn_type IN ('withdrawal', 'transfer_out', 'fee')
			THEN transactions.amount
			ELSE 0
		END
	) as total_sum
FROM accounts
LEFT JOIN transactions ON accounts.account_id = transactions.account_id
LEFT JOIN clients ON accounts.client_id = clients.client_id
GROUP BY accounts.account_id, clients.full_name;