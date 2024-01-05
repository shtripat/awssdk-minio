package org.example;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Handler {
    private final S3AsyncClient s3Client;

    public Handler() {
        s3Client = DependencyFactory.s3Client();
    }

    public void sendRequest() {
        String bucket = "bucket" + System.currentTimeMillis();
        String mpkey = "mpkey";
        int mB = 1024 * 1024;

        createBucket(s3Client, bucket);

        System.out.println("Starting multi part upload");
        try {
            CreateMultipartUploadRequest req = CreateMultipartUploadRequest
                    .builder()
                    .bucket(bucket)
                    .key(mpkey)
                    .build();
            CompletableFuture<CreateMultipartUploadResponse> res = s3Client.createMultipartUpload(req);
            String uploadId = res.get().uploadId();
            System.out.println("Mpart upload ID: " + uploadId);

            CompletedPart[] arr = new CompletedPart[100];
            for (int i = 0; i < 100; i++) {
                UploadPartRequest req1 = UploadPartRequest
                        .builder()
                        .bucket(bucket)
                        .key(mpkey)
                        .uploadId(uploadId)
                        .partNumber(i+1)
                        .build();
                String etag1 = s3Client.uploadPart(req1, AsyncRequestBody.fromByteBuffer(getRandomByteBuffer(5 * mB))).get().eTag();
                CompletedPart part1 = CompletedPart
                        .builder()
                        .partNumber(i+1)
                        .eTag(etag1)
                        .build();
                arr[0] = part1;
            }
            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder().parts(arr).build();
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    CompleteMultipartUploadRequest.builder()
                            .bucket(bucket).key(mpkey)
                            .uploadId(uploadId)
                            .multipartUpload(completedMultipartUpload)
                            .build();
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done multi part upload");

        cleanUp(s3Client, bucket, mpkey);

        System.out.println("Exiting...");
    }

    private static ByteBuffer getRandomByteBuffer(int size) throws IOException {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }

    public static void createBucket(S3AsyncClient s3Client, String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build());
            System.out.println("Creating bucket: " + bucketName);
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println(bucketName + " is ready.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void cleanUp(S3AsyncClient s3Client, String bucketName, String keyName) {
        System.out.println("Cleaning up...");
        try {
            System.out.println("Deleting object: " + keyName);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println(keyName + " has been deleted.");
            System.out.println("Deleting bucket: " + bucketName);
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(deleteBucketRequest);
            System.out.println(bucketName + " has been deleted.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Cleanup complete");
        System.out.printf("%n");
    }
}
