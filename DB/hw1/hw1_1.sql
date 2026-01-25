-- #1 Найти активные счета в EUR, открытые после 2024-01-01, отсортировать по дате открытия
SELECT * FROM accounts
WHERE status='active'
	AND currency ILIKE 'EUR'
	AND opened_at > DATE '2024-01-01'
ORDER BY opened_at;
