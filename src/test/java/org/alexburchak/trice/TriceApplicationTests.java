package org.alexburchak.trice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.Test;

@SpringBootTest(classes = TriceApplication.class)
@WebAppConfiguration
public class TriceApplicationTests extends AbstractTestNGSpringContextTests {
	@Test
	public void contextLoads() {
	}
}
