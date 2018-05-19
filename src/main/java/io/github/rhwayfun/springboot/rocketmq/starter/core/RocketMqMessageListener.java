package io.github.rhwayfun.springboot.rocketmq.starter.core;

import io.github.rhwayfun.springboot.rocketmq.starter.handler.HandlerHolder;
import io.github.rhwayfun.springboot.rocketmq.starter.handler.MessageHandler;
import io.github.rhwayfun.springboot.rocketmq.starter.serialize.MsgBody;
import io.github.rhwayfun.springboot.rocketmq.starter.serialize.MsgBodyBuilder;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class RocketMqMessageListener implements MessageListenerConcurrently {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqMessageListener.class);
    private static final int DEFAULT_MSG_DELAY = 300000;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        MessageQueue messageQueue = context.getMessageQueue();
        MessageExt msg = msgs.get(0);
        byte[] body = msg.getBody();
        String bodyString = null;
        if (null != body) {
            try {
                bodyString = new String(body, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("can not parse to string with UTF-8", e);
            }
        }
        String key = msg.getKeys();
        String topic = msg.getTopic();
        String tags = msg.getTags();
        LOGGER.info(
                "Receive New Messages: brokerName:{} queueId:{} topic:{} tag:{}  key:{} body:{} ",
                messageQueue.getBrokerName(), messageQueue.getQueueId(), topic, tags, key, bodyString);
        long bornTimestamp = msg.getBornTimestamp();
        long currentTimeMillis = System.currentTimeMillis();
        long timeElapsedFromStoreInMqToReceiveMsg = currentTimeMillis - bornTimestamp;
        ConsumeConcurrentlyStatus consumeConcurrentlyStatus = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        // 300s = 5min 作为一个消费阈值，超过这个值的消息都作为无效消息判断
        if (timeElapsedFromStoreInMqToReceiveMsg >= DEFAULT_MSG_DELAY) {
            LOGGER.warn("msg:{} is invalid, it was born {}s ago", msg, timeElapsedFromStoreInMqToReceiveMsg / 1000);
            return consumeConcurrentlyStatus;
        }
        String handlerKey = topic + tags;
        MessageHandler handler = HandlerHolder.getHandler(handlerKey.toLowerCase());
        if (null != handler) {
            try {
                MsgBody reader = MsgBodyBuilder.build(bodyString);
                consumeConcurrentlyStatus = handler.handle(key, reader);
            } catch (Throwable t) {
                LOGGER.error("mq handler error, msginfo:{}", msg, t);
                throw t;
            }
        }
        return consumeConcurrentlyStatus;
    }

}
