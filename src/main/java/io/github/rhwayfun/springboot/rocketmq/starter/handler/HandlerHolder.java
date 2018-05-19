package io.github.rhwayfun.springboot.rocketmq.starter.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class HandlerHolder {

    private static Map<String, MessageHandler> cacheMessageHandler = new HashMap<>();

    public static MessageHandler getHandler(String key) {
        return cacheMessageHandler.get(key);
    }

    public static void addHandler(String key, MessageHandler handler) {
        cacheMessageHandler.put(key, handler);
    }
}
