package ru.chaykin.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.chaykin.io.RandomInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class S3UploadTest {
    private static final Logger LOG = LogManager.getLogger();

    private static final String BUCKET = "ngcp";

    @BeforeEach
    public void init() {
	try (var client = S3ClientFactory.createClient()) {
	    if (client.listBuckets().join().buckets().stream().noneMatch(b -> b.name().equals(BUCKET))) {
		client.createBucket(b -> b.bucket(BUCKET)).join();
	    }
	}
    }

    @Test
    void threadStuckTest() throws IOException {
	try (var client = S3ClientFactory.createClient()) {
	    var body = AsyncRequestBody.forBlockingInputStream(null);
	    var request = createRequest();
	    var future = client.putObject(request, body);

	    ForkJoinPool.commonPool().execute(() -> {
		try {
		    Thread.sleep(45000L);
		} catch (InterruptedException e) {
		    throw new RuntimeException(e);
		}

		LOG.info("Cancelling future {}...", future);
		future.completeExceptionally(new RuntimeException("CANCEL!"));
	    });

	    try (InputStream in = new RandomInputStream()) {
		body.writeInputStream(in);
	    }

	    var resp = future.join();
	    Assertions.assertNotNull(resp);
	    LOG.debug("Upload finish: {}", resp);
	}
    }

    private PutObjectRequest createRequest() {
	String key = UUID.randomUUID().toString();
	return PutObjectRequest.builder().bucket(BUCKET).key(key).build();
    }
}
