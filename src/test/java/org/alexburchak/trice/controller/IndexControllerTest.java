package org.alexburchak.trice.controller;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author alexburchak
 */
public class IndexControllerTest extends AbstractTestNGSpringWebAppTests {
    @Test
    public void testIndex() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(IndexController.PATH_INDEX))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(IndexController.MODEL_BASE_URL))
                .andExpect(view().name(IndexController.RESULT_SUCCESS));
    }
}
