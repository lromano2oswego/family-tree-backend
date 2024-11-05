package com.family_tree.familytree;

import org.springframework.boot.SpringApplication;

public class TestFamilytreeApplication {

	public static void main(String[] args) {
		SpringApplication.from(FamilytreeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
