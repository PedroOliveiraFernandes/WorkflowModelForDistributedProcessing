package isel.meic.tmf.engine;

import isel.meic.tmf.configuration.models.IterationsDto;
import isel.meic.tmf.utils.Observable;

public class IterationController extends Observable {
    private final int step;
    private final int total;
    private int current;

    public IterationController(IterationsDto iterationConfig) {
        step = iterationConfig.step;
        current = iterationConfig.start;
        total = iterationConfig.total;
    }

    public boolean isCurrentEqualTo(int iteration) {
        synchronized (this) {
            return current == iteration;
        }
    }

    public void stepIteration() {
        synchronized (this) {
            current += step;
        }
        notifyAllSubscribers();
    }

    public int getCurrent() {
        synchronized (this) {
            return current;
        }
    }

    public int getNextInteration() {
        return getCurrent() + step;
    }

    public boolean canContinue() {
        return total == 0 || getNextInteration() < total;
    }
}
