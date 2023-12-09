package org.example;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ArgumentsModel argumentsModel = ArgumentsModel.fromArgs(args);
        var argument0 = Integer.parseInt(argumentsModel.arguments[0]);
        var argument1 = Integer.parseInt(argumentsModel.arguments[1]);

        var sumValue = argument0 + argument1;
        new ResultsModel("" + sumValue).writeToOutputFile();
    }
}
