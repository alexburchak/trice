package org.alexburchak.trice.controller;

import org.alexburchak.trice.data.HttpRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertNotNull;

/**
 * @author alexburchak
 */
public class HookControllerTest extends AbstractTestNGSpringWebAppTests {
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testHook() throws Exception {
        String sid = UUID.randomUUID().toString();

        doAnswer(i -> {
            HttpRequest request = (HttpRequest) i.getArguments()[1];
            assertNotNull(request);

            return null;
        }).when(rabbitTemplate).convertAndSend(eq(sid), any(HttpRequest.class));

        mockMvc
                .perform(MockMvcRequestBuilders.get(HookController.PATH_HOOK)
                                .param(TriceController.PARAM_SID, sid)
                )
                .andExpect(status().isOk());

        verify(rabbitTemplate).convertAndSend(eq(sid), any(HttpRequest.class));
    }

    @Test
    public void testHookNoSid() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(HookController.PATH_HOOK))
                .andExpect(status().isBadRequest());
    }
}
