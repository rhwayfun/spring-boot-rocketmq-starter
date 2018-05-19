package io.github.rhwayfun.springboot.rocketmq.starter.serialize;

import io.github.rhwayfun.springboot.rocketmq.starter.serialize.json.JsonSerialzation;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class MsgBodyBuilder {

    public static MsgBody build(String body) {
        return new JsonSerialzation(body);
    }

}
