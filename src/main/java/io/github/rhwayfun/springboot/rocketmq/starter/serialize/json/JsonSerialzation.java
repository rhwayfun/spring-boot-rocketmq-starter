package io.github.rhwayfun.springboot.rocketmq.starter.serialize.json;

import com.alibaba.fastjson.JSON;
import io.github.rhwayfun.springboot.rocketmq.starter.serialize.MsgBody;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class JsonSerialzation implements MsgBody {

    private final String body;

    public JsonSerialzation(String body) {
        this.body = body;
    }

    @Override
    public <T> T getBody(Class<T> clazz) {
        if (null == body || "".equals(body.trim())) {
            return null;
        }
        return JSON.parseObject(body, clazz);
    }
}
