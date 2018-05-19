package io.github.rhwayfun.springboot.rocketmq.starter.core;

import java.util.List;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class RocketMqConsumerMBean{

    /**
     * consumer beans container.
     *
     */
    private List<AbstractRocketMqConsumer> consumers;

    public List<AbstractRocketMqConsumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<AbstractRocketMqConsumer> consumers) {
        this.consumers = consumers;
    }

}
