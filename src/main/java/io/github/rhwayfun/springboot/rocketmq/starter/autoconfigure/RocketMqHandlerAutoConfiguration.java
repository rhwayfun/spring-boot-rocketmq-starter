package io.github.rhwayfun.springboot.rocketmq.starter.autoconfigure;

import io.github.rhwayfun.springboot.rocketmq.starter.core.RocketMqMessageListener;
import io.github.rhwayfun.springboot.rocketmq.starter.handler.HandlerInitializingBean;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
@Configuration
@AutoConfigureBefore(RocketMqAutoConfiguration.class)
@ConditionalOnClass(RocketMqMessageListener.class)
public class RocketMqHandlerAutoConfiguration {

    @Bean
    public HandlerInitializingBean hanlderInitializingBean() {
        return new HandlerInitializingBean();
    }

    @Bean
    @ConditionalOnMissingBean(MessageListener.class)
    public RocketMqMessageListener rocketMqMessageListener() {
        return new RocketMqMessageListener();
    }

}
