package bank.command;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TimeCommandDecoratorTest {

    @Test
    void execute_ShouldMeasureTime() {
        Command mockCommand = mock(Command.class);
        when(mockCommand.getDescription()).thenReturn("Тестовая команда");

        TimedCommandDecorator decorator = new TimedCommandDecorator(mockCommand);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        decorator.execute();
        System.setOut(System.out);

        String output = outContent.toString();
        assertTrue(output.contains("Время выполнения команды 'Тестовая команда':"));
        assertTrue(output.contains("мс"));

        verify(mockCommand).execute();
    }

    @Test
    void delegateMethods_ShouldBeDelegated() {
        Command mockCommand = mock(Command.class);
        TimedCommandDecorator decorator = new TimedCommandDecorator(mockCommand);

        decorator.getDescription();
        verify(mockCommand).getDescription();

        decorator.isExecuted();
        verify(mockCommand).isExecuted();
    }
}