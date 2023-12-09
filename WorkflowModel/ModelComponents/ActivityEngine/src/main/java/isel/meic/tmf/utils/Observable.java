package isel.meic.tmf.utils;

import java.util.HashSet;

public abstract class Observable implements IObservable {

    private final HashSet<IObserver> observers;

    protected Observable() {
        this.observers = new HashSet<>();
    }

    @Override
    public void subscribe(IObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unsubscribe(IObserver observer) {
        this.observers.add(observer);
    }

    protected void notifyAllSubscribers() {
        observers.forEach(IObserver::notifyChange);
    }
}
