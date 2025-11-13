package com.gmg.sec30;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Sec30Application {

	public static void main(String[] args) {
		// .env 파일 로드
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory(".")
					.ignoreIfMissing()
					.load();

			// 환경변수를 시스템 프로퍼티로 설정
			dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
			);
		} catch (Exception e) {
			System.out.println("Warning: Failed to load .env file - " + e.getMessage());
		}

		SpringApplication.run(Sec30Application.class, args);
	}

}
