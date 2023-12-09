package org.example;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;
import org.example.annotations.AllAnnotationsDetected;
import org.example.annotations.AnnotationDetected;
import org.example.workflowEssentials.ArgumentsModel;
import org.example.workflowEssentials.ResultsModel;

import java.io.IOException;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws IOException {
        var arguments = ArgumentsModel.fromArgs(args);

        var translateService = TranslateOptions.getDefaultInstance()
                                               .getService();

        var translatedDetections = Arrays.stream(new Gson().fromJson(arguments.arguments[0],
                                                                     AllAnnotationsDetected.class).annotationsDetecteed)
                                         .map(annotationDetected -> {
                                             annotationDetected.label = translateLabel(translateService,
                                                                                       annotationDetected.label,
                                                                                       arguments.constants[0]);
                                             return annotationDetected;
                                         })
                                         .toArray(AnnotationDetected[]::new);

        var allobjectsTranslated = new Gson().toJson(new AllAnnotationsDetected(translatedDetections));

        new ResultsModel(allobjectsTranslated).writeToOutputFile();
    }

    static String translateLabel(Translate translateService, String text, String translateToLanguage) {
        try {

            Translation translation = translateService.translate(
                    text,
//                        Translate.TranslateOption.sourceLanguage("en"),
                    Translate.TranslateOption.targetLanguage(translateToLanguage));
            return translation.getTranslatedText();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
