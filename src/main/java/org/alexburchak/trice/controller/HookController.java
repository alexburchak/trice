package org.alexburchak.trice.controller;

import lombok.extern.slf4j.Slf4j;
import org.alexburchak.trice.data.HttpRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * @author alexburchak
 */
@RestController
@Slf4j
public class HookController {
    public static final String PATH_HOOK = "/hook";

    public static final String PARAM_SID = "sid";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(PATH_HOOK)
    public void hook(@RequestParam(PARAM_SID) String sid, HttpServletRequest request) throws URISyntaxException {
        log.debug("Received {} request for sid {}", PATH_HOOK, sid);

        HttpRequest httpRequest = new HttpRequest(request);

        log.debug("Created HTTP request {}", httpRequest);

        rabbitTemplate.convertAndSend(sid, httpRequest);
    }
}
