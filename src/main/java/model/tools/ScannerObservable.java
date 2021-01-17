package model.tools;

public interface ScannerObservable {
    void triggerListenersForStart();
    void triggerListenersForStop();
    void triggerListenersWithError(String message, Exception e);
}
