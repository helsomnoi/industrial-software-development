package bank.command;

public class TimedCommandDecorator implements Command {
    private final Command delegate;

    public TimedCommandDecorator(Command delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute() {
        long start = System.currentTimeMillis();
        try {
            delegate.execute();
        } finally {
            long duration = System.currentTimeMillis() - start;
            System.out.println("⏱️ Время выполнения команды '" + delegate.getDescription() + "': " + duration + " мс");
        }
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public boolean isExecuted() {
        return delegate.isExecuted();
    }
}