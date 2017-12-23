package io.github.rhwayfun.springboot.rocketmq.starter.common;

import io.github.rhwayfun.springboot.rocketmq.starter.constants.RocketMqContent;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public interface RocketMqMessageListener<Content extends RocketMqContent> {

    /**
     * consumer msg.
     *
     * @param content cntent
     * @param msg msg
     * @return consume result
     */
    boolean consumeMsg(Content content, MessageExt msg);

}
