package bank.factory;

public interface UniversalFactory {
    <T> T create(Object... params);
}
