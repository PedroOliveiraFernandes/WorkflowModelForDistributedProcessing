package isel.meic.tmf.arguments;

public class ArgumentsValidator {
    public static void validate(LogListenerArguments arguments) {
        if (arguments.broker == null || arguments.broker.host == null || arguments.broker.port == 0) {
            System.out.println("Broker is needed to be specified in order to listen to logs. \nPlease add the " +
                                       "argument as: -broker=host:port\nFor example: -broker=1.2.3.4:50");
        }
    }
}
