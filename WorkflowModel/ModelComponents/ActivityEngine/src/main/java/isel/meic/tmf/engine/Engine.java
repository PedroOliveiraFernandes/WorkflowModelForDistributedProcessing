package isel.meic.tmf.engine;

import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.engine.states.EngineState;
import isel.meic.tmf.engine.states.S1Inicialization;

public class Engine {
    private final ActivityConfiguration configuration;
    private EngineState state;

    public Engine(ActivityConfiguration configuration) {
        this.configuration = configuration;
        setState(new S1Inicialization());
    }

    public void setState(EngineState state) {
        this.state = state;
    }

    public void run() {
        var context = new StateContext(this);
        context.register(ActivityConfiguration.class, configuration);

        while (state != null) {
            state.execute(context);
        }
    }
}
