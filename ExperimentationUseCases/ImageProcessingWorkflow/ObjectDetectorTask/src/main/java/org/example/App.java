package org.example;

import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import org.example.annotations.AllAnnotationsDetected;
import org.example.annotations.AnnotationDetected;
import org.example.annotations.Vertex;
import org.example.workflowEssentials.ArgumentsModel;
import org.example.workflowEssentials.ResultsModel;

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        var arguments = ArgumentsModel.fromArgs(args);
        var gcsPath = arguments.arguments[0];
        var detections = detectLocalizedObjectsGcs(gcsPath);

        new ResultsModel(new Gson().toJson(detections)).writeToOutputFile();
    }

    public static AllAnnotationsDetected detectLocalizedObjectsGcs(String gcsPath) throws IOException {

        ImageSource imgSource = ImageSource.newBuilder()
                                           .setGcsImageUri(gcsPath)
                                           .build();
        Image img = Image.newBuilder()
                         .setSource(imgSource)
                         .build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                                    .addFeatures(Feature.newBuilder()
                                                        .setType(Feature.Type.OBJECT_LOCALIZATION))
                                    .setImage(img)
                                    .build();
        BatchAnnotateImagesRequest singleBatchRequest = BatchAnnotateImagesRequest.newBuilder()
                                                                                  .addRequests(request)
                                                                                  .build();
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            // Perform the request
            BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(singleBatchRequest);
            List<AnnotateImageResponse> listResponses = batchResponse.getResponsesList();

            if (listResponses.isEmpty()) {
                System.out.println("Empty response, no object detected.");
                return new AllAnnotationsDetected();
            }
            // get the only response
            AnnotateImageResponse response = listResponses.get(0);
            // print information in standard output

            var results = new AnnotationDetected[response.getLocalizedObjectAnnotationsList()
                                                         .size()];
            for (int i = 0; i < response.getLocalizedObjectAnnotationsList()
                                        .size(); ++i) {
                LocalizedObjectAnnotation annotation = response.getLocalizedObjectAnnotationsList()
                                                               .get(i);

                results[i] = mapAnnotation(annotation);
//                System.out.format("Normalized Vertices:%n");
//                annotation
//                        .getBoundingPoly()
//                        .getNormalizedVerticesList()
//                        .forEach(vertex -> System.out.println("(" + vertex.getX() + ", " + vertex.getY() + ")"));

                System.out.println();
            }
            return new AllAnnotationsDetected(results);
//            // annotate in memory Blob image
//            BufferedImage bufferImg = getBlobBufferedImage(storage, BlobId.of(bucketName, blobName));
//            annotateWithObjects(bufferImg, response.getLocalizedObjectAnnotationsList());
//            // save the image to a new blob in the same bucket. The name of new blob has the annotated prefix
//            writeAnnotatedImage(storage, bufferImg, bucketName, "annotated-" + blobName);
//            System.out.println("Annoted Blob URL: " + "https://storage.googleapis.com/" + bucketName + "/" +
//            "annotated-" + blobName);
        }
    }

    private static AnnotationDetected mapAnnotation(LocalizedObjectAnnotation annotation) {
        System.out.format("Object name: %s%n", annotation.getName());
        System.out.format("Confidence: %s%n", annotation.getScore());
        return new AnnotationDetected(annotation.getName(), annotation.getScore(),
                                      AnnotationDetected.Type.OBJECT, mapPoly(annotation.getBoundingPoly()));
    }

    private static Vertex[] mapPoly(BoundingPoly boundingPoly) {
        return boundingPoly.getNormalizedVerticesList()
                           .stream()
                           .map(v -> new Vertex(v.getX(), v.getY()))
                           .toArray(Vertex[]::new);
    }
}
