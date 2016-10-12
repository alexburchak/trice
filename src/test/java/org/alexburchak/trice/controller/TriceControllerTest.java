package org.alexburchak.trice.controller;

import org.alexburchak.trice.config.RabbitConfiguration;
import org.alexburchak.trice.config.TriceConfiguration;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertEquals;

/**
 * @author alexburchak
 */
public class TriceControllerTest extends AbstractTestNGSpringWebAppTests {
    @Autowired
    private TriceConfiguration triceConfiguration;
    @Autowired
    private RabbitConfiguration rabbitConfiguration;
    @MockBean
    private AmqpAdmin amqpAdmin;

    @Test
    public void testTrice() throws Exception {
        String sid = UUID.randomUUID().toString();

        String queueName = triceConfiguration.getQueueNamePrefix() + sid;

        doAnswer(i -> {
            Queue queue = ((Queue) i.getArguments()[0]);

            assertEquals(queue.getName(), queueName);

            return queue.getName();
        }).when(amqpAdmin).declareQueue(any(Queue.class));

        doAnswer(i -> {
            Binding binding = ((Binding) i.getArguments()[0]);

            assertEquals(binding.getExchange(), rabbitConfiguration.getExchangeName());
            assertEquals(binding.getRoutingKey(), sid);

            return null;
        }).when(amqpAdmin).declareBinding(any(Binding.class));

        mockMvc
                .perform(MockMvcRequestBuilders.get(TriceController.PATH_TRICE)
                                .param(TriceController.PARAM_SID, sid)
                )
                .andExpect(model().attribute(TriceController.MODEL_ENDPOINT, triceConfiguration.getEndpoint()))
                .andExpect(model().attribute(TriceController.MODEL_SID, sid))
                .andExpect(status().isOk())
                .andExpect(view().name(TriceController.RESULT_SUCCESS));

        verify(amqpAdmin).declareQueue(any(Queue.class));
        verify(amqpAdmin).declareBinding(any(Binding.class));

        verifyNoMoreInteractions(amqpAdmin);
    }

    @Test
    public void testTriceNoSid() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(TriceController.PATH_TRICE))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(amqpAdmin);
    }
}
