package bank.command;

public interface Command {
    void execute();
    String getDescription();
    boolean isExecuted();
}
