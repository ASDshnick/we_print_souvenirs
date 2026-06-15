package com.weprintsouvenirs.we_print_souvenirs;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WePrintSouvenirsApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		setSystemPropertyIfPresent(dotenv, "DB_NAME");
		setSystemPropertyIfPresent(dotenv, "DB_USER");
		setSystemPropertyIfPresent(dotenv, "DB_PASS");
		setSystemPropertyIfPresent(dotenv, "DB_PORT");
		setSystemPropertyIfPresent(dotenv, "JWT_SECRET_KEY");
		SpringApplication.run(WePrintSouvenirsApplication.class, args);
	}

	private static void setSystemPropertyIfPresent(Dotenv dotenv, String key) {
		String value = dotenv.get(key);
		if (value != null) {
			System.setProperty(key, value);
		}
	}

}
