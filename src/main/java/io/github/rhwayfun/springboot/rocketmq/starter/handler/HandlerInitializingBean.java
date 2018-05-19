package io.github.rhwayfun.springboot.rocketmq.starter.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class HandlerInitializingBean {

    /** Logger */
    private static Logger logger = LoggerFactory.getLogger(HandlerInitializingBean.class);

    @Autowired(required = false)
    public void init(List<MessageHandler> messageHandlers) {
        if (CollectionUtils.isEmpty(messageHandlers)) {
            return;
        }
        for (MessageHandler messageHandler : messageHandlers) {
            if (messageHandler.getTopic() == null || "".equals(messageHandler.getTopic())) {
                throw new IllegalArgumentException("topic is empty");
            }
            if (messageHandler.getTags() == null || "".equals(messageHandler.getTags())) {
                throw new IllegalArgumentException("tags is empty");
            }
            String key = messageHandler.getTopic() + messageHandler.getTags();
            HandlerHolder.addHandler(key, messageHandler);
            logger.info("add handler, topic:{}, tags:{}, key:{}", messageHandler.getTopic(), messageHandler.getTags(), key);
        }
    }

}
