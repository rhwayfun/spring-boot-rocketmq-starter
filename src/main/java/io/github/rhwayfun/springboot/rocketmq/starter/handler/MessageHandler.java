package io.github.rhwayfun.springboot.rocketmq.starter.handler;

import io.github.rhwayfun.springboot.rocketmq.starter.serialize.MsgBody;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public interface MessageHandler {

    /**
     * Subscribe consumer topic.
     *
     * @return topic
     */
    String getTopic();

    /**
     * Subscribe consumer tags.
     *
     * @return tags expression
     */
    String getTags();

    /**
     * How to consumer message.
     *
     * @param key message key
     * @param msgBody wrapped message body
     * @return consumer status
     */
    ConsumeConcurrentlyStatus handle(String key, MsgBody msgBody);

}
