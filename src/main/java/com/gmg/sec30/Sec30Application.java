package com.gmg.sec30;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Sec30Application {

	public static void main(String[] args) {
		// .env 파일 로드
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory(".")
					.ignoreIfMissing()
					.load();

			// 환경변수를 시스템 프로퍼티로 설정
			dotenv.entries().forEach(entry -> {
				String key = entry.getKey();
				String value = entry.getValue();
				// 원본 키 그대로 세팅 (예: SPOTIFY_CLIENT_ID)
				System.setProperty(key, value);
				// 언더스코어 -> 점, 소문자 변환(예: spotify.client.id)도 함께 세팅
				String dotted = key.toLowerCase().replace('_', '.');
				System.setProperty(dotted, value);
			});
		} catch (Exception e) {
			System.out.println("Warning: Failed to load .env file - " + e.getMessage());
		}

		SpringApplication.run(Sec30Application.class, args);
	}

}
