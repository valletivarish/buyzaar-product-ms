package com.buyzaar.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {
	@Value("${minio.url}")
	private String url;

	@Value("${minio.secret-key}")
	private String secretKey;

	@Value("${minio.access-key}")
	private String accessKey;

	@Value("${minio.bucket}")
	private String bucket;

	@Bean
	public MinioClient getMinioClient() {
		return new MinioClient.Builder().endpoint(url).credentials(accessKey, secretKey).build();
	}
}
