package ru.chaykin.s3;

import java.net.URI;
import java.time.Duration;

import ru.chaykin.s3.conf.IS3StorageInstance;
import ru.chaykin.s3.conf.PlayStorageInstance;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;

import static software.amazon.awssdk.regions.Region.EU_SOUTH_2;
import static software.amazon.awssdk.regions.Region.US_EAST_1;

public class S3ClientFactory {
    private static final IS3StorageInstance STORAGE_INSTANCE = new PlayStorageInstance();

    private static final int MAX_CONCURRENCY = 96;
    private static final long PART_SIZE = 8 * 1024L * 1024L;
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(3);

    private S3ClientFactory() {
    }

    public static S3AsyncClient createClient() {
	return S3AsyncClient.builder()
			.httpClientBuilder(createCrtHttpClient())
			.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).checksumValidationEnabled(false).build())
			//.forcePathStyle(true)
			.endpointOverride(URI.create(STORAGE_INSTANCE.getUrl()))
			.credentialsProvider(createCredentialsProvider())
			.region(US_EAST_1)
			.multipartEnabled(true)
			.multipartConfiguration(b -> b.minimumPartSizeInBytes(PART_SIZE))
			.build();
    }

    private static AwsCrtAsyncHttpClient.Builder createCrtHttpClient() {
	return AwsCrtAsyncHttpClient.builder()
			.connectionTimeout(CONNECTION_TIMEOUT)
			.maxConcurrency(MAX_CONCURRENCY);
    }

    private static AwsCredentialsProvider createCredentialsProvider() {
	AwsCredentials cred = AwsBasicCredentials.create(STORAGE_INSTANCE.getAccessKey(),
			STORAGE_INSTANCE.getSecretKey());
	return StaticCredentialsProvider.create(cred);
    }
}
