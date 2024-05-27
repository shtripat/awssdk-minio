
package org.example;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import java.net.URI;

/**
 * The module containing all dependencies required by the {@link Handler}.
 */
public class DependencyFactory {

    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */
    public static S3AsyncClient s3Client() {
        /*S3Configuration config = S3Configuration.builder().chunkedEncodingEnabled(true).build();
        return S3AsyncClient.builder()
//                .endpointOverride(URI.create("http://127.0.0.1:9000"))
                .endpointOverride(URI.create("https://s3.amazonaws.com"))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .serviceConfiguration(config)
                .build();*/
        return S3AsyncClient.crtBuilder()
                .endpointOverride(URI.create("http://127.0.0.1:9000"))
                //.credentialsProvider(DefaultCredentialsProvider.create())
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .targetThroughputInGbps(1.0)
                .minimumPartSizeInBytes(Long.valueOf(2 * 1024 * 1024))
                .build();
    }
}
