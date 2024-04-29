
package org.example;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;

// ENABLE BELOW IMPORTS FOR TESTING WITH MinIO HTTPS SERVER
// import software.amazon.awssdk.auth.credentials.*;
// import software.amazon.awssdk.http.SdkHttpConfigurationOption;
// import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
// import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
// import software.amazon.awssdk.utils.AttributeMap;
// import software.amazon.awssdk.regions.Region;

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
        S3Configuration config = S3Configuration.builder().chunkedEncodingEnabled(true).build();
        return S3AsyncClient.builder()
                .endpointOverride(URI.create("http://127.0.0.1:9000"))
//                .endpointOverride(URI.create("https://s3.amazonaws.com"))
                .credentialsProvider(ProfileCredentialsProvider.create())
                .serviceConfiguration(config)
                .build();
    }

// ENABLE BELOW CODE FOR TESTING WITH MinIO HTTPS SERVER. ALSO COMMENT ABOVE METHOD `s3Client`
//    public static S3AsyncClient s3Client() {
//            SdkAsyncHttpClient sdkAsyncHttpClient = NettyNioAsyncHttpClient
//                    .builder()
//                    .buildWithDefaults(AttributeMap
//                            .builder()
//                            .put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
//                            .build());
//            return S3AsyncClient.builder()
//                    .endpointOverride(URI.create("https://127.0.0.1:9000"))
//                    .forcePathStyle(true)
//                    .credentialsProvider(ProfileCredentialsProvider.create())
//                    .region(Region.US_EAST_1)
//                    .httpClient(sdkAsyncHttpClient)
//                    .build();
//    }
}
