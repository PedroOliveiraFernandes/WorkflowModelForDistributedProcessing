package isel.meic.tmf.engine.states;

import isel.meic.tmf.engine.IStateContext;

public abstract class EngineState {

    public abstract void execute(IStateContext context);

    protected void setNextStateToFinalState(IStateContext context) {
        var engine = context.getEngine();
        engine.setState(new S9FinalizationState());
    }

    protected void setNextState(EngineState state, IStateContext context) {
        var engine = context.getEngine();
        engine.setState(state);
    }
}
