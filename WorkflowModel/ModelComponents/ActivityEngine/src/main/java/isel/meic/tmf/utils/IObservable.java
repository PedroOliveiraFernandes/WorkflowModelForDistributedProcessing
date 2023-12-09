package isel.meic.tmf.utils;

public interface IObservable {
    void subscribe(IObserver observer);
    void unsubscribe(IObserver observer);
}
