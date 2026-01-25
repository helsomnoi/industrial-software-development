-- #9 Показать все активные карты и кому они принадлежат (ФИО, account_id, срок действия)
SELECT cards.card_id, clients.full_name, accounts.account_id, cards.card_type, cards.issued_at, cards.expires_at
FROM cards
INNER JOIN accounts ON cards.account_id = accounts.account_id
INNER JOIN clients ON accounts.client_id = clients.client_id
WHERE cards.status = 'active'
	AND cards.expires_at > CURRENT_DATE
ORDER BY clients.full_name, cards.expires_at;
