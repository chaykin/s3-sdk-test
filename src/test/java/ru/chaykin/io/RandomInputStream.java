package ru.chaykin.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class RandomInputStream extends InputStream {
    private static final long THRESHOLD = 1024L * 1024L * 1024L;

    private final Random rnd = new Random();

    private boolean closed;
    private long readBytes;

    @Override
    public int read() throws IOException {
	checkOpen();

	if (readBytes++ <= THRESHOLD) {
	    return rnd.nextInt(256);
	}

	return -1;
    }

    @Override
    public void close() {
	closed = true;
    }

    private void checkOpen() throws IOException {
	if (closed) {
	    throw new IOException("Stream closed");
	}
    }
}
