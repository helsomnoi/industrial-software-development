package bank.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OperationTypeTest {

    @Test
    void values_ShouldContainIncomeAndExpense() {
        OperationType[] values = OperationType.values();
        assertEquals(2, values.length);
        assertEquals(OperationType.INCOME, values[0]);
        assertEquals(OperationType.EXPENSE, values[1]);
    }

    @Test
    void getDescription_ShouldReturnCorrect() {
        assertEquals("Доход", OperationType.INCOME.getDescription());
        assertEquals("Расход", OperationType.EXPENSE.getDescription());
    }
}