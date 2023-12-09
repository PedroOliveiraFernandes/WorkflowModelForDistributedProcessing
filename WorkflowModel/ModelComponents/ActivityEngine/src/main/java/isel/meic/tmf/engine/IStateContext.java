package isel.meic.tmf.engine;

public interface IStateContext {
    <T> T get(Class<T> serviceClass);

    void register(Class<?> serviceClass, Object service);

    Engine getEngine();
}
