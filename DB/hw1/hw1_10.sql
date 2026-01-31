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