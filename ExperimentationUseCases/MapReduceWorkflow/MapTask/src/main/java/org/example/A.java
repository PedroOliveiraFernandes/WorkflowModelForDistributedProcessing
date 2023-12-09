package org.example;

import java.util.Arrays;

public class A {
    public static String processString(String input) {
        // Remove trailing non-alphanumeric characters (except periods, single quotes, hyphens, and commas in certain
        // cases)
        String result = input.replaceAll("(?<=\\\\W|^)[^a-zA-Z]+|[^a-zA-Z]+(?=\\\\W|$)", "");//.replace("[^a-zA-Z0-9]");

        // Convert the result to lowercase
        result = result.toLowerCase();

        return result;
    }

    public static void main(String[] args) {
        String[] res = new String[]{
                "apple.",
                ".12.3",
                "12.3.",
                "12.3|\"",
                "\"publish/subscribe",
                "publish/subscribe.",
                "it's",
                "publish-subscribe",
                "publish/subscribe,",
                "apple'",
                "apple--apple,",
                "**apple'**",
                "apple4you",
                "4apple4you"
        };

        for (String r : res) {
            System.out.println(processString(r));   // Output: "apple"
        }

        String sentence = "\"publish/subscribe publish/subscribe. a it's pple--apple, apple4you Hello  world|! This, is. .is is a-test -sentence... .1.2 Hello-world";
        // Split the sentence by spaces and two consecutive special characters
        String[] parts = sentence.replaceAll("([^a-zA-Z]{2,}+)|\""," ").split("\\s");

        // Print the split parts
        System.out.println(Arrays.toString(parts));


    }
}