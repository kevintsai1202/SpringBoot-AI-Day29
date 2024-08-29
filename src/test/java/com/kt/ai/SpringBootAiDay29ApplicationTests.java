package com.kt.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kt.ai.service.EtlService;

@SpringBootTest
class SpringBootAiDay29ApplicationTests {

	@Autowired
	EtlService etlService;
	
	@Test
	void contextLoads() {
		etlService.loadJsonAsDocuments();
	}

}
