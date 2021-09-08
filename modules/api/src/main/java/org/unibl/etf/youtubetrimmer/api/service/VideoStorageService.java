package org.unibl.etf.youtubetrimmer.api.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.unibl.etf.youtubetrimmer.api.properties.StorageProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Service
@RequiredArgsConstructor
public class VideoStorageService implements InitializingBean {


    private final StorageProperties props;
    private final TimeService timeService;
    private BlobContainerClient videoContainer;

    public String getVideoDownloadUrl(String name)
    {
        BlobClient blob = videoContainer.getBlobClient(name);
        String sasQueryParams = generateSasQueryParameters(blob);
        return UriComponentsBuilder
                .fromHttpUrl(props.getDownloadUrlBase())
                .query(sasQueryParams)
                .build()
                .toString();
    }

    private String generateSasQueryParameters(BlobClient blob)
    {
        Instant now = timeService.now();
        OffsetDateTime startTime = OffsetDateTime
                .ofInstant(now, ZoneOffset.UTC)
                .minusMinutes(5);
        OffsetDateTime expiryTime = OffsetDateTime
                .ofInstant(now, ZoneOffset.UTC)
                .plusDays(props.getSasTokenExpiryDurationDays());

        BlobSasPermission readPermission = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues builder =
                new BlobServiceSasSignatureValues(expiryTime, readPermission)
                        .setProtocol(SasProtocol.HTTPS_ONLY)
                        .setStartTime(startTime);
        return blob.generateSas(builder);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String connectionString = Files.readString(Path.of(props.getConnectionStringPath()));
        videoContainer = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(props.getVideoContainerName())
                .buildClient();

        if(!videoContainer.exists())
            videoContainer.create();
    }
}
