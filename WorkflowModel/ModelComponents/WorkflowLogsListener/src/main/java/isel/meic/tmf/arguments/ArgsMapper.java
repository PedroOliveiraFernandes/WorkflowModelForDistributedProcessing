package isel.meic.tmf.arguments;

public class ArgsMapper {
    public static LogListenerArguments map(String[] args) {
        var result = new LogListenerArguments();

        for (String arg : args) {
            var auxArg = arg.replace("-", "");
            var argSplit = auxArg.split("=");

            if (argSplit.length != 2) continue;

            switch (argSplit[0]) {
                case "broker":
                    result.broker = extractBroker(argSplit[1]);
                    break;
                case "filterByWorkflowName":
                    result.filterByWorkflowName = argSplit[1];
                    break;
                case "outputFile":
                    result.outputFile = argSplit[1];
                    break;
            }
        }
        return result;
    }

    private static LogListenerArguments.Broker extractBroker(String argValue) {
        var brokerSplit = argValue.split(":");
        if (brokerSplit.length != 2) return null;
        return new LogListenerArguments.Broker(brokerSplit[0], Integer.parseInt(brokerSplit[1]));
    }
}
