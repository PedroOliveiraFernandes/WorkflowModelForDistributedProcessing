package org.example;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import org.example.annotations.AllAnnotationsDetected;
import org.example.annotations.AnnotationDetected;
import org.example.annotations.Vertex;
import org.example.workflowEssentials.ArgumentsModel;
import org.example.workflowEssentials.ResultsModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        var arguments = ArgumentsModel.fromArgs(args);
        var gcsPath = arguments.arguments[0];
        var detections = detectText(gcsPath);
        normalizeVerticesInDetections(gcsPath, detections);

        new ResultsModel(new Gson().toJson(detections)).writeToOutputFile();
    }

    private static void normalizeVerticesInDetections(String gcsPath,
                                                      AllAnnotationsDetected detections) throws IOException {
        if (detections.annotationsDetecteed.length == 0) return;
        var storage = StorageOptions.getDefaultInstance()
                                    .getService();
        // annotate in memory Blob image
        BlobId blobId = BlobId.fromGsUtilUri(gcsPath);

        Blob blob = storage.get(blobId);
        if (blob == null) {
            System.out.println("No such Blob exists !");
            throw new IOException("Blob <" + blobId.getName() + "> not found in bucket <" + blobId.getBucket() + ">");
        }
        ReadChannel reader = blob.reader();
        InputStream in = Channels.newInputStream(reader);
        BufferedImage bufferImg = ImageIO.read(in);

        System.out.println(bufferImg);
        float imageWidth = bufferImg.getWidth(), imageHeight = bufferImg.getHeight();

        Arrays.stream(detections.annotationsDetecteed)
              .forEach(annotationDetected -> {

                  for (int i = 0; i < annotationDetected.polygon.length; i++) {
                      annotationDetected.polygon[i].x = annotationDetected.polygon[i].x / imageWidth;
                      annotationDetected.polygon[i].y = annotationDetected.polygon[i].y / imageHeight;
                  }
              });
    }

    public static AllAnnotationsDetected detectText(String gsURI) throws IOException {

//        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
//        Image img = Image.newBuilder().setContent(imgBytes).build();

        //String gcsPath = "gs://" + bucketName + "/" + blobName;
        ImageSource imgSource = ImageSource.newBuilder()
                                           .setGcsImageUri(gsURI)
                                           .build();
        Image img = Image.newBuilder()
                         .setSource(imgSource)
                         .build();
        // https://cloud.google.com/vision/docs/features-list
        Feature feat = Feature.newBuilder()
                              .setType(Feature.Type.DOCUMENT_TEXT_DETECTION)
                              .build();

        List<AnnotateImageRequest> requests = new ArrayList<>();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                                                           .addFeatures(feat)
                                                           .setImage(img)
                                                           .build();
        requests.add(request);
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> listResponses = batchResponse.getResponsesList();

            if (listResponses.isEmpty() || listResponses.get(0)
                                                        .getFullTextAnnotation()
                                                        .getPagesList()
                                                        .isEmpty()) {
                System.out.println("Empty response, no text detected.");
                return new AllAnnotationsDetected();
            }
            // get the only response
            AnnotateImageResponse response = listResponses.get(0);

            // For full list of available annotations, see http://g.co/cloud/vision/docs

            var blocks = response.getFullTextAnnotation()
                                 .getPages(0)
                                 .getBlocksList();

            var results = new AnnotationDetected[blocks.size()];

            for (int i = 0; i < blocks.size(); ++i) {
                results[i] = mapToAnnotationDetected(blocks.get(i));
                //                    DetectedText dt = new DetectedText();
//                    dt.text = annotation.getDescription();
//                    dt.language = detectLanguage(translate, dt.text);
//                    dt.poly = annotation.getBoundingPoly();
//                    allText.add(dt);
            }
            return new AllAnnotationsDetected(results);
        }
    }

    private static AnnotationDetected mapToAnnotationDetected(Block block) {
        String textDetected = mapToText(block);
        System.out.format("Object name: %s%n", textDetected);
        System.out.format("Confidence: %s%n", block.getConfidence());
        System.out.println();
//        System.out.format("Vertices: %s%n", block.getBoundingBox());

        return new AnnotationDetected(textDetected, block.getConfidence(), AnnotationDetected.Type.TEXT,
                                      mapPoly(block.getBoundingBox()));
    }

    private static String mapToText(Block block) {
        StringBuilder textBlockResult = new StringBuilder();

        block.getParagraphsList()
             .forEach(p -> p.getWordsList()
                            .forEach(word -> {
                                word.getSymbolsList()
                                    .forEach(s -> {

                                        textBlockResult.append(s.getText());
                                        if (s.hasProperty() && s.getProperty()
                                                                .hasDetectedBreak())
                                            switch (s.getProperty()
                                                     .getDetectedBreak()
                                                     .getType()) {
                                                case HYPHEN:
                                                    textBlockResult.append("-");
                                                    break;
                                                default:
                                                    textBlockResult.append(" ");
                                            }
                                    });
                            }));

        if (textBlockResult.charAt(textBlockResult.length() - 1) == '\n')
            textBlockResult.deleteCharAt(textBlockResult.length() - 1);

        return textBlockResult.toString();
    }

    private static Vertex[] mapPoly(BoundingPoly boundingPoly) {
        return boundingPoly.getVerticesList()
                           .stream()
                           .map(v -> new Vertex(v.getX(), v.getY()))
                           .toArray(Vertex[]::new);
    }
}
