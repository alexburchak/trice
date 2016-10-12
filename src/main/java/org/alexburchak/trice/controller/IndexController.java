package org.alexburchak.trice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author alexburchak
 */
@Controller
@Slf4j
public class IndexController {
    public static final String PATH_INDEX = "/";

    static final String MODEL_BASE_URL = "baseUrl";

    static final String RESULT_SUCCESS = "index";

    @RequestMapping(PATH_INDEX)
    public String index(HttpServletRequest request, Model model) throws MalformedURLException {
        log.debug("Received {} request", PATH_INDEX);

        URL url = new URL(request.getRequestURL().toString());

        model.addAttribute(MODEL_BASE_URL, new URL(url.getProtocol(), url.getHost(), url.getPort(), HookController.PATH_HOOK).toString());

        return RESULT_SUCCESS;
    }
}
