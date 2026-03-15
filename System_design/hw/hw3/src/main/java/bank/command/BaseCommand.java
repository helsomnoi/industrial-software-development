package bank.command;

public abstract class BaseCommand implements Command {
    protected boolean executed = false;

    public void undo() {
        throw new UnsupportedOperationException("Отмена не поддерживается");
    }

    protected void validateNotExecuted() {
        if (executed) {
            throw new IllegalStateException("Команда уже выполнена");
        }
    }

    public boolean isExecuted() {
        return executed;
    }
}
