package org.alexburchak.trice.data;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author alexburchak
 */
@Getter
@ToString
public class HttpRequest {
    private String url;
    private String method;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String remoteUser;
    private List<NameValuePair> parameters;
    private List<NameValuePair> headers;
    private List<Cookie> cookies;

    public HttpRequest(HttpServletRequest request) {
        url = request.getRequestURI();
        method = request.getMethod();
        remoteAddr = request.getRemoteAddr();
        remoteHost = request.getRemoteHost();
        remotePort = request.getRemotePort();
        remoteUser = request.getRemoteUser();

        parameters = request.getParameterMap().entrySet().stream()
                .flatMap(e -> Arrays.stream(e.getValue())
                                .map(v -> new NameValuePair(e.getKey(), v))
                )
                .collect(Collectors.toList());

        headers = StreamSupport.stream(Spliterators.spliteratorUnknownSize(CollectionUtils.toIterator(request.getHeaderNames()), Spliterator.IMMUTABLE), false)
                .flatMap(n -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(CollectionUtils.toIterator(request.getHeaders(n)), Spliterator.IMMUTABLE), false)
                                .map(v -> new NameValuePair(n, v))
                )
                .collect(Collectors.toList());

        if (request.getCookies() != null) {
            cookies = Arrays.stream(request.getCookies())
                    .map(Cookie::new)
                    .collect(Collectors.toList());
        }
    }
}
