package org.unibl.etf.youtubetrimmer.trimmer.service;

import com.azure.storage.blob.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.unibl.etf.youtubetrimmer.trimmer.properties.StorageProperties;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Log4j2
public class VideoStorageService implements InitializingBean {

    private final StorageProperties props;
    private BlobContainerClient videoContainer;

    public void upload(Path videoPath, String name)
    {
        BlobClient client = videoContainer.getBlobClient(name);
        log.info("Path:{}; name:{}", videoPath.toString(), name);
        client.uploadFromFile(videoPath.toString());
    }

    @SneakyThrows
    private String readConnectionString()
    {
        return Files.readString(Path.of(props.getConnectionStringPath()));
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
