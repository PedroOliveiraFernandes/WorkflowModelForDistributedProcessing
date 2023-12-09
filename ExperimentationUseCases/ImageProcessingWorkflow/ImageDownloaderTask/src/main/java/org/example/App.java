package org.example;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import org.example.workflowEssentials.ArgumentsModel;
import org.example.workflowEssentials.Utils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {"arguments":[],"constants":["output_%.jpg"],"currentIteration":0}
 */
public class App {

    public static void main(String[] args) throws IOException {
        System.out.println("downloader");
        System.out.println(args[0]);
        var arguments = ArgumentsModel.fromArgs(args);

        var gcsPathToDownload = arguments.arguments[0];

        var outputFileRelativePath = arguments.constants[0].replace("%", "" + arguments.currentIteration);
        var outputFileAbsolutePath = Utils.PATH_TO_WORK_DIRECTORY + outputFileRelativePath;

        downloadBlob(gcsPathToDownload, outputFileAbsolutePath);
    }

    private static void downloadBlob(String gcsPath, String locaStoragePath) throws IOException {
        var storage = StorageOptions.getDefaultInstance()
                                    .getService();

        Path pathToDownloadTo = Paths.get(locaStoragePath);
        System.out.println("download to: " + pathToDownloadTo);
        BlobId blobId = BlobId.fromGsUtilUri(gcsPath);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            System.out.println("No such Blob exists !");
            return;
        }
        Files.createDirectories(pathToDownloadTo.getParent());
        PrintStream writeTo = new PrintStream(Files.newOutputStream(pathToDownloadTo));
        try (ReadChannel reader = blob.reader()) {
            WritableByteChannel channel = Channels.newChannel(writeTo);
            ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
            while (reader.read(bytes) > 0) {
                bytes.flip();
                channel.write(bytes);
                bytes.clear();
            }
        } catch (NoSuchFileException e) {
            System.out.println("The file " + pathToDownloadTo + " could not be found");
        }
        writeTo.close();
        System.out.println("Blob downloaded");
    }
}
