package isel.meic.tmf.ports;

import java.util.List;

public class OutputPortsCollection {
    public final List<OutputPort> outputPorts;

    public OutputPortsCollection(List<OutputPort> inputPorts) {
        this.outputPorts = inputPorts;
    }
}
