package org.unibl.etf.youtubetrimmer.trimmer.service;

import com.azure.storage.blob.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.trimmer.properties.TrimmerProperties;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class VideoStorageService implements InitializingBean {

    private static final String STORACE_ACCOUNT_CONNECTION_STRING_PATH = "/secret/azurestorageconnectionstring";
    private final TrimmerProperties props;
    private BlobContainerClient videoContainer;

    public void upload(Path videoPath, String name)
    {
        BlobClient client = videoContainer.getBlobClient(name);
        client.uploadFromFile(videoPath.toString());
    }

    @SneakyThrows
    private String readConnectionString()
    {
        return Files.readString(Path.of(STORACE_ACCOUNT_CONNECTION_STRING_PATH));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        videoContainer = new BlobContainerClientBuilder()
                .connectionString(readConnectionString())
                .buildClient();
        if(!videoContainer.exists())
            videoContainer.create();;

    }
}
