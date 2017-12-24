package io.github.rhwayfun.springboot.rocketmq.starter.common;

import com.alibaba.fastjson.JSON;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.ConsumeMode;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.RocketMqContent;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.RocketMqTopic;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract message listener.
 * For consume message, you should inherit this class to implement three methods.
 * @author rhwayfun
 * @since 0.0.1
 */
public abstract class AbstractRocketMqConsumer<Topic extends RocketMqTopic, Content extends RocketMqContent> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Class<Topic> topicClazz;

    protected Class<Content> contentClazz;

    /**
     * is started.
     *
     */
    private boolean isStarted;

    /**
     * min consume thread.
     *
     */
    private Integer consumeThreadMin;

    /**
     * max consume thread.
     *
     */
    private Integer consumeThreadMax;

    /**
     * consume from where, default is
     * @see ConsumeFromWhere
     * @value ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET
     *
     */
    private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;

    /**
     * Message consume retry strategy<br>
     * -1, no retry,put into DLQ directly<br>
     * 0, broker control retry frequency<br>
     * >0, client control retry frequency
     *
     */
    private int delayLevelWhenNextConsume = 0;

    private long suspendCurrentQueueTimeMillis = -1;

    /**
     * consume message mode, default is CLUSTERING
     * @see MessageModel
     * @value MessageModel.CLUSTERING
     *
     */
    private MessageModel messageModel = MessageModel.CLUSTERING;

    /**
     * consume mode, default is CONCURRENTLY
     * @see ConsumeMode
     * @value ConsumeMode.CONCURRENTLY
     *
     */
    private ConsumeMode consumeMode = ConsumeMode.CONCURRENTLY;

    /**
     * consumer holder.
     *
     */
    private DefaultMQPushConsumer consumer;

    /**
     * subscribeTopicTags for specific consumer.
     *
     * @return subscribeTopicTags
     */
    public abstract Map<String, Set<String>> subscribeTopicTags();

    /**
     * specific consumer group.
     *
     * @return consumer group
     */
    public abstract String getConsumerGroup();

    /**
     * consumer msg.
     *
     * @param content cntent
     * @param msg msg
     * @return consume result
     */
    public abstract boolean consumeMsg(Content content, MessageExt msg);

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() throws MQClientException {
        Class<? extends AbstractRocketMqConsumer> parentClazz = this.getClass();
        // 得到泛型父类
        Type genType = parentClazz.getGenericSuperclass();
        // 一个泛型类可能有多个泛型形参，比如ClassName<T,K> 这里有两个泛型形参T和K，Class Name<T> 这里只有1个泛型形参T
        Type[] types = ((ParameterizedType) genType).getActualTypeArguments();
        topicClazz = (Class<Topic>) types[0];
        contentClazz = (Class<Content>) types[1];

        if (this.isStarted()) {
            throw new IllegalStateException("container already started. " + this.toString());
        }

        initRocketMQPushConsumer();
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (Objects.nonNull(consumer)) {
            consumer.shutdown();
        }
        logger.info("consumer shutdown, {}", this.toString());
    }

    public class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {

        @Override
        @SuppressWarnings("unchecked")
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt messageExt : msgs) {
                try {
                    long now = System.currentTimeMillis();
                    consumeMsg(parseMsg(messageExt.getBody(), contentClazz), messageExt);
                    long costTime = System.currentTimeMillis() - now;
                    logger.info("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (Exception e) {
                    logger.warn("consume message failed. messageExt:{}", messageExt, e);
                    context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    public class DefaultMessageListenerOrderly implements MessageListenerOrderly {

        @Override
        @SuppressWarnings("unchecked")
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            for (MessageExt messageExt : msgs) {
                try {
                    long now = System.currentTimeMillis();
                    consumeMsg(parseMsg(messageExt.getBody(), contentClazz), messageExt);
                    long costTime = System.currentTimeMillis() - now;
                    logger.debug("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (Exception e) {
                    logger.warn("consume message failed. messageExt:{}", messageExt, e);
                    context.setSuspendCurrentQueueTimeMillis(suspendCurrentQueueTimeMillis);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }

            return ConsumeOrderlyStatus.SUCCESS;
        }
    }

    private void initRocketMQPushConsumer() throws MQClientException {

        Assert.notNull(getConsumerGroup(), "Property 'consumerGroup' is required");
        Assert.notEmpty(subscribeTopicTags(), "subscribeTopicTags method can't be empty");

        consumer = new DefaultMQPushConsumer(getConsumerGroup());
        if (consumeThreadMax != null) {
            consumer.setConsumeThreadMax(consumeThreadMax);
        }
        if (consumeThreadMax != null && consumeThreadMax < consumer.getConsumeThreadMin()) {
            consumer.setConsumeThreadMin(consumeThreadMax);
        }

        consumer.setConsumeFromWhere(consumeFromWhere);
        consumer.setMessageModel(messageModel);

        switch (consumeMode) {
            case Orderly:
                consumer.setMessageListener(new DefaultMessageListenerOrderly());
                break;
            case CONCURRENTLY:
                consumer.setMessageListener(new DefaultMessageListenerConcurrently());
                break;
            default:
                throw new IllegalArgumentException("Property 'consumeMode' was wrong.");
        }

    }

    private <T> T parseMsg(byte[] body, Class<? extends RocketMqContent> clazz){
        T t = null;
        if (body != null) {
            try {
                t = JSON.parseObject(body, clazz);
            } catch (Exception e) {
                logger.error("can not parse to object", e);
            }
        }
        return t;
    }

    public Integer getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public void setConsumeThreadMin(Integer consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public Integer getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(Integer consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public ConsumeFromWhere getConsumeFromWhere() {
        return consumeFromWhere;
    }

    public void setConsumeFromWhere(ConsumeFromWhere consumeFromWhere) {
        this.consumeFromWhere = consumeFromWhere;
    }

    public int getDelayLevelWhenNextConsume() {
        return delayLevelWhenNextConsume;
    }

    public void setDelayLevelWhenNextConsume(int delayLevelWhenNextConsume) {
        this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
    }

    public long getSuspendCurrentQueueTimeMillis() {
        return suspendCurrentQueueTimeMillis;
    }

    public void setSuspendCurrentQueueTimeMillis(long suspendCurrentQueueTimeMillis) {
        this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public ConsumeMode getConsumeMode() {
        return consumeMode;
    }

    public void setConsumeMode(ConsumeMode consumeMode) {
        this.consumeMode = consumeMode;
    }

    public DefaultMQPushConsumer getConsumer() {
        return consumer;
    }
}
