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