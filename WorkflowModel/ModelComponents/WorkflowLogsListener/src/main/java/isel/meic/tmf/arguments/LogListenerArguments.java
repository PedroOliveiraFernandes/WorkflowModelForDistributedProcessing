package isel.meic.tmf.arguments;

public class LogListenerArguments {
    public Broker broker;
    public String filterByWorkflowName;
    public String outputFile;

    public static class Broker {
        public String host;
        public int port;

        public Broker(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
