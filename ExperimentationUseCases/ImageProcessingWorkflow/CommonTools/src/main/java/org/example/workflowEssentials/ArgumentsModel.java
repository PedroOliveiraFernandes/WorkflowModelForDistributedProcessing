package org.example.workflowEssentials;

import com.google.gson.Gson;

public class ArgumentsModel {
    public String[] arguments;
    public String[] constants;
    public int currentIteration;

    public static ArgumentsModel fromArgs(String[] args) {
        return new Gson().fromJson(args[0], ArgumentsModel.class);
    }
}