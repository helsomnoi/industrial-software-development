-- #8 Найти кредиты, по которым сумма успешных платежей < 50% от principal
SELECT loans.loan_id, loans.principal, COALESCE(SUM(loan_payments.amount), 0) as total_paid
FROM loans
LEFT JOIN loan_payments ON loans.loan_id = loan_payments.loan_id
    AND loan_payments.status = 'success'
GROUP BY loans.loan_id, loans.principal
HAVING COALESCE(SUM(loan_payments.amount), 0) < loans.principal * 0.5;
