package ru.chaykin.s3.conf;

public class PlayStorageInstance implements IS3StorageInstance {
    @Override
    public String getUrl() {
	return "https://play.min.io";
    }

    @Override
    public String getAccessKey() {
	return "Q3AM3UQ867SPQQA43P2F";
    }

    @Override
    public String getSecretKey() {
	return "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG";
    }
}
