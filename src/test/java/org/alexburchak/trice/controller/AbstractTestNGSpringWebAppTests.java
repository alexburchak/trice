package org.alexburchak.trice.controller;

import org.alexburchak.trice.TriceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;

/**
 * @author alexburchak
 */
@SpringBootTest(classes = TriceApplication.class)
@WebAppConfiguration
@TestExecutionListeners(inheritListeners = false, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class AbstractTestNGSpringWebAppTests extends AbstractTestNGSpringContextTests {
    @Autowired
    protected WebApplicationContext webContext;

    protected MockMvc mockMvc;

    @BeforeMethod
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webContext)
                .build();
    }
}
