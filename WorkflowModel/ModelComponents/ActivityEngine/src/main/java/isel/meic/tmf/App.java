package isel.meic.tmf;

import isel.meic.tmf.configuration.ActivityConfiguration;
import isel.meic.tmf.configuration.ConfigurationParser;
import isel.meic.tmf.engine.Engine;

public class App {
    static {
        var os = System.getProperty("os.name");
        IS_RUNNING_IN_WINDOWS = os.toLowerCase()
                                  .contains("windows");
    }

    public static final boolean IS_RUNNING_IN_WINDOWS;

    public static void main(String[] args) {
        ActivityConfiguration configuration = ConfigurationParser.parse(args);
        new Engine(configuration).run();
    }
}

