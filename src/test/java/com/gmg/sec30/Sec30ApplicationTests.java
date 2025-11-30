package com.gmg.sec30;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Spring Boot 애플리케이션 컨텍스트 로딩 테스트")
class Sec30ApplicationTests {

	@Test
	@DisplayName("애플리케이션 컨텍스트가 정상적으로 로드되는지 테스트")
	void contextLoads() {
		// 애플리케이션 컨텍스트가 성공적으로 로드되면 테스트 통과
	}

}

