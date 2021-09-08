package org.unibl.etf.youtubetrimmer.trimmer.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.trimmer.properties.TrimmerProperties;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class VideoStorageService {

    private static final String STORACE_ACCOUNT_CONNECTION_STRING_PATH = "/secret/azurestorageconnectionstring";
    private final TrimmerProperties props;

    public void upload(Path videoPath, String blobName)
    {
        BlobServiceClient blobClient = new BlobServiceClientBuilder()
                .connectionString(readConnectionString()).buildClient();

        BlobContainerClient videoContainer = blobClient.getBlobContainerClient(props.getVideoContainerName());

        if(!videoContainer.exists())
            videoContainer.create();

        BlobClient client = videoContainer.getBlobClient(blobName);
        client.uploadFromFile(videoPath.toString());
    }

    @SneakyThrows
    private String readConnectionString()
    {
        return Files.readString(Path.of(STORACE_ACCOUNT_CONNECTION_STRING_PATH));
    }
}
