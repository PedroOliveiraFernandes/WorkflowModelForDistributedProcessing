package org.example;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import org.example.workflowEssentials.ArgumentsModel;
import org.example.workflowEssentials.ResultsModel;
import org.example.workflowEssentials.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {"arguments":[],"constants":["input_%.jpg"],"currentIteration":0}
 */
public class App {

    static final String BUCKET_NAME = "tfm-41427-images";

    public static void main(String[] args) throws IOException {
        var arguments = ArgumentsModel.fromArgs(args);

        String pathToInputImageFileFromWorkdir = arguments.constants[0].replace("%", "" + arguments.currentIteration);
        String[] pathSplited = pathToInputImageFileFromWorkdir.split("/");
        String filenameInputImage = pathSplited[pathSplited.length - 1];
        var absolutePathToInputFile = Utils.PATH_TO_WORK_DIRECTORY + pathToInputImageFileFromWorkdir;
        String gcsPath = "gs://" + BUCKET_NAME + "/" + filenameInputImage;

        uploadFile(absolutePathToInputFile, gcsPath);

        new ResultsModel(gcsPath).writeToOutputFile();
    }

    private static void uploadFile(String pathOfFileToUpload, String gcsPath) throws IOException {
        var storage = StorageOptions.getDefaultInstance()
                                    .getService();

        Path uploadFrom = Paths.get(pathOfFileToUpload);
        String contentType = Files.probeContentType(uploadFrom);
        BlobId blobId = BlobId.fromGsUtilUri(gcsPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                                    .setContentType(contentType)
                                    .build();

        try (WriteChannel writer = storage.writer(blobInfo)) {
            byte[] buffer = new byte[64 * 1024];
            try (InputStream input = Files.newInputStream(uploadFrom)) {
                int limit;
                while ((limit = input.read(buffer)) >= 0) {
                    //System.out.println("more one block");
                    try {
                        writer.write(ByteBuffer.wrap(buffer, 0, limit));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (NoSuchFileException e) {
                System.out.println("The file " + pathOfFileToUpload + "could not be found");
            }
        }
        // put Blob with public access
        Blob blob = storage.get(blobId);
        Acl.Entity aclEnt = Acl.User.ofAllUsers();
        Acl.Role role = Acl.Role.READER;
        Acl acl = Acl.newBuilder(aclEnt, role)
                     .build();
        blob.createAcl(acl);
        System.out.println("Blob access URL: " + "https://storage.googleapis.com/" + blobId.getBucket() + "/" + blobId.getName());
        System.out.println("Blob uploaded with gs path: " + gcsPath);
    }
}