package org.alexburchak.trice.controller;

import lombok.extern.slf4j.Slf4j;
import org.alexburchak.trice.config.TriceConfiguration;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author alexburchak
 */
@Controller
@Slf4j
public class TriceController {
    public static final String PATH_TRICE = "/trice";

    public static final String PARAM_SID = "sid";

    static final String MODEL_ENDPOINT = "endpoint";
    static final String MODEL_SID = "sid";

    static final String RESULT_SUCCESS = "trice";
    static final String RESULT_ERROR = "error";

    private static final String HEADER_X_EXPIRES = "x-expires";

    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private TriceConfiguration triceConfiguration;
    @Autowired
    private TopicExchange exchange;

    @RequestMapping(PATH_TRICE)
    public String trice(@RequestParam(PARAM_SID) String sid, Model model) {
        log.debug("Received {} request for sid {}", PATH_TRICE, sid);

        Map<String, Object> args = new HashMap<>();
        args.put(HEADER_X_EXPIRES, triceConfiguration.getQueueExpires());

        String queueName = triceConfiguration.getQueueNamePrefix() + sid;

        Queue queue = new Queue(queueName, true, false, false, args);

        log.debug("Created queue name {}", queueName);

        if (amqpAdmin.declareQueue(queue) == null) {
            log.error("Queue {} could not be declared", queueName);

            return RESULT_ERROR;
        }

        Binding binding = BindingBuilder.bind(queue)
                .to(exchange)
                .with(sid);
        amqpAdmin.declareBinding(binding);

        log.debug("Bound queue {} to exchange {} by SID {}", queueName, exchange.getName(), sid);

        model.addAttribute(MODEL_ENDPOINT, triceConfiguration.getEndpoint());
        model.addAttribute(MODEL_SID, sid);

        return RESULT_SUCCESS;
    }
}
