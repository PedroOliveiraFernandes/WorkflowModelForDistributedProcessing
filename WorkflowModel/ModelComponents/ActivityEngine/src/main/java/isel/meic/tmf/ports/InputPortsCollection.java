package isel.meic.tmf.ports;

import java.util.List;

public class InputPortsCollection {
    public final List<InputPort> inputPorts;

    public boolean hasInputPorts() {
        return inputPorts != null && !inputPorts.isEmpty();
    }

    public InputPortsCollection(List<InputPort> inputPorts) {
        this.inputPorts = inputPorts;
    }

    public void confirmAllTokens() {
        inputPorts.forEach(InputPort::confirmToken);
    }
}
