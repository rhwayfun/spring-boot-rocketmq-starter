package io.github.rhwayfun.springboot.rocketmq.starter.common;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class DefaultRocketMqProducer{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DefaultMQProducer producer;

    /**
     * send msg.
     *
     * @param msg content
     * @return send result
     */
    public boolean sendMsg(Message msg) {
        SendResult sendResult = null;
        try {
            sendResult = producer.send(msg);
        } catch (Exception e) {
            logger.error("send msg error", e);
        }
        return sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK;
    }

    /**
     * sene one way msg.
     *
     * @param msg msg
     */
    public void sendOneWayMsg(Message msg) {
        try {
            producer.sendOneway(msg);
        } catch (Exception e) {
            logger.error("send msg error", e);
        }
    }

    /**
     * send delay msg.
     *
     * @param msg content
     * @param delayLevel 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * @return send result
     */
    public boolean sendDelayMsg(String topic, String tag, Message msg, int delayLevel) {
        msg.setDelayTimeLevel(delayLevel);
        SendResult sendResult = null;
        try {
            sendResult = producer.send(msg);
        } catch (Exception e) {
            logger.error("send msg error", e);
        }
        return sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK;
    }

    public DefaultMQProducer getProducer() {
        return producer;
    }

    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }

    public void destroy() {
        if (Objects.nonNull(producer)) {
            producer.shutdown();
        }
    }
}
