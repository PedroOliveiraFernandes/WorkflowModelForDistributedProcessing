package org.example;

public class App {
    public static void main(String[] args) {
        System.out.println(args[0]);
        ArgumentsModel argumentsModel = ArgumentsModel.fromArgs(args);
        var initial = Integer.parseInt(argumentsModel.constants[0]);
        var step = Integer.parseInt(argumentsModel.constants[1]);
        var calculatedValue =  initial + argumentsModel.currentIteration * step ;
        new ResultsModel("" + calculatedValue).writeToOutputFile();
    }
}