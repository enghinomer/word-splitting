package com.ionos.domains.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;


@SpringBootTest
class DemoApplicationTests {
	Map<String, Double> wordNetSim = new HashMap<>();
	Map<String, Double> lSim = new HashMap<>();
	Map<String, Double> embeddingSim = new HashMap<>();

	@Test
	void contextLoads() {
		List<String> ngrams = Arrays.asList("grand", "small house");
		System.out.println(String.join(",", ngrams));
	}

	@Test
	void t() {
		String text = "a:2.3 b:2.5 c:3.3";
		String first = text.substring(0, text.indexOf("b:"));
		String second = text.substring(text.indexOf(" ")+1);
		System.out.println(first);
	}

}
