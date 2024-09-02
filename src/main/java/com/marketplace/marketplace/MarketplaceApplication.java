package com.marketplace.marketplace;

import com.marketplace.marketplace.image.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class MarketplaceApplication {
	private final ImageStorageService imageStorageService;

	public static void main(String[] args) {
		SpringApplication.run(MarketplaceApplication.class, args);
	}
}
