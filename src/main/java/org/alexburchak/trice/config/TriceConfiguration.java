package org.alexburchak.trice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author alexburchak
 */
@Configuration
@Getter
public class TriceConfiguration extends WebMvcConfigurerAdapter {
    @Value("${trice.messaging.endpoint}")
    private String endpoint;
    @Value("${trice.messaging.queue-expires}")
    private long queueExpires;
    @Value("${trice.messaging.queue-name-prefix}")
    private String queueNamePrefix;
}
