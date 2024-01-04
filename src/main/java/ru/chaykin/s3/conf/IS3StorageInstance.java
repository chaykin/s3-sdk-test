package ru.chaykin.s3.conf;

public interface IS3StorageInstance {
    String getUrl();

    String getAccessKey();

    String getSecretKey();
}
