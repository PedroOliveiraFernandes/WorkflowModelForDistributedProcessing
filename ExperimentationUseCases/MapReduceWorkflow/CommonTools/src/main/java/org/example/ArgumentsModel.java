package org.example;

import com.google.gson.Gson;

public class ArgumentsModel {
    public String[] arguments;
    public String[] constants;
    public int currentIteration;
    public String activityName;

    public static ArgumentsModel fromArgs(String[] args) {
        return new Gson().fromJson(args[0], ArgumentsModel.class);
    }
}