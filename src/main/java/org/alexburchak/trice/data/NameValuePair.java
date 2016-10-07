package org.alexburchak.trice.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author alexburchak
 */
@AllArgsConstructor
@Getter
@ToString
public class NameValuePair {
    private String name;
    private String value;
}
