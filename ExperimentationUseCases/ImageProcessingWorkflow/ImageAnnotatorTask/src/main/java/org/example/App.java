package org.example;

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import org.example.annotations.AllAnnotationsDetected;
import org.example.annotations.AnnotationDetected;
import org.example.annotations.Vertex;
import org.example.workflowEssentials.ArgumentsModel;
import org.example.workflowEssentials.ResultsModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws IOException {
        var arguments = ArgumentsModel.fromArgs(args);
        var gcsPath = arguments.arguments[0];

        ArrayList<AnnotationDetected> annotationsDeteccted = new ArrayList<>();

        for (int i = 1; i < arguments.arguments.length; i++) {
            var objectsDetectedString = arguments.arguments[i];
            var allobjectsDetected = new Gson().fromJson(objectsDetectedString, AllAnnotationsDetected.class);
            annotationsDeteccted.addAll(Arrays.stream(allobjectsDetected.annotationsDetecteed)
                                              .collect(Collectors.toList()));
        }

        BlobId blobIdAnnotated = annotate(gcsPath, annotationsDeteccted.toArray(AnnotationDetected[]::new));

        new ResultsModel(blobIdAnnotated.toGsUtilUri()).writeToOutputFile();
    }

    private static BlobId annotate(String gcsPath, AnnotationDetected[] annotationDetected) throws IOException {
        var storage = StorageOptions.getDefaultInstance()
                                    .getService();
        // annotate in memory Blob image
        BlobId blobIdToAnnotate = BlobId.fromGsUtilUri(gcsPath);
        BufferedImage bufferImg = getBlobBufferedImage(storage, blobIdToAnnotate);
        annotateWithObjects(bufferImg, annotationDetected);
        // save the image to a new blob in the same bucket. The name of new blob has the annotated prefix

        var blobIdAnnotated = BlobId.of(blobIdToAnnotate.getBucket(), "annotated_" + blobIdToAnnotate.getName());
        writeAnnotatedImage(storage, bufferImg, blobIdAnnotated);
        System.out.println("Annoted Blob URL: " + "https://storage.googleapis.com/" + blobIdAnnotated.getBucket() +
                                   "/" +
                                   blobIdAnnotated.getName());
        return blobIdAnnotated;
    }

    private static BufferedImage getBlobBufferedImage(Storage storage, BlobId blobId) throws IOException {

        Blob blob = storage.get(blobId);
        if (blob == null) {
            System.out.println("No such Blob exists !");
            throw new IOException("Blob <" + blobId.getName() + "> not found in bucket <" + blobId.getBucket() + ">");
        }
        ReadChannel reader = blob.reader();
        InputStream in = Channels.newInputStream(reader);
        var result = ImageIO.read(in);

        if (result == null) {
            throw new IOException("Image could not be read, format or content invalid");
        }
        return result;
    }

    private static void annotateWithObjects(BufferedImage img, AnnotationDetected[] annotationDetected) {
        for (AnnotationDetected obj : annotationDetected) {
            annotateWithObject2(img, obj);
        }
    }

    private static void annotateWithObject2(BufferedImage img, AnnotationDetected annotationDetected) {
        denormalizeVerticesInDetection(img.getWidth(), img.getHeight(), annotationDetected);

        Graphics2D gfx = img.createGraphics();
        int fontSize = Math.min(img.getWidth(), img.getHeight()) / 20;
        gfx.setFont(new Font("Arial", Font.PLAIN, fontSize));
        gfx.setStroke(new BasicStroke(3));
        gfx.setColor(new Color(0xff0000));
        Polygon poly = new Polygon();
        // draw object name
        System.out.println("annotationDetected name: " + annotationDetected.label);
        // Calculate the angle of the first edge
        float dx = (annotationDetected.polygon[1].x) - (annotationDetected.polygon[0].x);
        float dy = (annotationDetected.polygon[1].y) - (annotationDetected.polygon[0].y);
        double angle = Math.atan2(dy, dx);

        // Rotate the graphics context to align the text with the polygon's angle
        gfx.rotate(angle, annotationDetected.polygon[0].x, annotationDetected.polygon[0].y);

        if (annotationDetected.getType() == AnnotationDetected.Type.OBJECT) {
            // Calculate the offset values based on the font size and image dimensions
            int xOffset = (int) (4 * fontSize / 32.0);
            int yOffset = (int) (28 * fontSize / 32.0);
            gfx.drawString(annotationDetected.label,
                           (int) (annotationDetected.polygon[0].x + xOffset),
                           (int) (annotationDetected.polygon[0].y + yOffset));
        } else {
            // Calculate the offset values based on the font size and image dimensions
            int xOffset = (int) (4 * fontSize / 32.0);
            int yOffset = (int) (10 * fontSize / 32.0);
            gfx.drawString(annotationDetected.label,
                           (int) (annotationDetected.polygon[0].x + xOffset),
                           (int) (annotationDetected.polygon[0].y - yOffset));
        }

        // Restore the original rotation for subsequent drawing operations
        gfx.rotate(-angle, annotationDetected.polygon[0].x, annotationDetected.polygon[0].y);

        // draw bounding box of object
        for (Vertex vertex : annotationDetected.polygon) {
            poly.addPoint((int) vertex.x, (int) vertex.y);
        }

        if (annotationDetected.getType() == AnnotationDetected.Type.OBJECT) gfx.setColor(new Color(0x00ff00));
        else gfx.setColor(new Color(0xEB36FF));

        gfx.draw(poly);
    }

    private static void writeAnnotatedImage(Storage storage, BufferedImage bufferImg, BlobId blobId) throws IOException {
        BlobInfo blobInfo = BlobInfo
                .newBuilder(blobId)
                .setContentType("image/jpeg")
                .build();
        Blob destBlob = storage.create(blobInfo);
        WriteChannel writeChannel = storage.writer(destBlob);
        OutputStream out = Channels.newOutputStream(writeChannel);
        ImageIO.write(bufferImg, "jpg", out);
        out.close();
        // put Blob with public access
        Blob blob = storage.get(blobId);
        Acl.Entity aclEnt = Acl.User.ofAllUsers();
        Acl.Role role = Acl.Role.READER;
        Acl acl = Acl.newBuilder(aclEnt, role)
                     .build();
        blob.createAcl(acl);
    }

    private static void denormalizeVerticesInDetection(float width, float heigth,
                                                       AnnotationDetected annotationDetected) {
        for (int i = 0; i < annotationDetected.polygon.length; i++) {
            annotationDetected.polygon[i].x = annotationDetected.polygon[i].x * width;
            annotationDetected.polygon[i].y = annotationDetected.polygon[i].y * heigth;
        }
    }
}
