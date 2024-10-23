package com.family_tree.familytree;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FamilytreeApplicationTests {

	@Test
	void contextLoads() {
	}

}
