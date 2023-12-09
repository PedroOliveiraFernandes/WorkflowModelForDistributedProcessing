package isel.meic.tmf.engine;

import java.util.HashMap;
import java.util.Map;

public class StateContext implements IStateContext {

    private final Map<Class<?>, Object> services;
    private final Engine engine;

    public StateContext(Engine engine) {
        this.engine = engine;
        services = new HashMap<>();
    }

    @Override
    public <T> T get(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }

    @Override
    public void register(Class<?> serviceClass, Object service) {
        services.put(serviceClass, service);
    }

    @Override
    public Engine getEngine() {
        return engine;
    }
}
