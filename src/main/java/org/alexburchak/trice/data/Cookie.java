package org.alexburchak.trice.data;

import lombok.Getter;
import lombok.ToString;

/**
 * @author alexburchak
 */
@Getter
@ToString
public class Cookie {
    private String name;
    private String value;
    private String comment;
    private String domain;
    private int maxAge;
    private String path;
    private boolean secure;
    private int version;

    public Cookie(javax.servlet.http.Cookie cookie) {
        name = cookie.getName();
        value = cookie.getValue();
        comment = cookie.getComment();
        domain = cookie.getDomain();
        maxAge = cookie.getMaxAge();
        path = cookie.getPath();
        secure = cookie.getSecure();
        version = cookie.getVersion();
    }
}
