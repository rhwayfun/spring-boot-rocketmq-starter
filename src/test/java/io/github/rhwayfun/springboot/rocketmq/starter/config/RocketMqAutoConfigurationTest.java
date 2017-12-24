package io.github.rhwayfun.springboot.rocketmq.starter.config;

import io.github.rhwayfun.springboot.rocketmq.starter.common.AbstractRocketMqConsumer;
import io.github.rhwayfun.springboot.rocketmq.starter.common.DefaultRocketMqProducer;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.ConsumeMode;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.RocketMqContent;
import io.github.rhwayfun.springboot.rocketmq.starter.constants.RocketMqTopic;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class RocketMqAutoConfigurationTest {

    private static final String TEST_NAME_SERVER = "127.0.0.1:9876";

    private static final String TEST_PRODUCER_GROUP = "test-producer-group";

    private static final String TEST_CONSUMER_GROUP = "test-consumer-group";

    private static final String TEST_TOPIC = "test-topic";

    private static final String TEST_TAG = "test-tag";

    private AnnotationConfigApplicationContext context;

    @Test
    public void defaultRocketMqProducer() {

        load("spring.rocketmq.nameServer=" + TEST_NAME_SERVER,
                "spring.rocketmq.producer-group-name=" + TEST_PRODUCER_GROUP);

        Assert.assertTrue(this.context.containsBean("defaultRocketMqProducer"));
        Assert.assertTrue(this.context.containsBean("mqProducer"));

        DefaultRocketMqProducer defaultRocketMqProducer = this.context.getBean(DefaultRocketMqProducer.class);
        DefaultMQProducer defaultMQProducer = defaultRocketMqProducer.getProducer();

        Assert.assertEquals(defaultMQProducer.getNamesrvAddr(), TEST_NAME_SERVER);
        Assert.assertEquals(defaultMQProducer.getProducerGroup(), TEST_PRODUCER_GROUP);
    }

    @Test
    public void demoMqConsumer() {
        load(false, "spring.rocketmq.nameServer=" + TEST_NAME_SERVER);

        BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(DemoMqConsumer.class);
        this.context.registerBeanDefinition("demoMqConsumer", beanBuilder.getBeanDefinition());
        this.context.refresh();

        DemoMqConsumer demoMqConsumer = (DemoMqConsumer) this.context.getBean("demoMqConsumer");

        Assert.assertEquals(demoMqConsumer.getConsumeMode(), ConsumeMode.CONCURRENTLY);
        Assert.assertEquals(demoMqConsumer.getConsumerGroup(), TEST_CONSUMER_GROUP);
        Assert.assertEquals(demoMqConsumer.getConsumer().getNamesrvAddr(), TEST_NAME_SERVER);
        Assert.assertEquals(demoMqConsumer.getMessageModel(), MessageModel.CLUSTERING);
        Assert.assertEquals(demoMqConsumer.getConsumeFromWhere(), ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        Assert.assertEquals(demoMqConsumer.getSuspendCurrentQueueTimeMillis(), -1);
        Assert.assertEquals(demoMqConsumer.getDelayLevelWhenNextConsume(), 0);

        Assert.assertNull(demoMqConsumer.getConsumeThreadMin());
        Assert.assertNull(demoMqConsumer.getConsumeThreadMax());
        demoMqConsumer.setConsumeThreadMin(1);
        demoMqConsumer.setConsumeThreadMax(10);
        Assert.assertNotNull(demoMqConsumer.getConsumeThreadMin());
        Assert.assertNotNull(demoMqConsumer.getConsumeThreadMax());

        Assert.assertTrue(demoMqConsumer.isStarted());
    }

    @After
    public void closeContext() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Component
    private static class DemoMqConsumer extends AbstractRocketMqConsumer<DemoMqTopic, DemoMqContent> {

        @Override
        public boolean consumeMsg(DemoMqContent content, MessageExt msg) {
            System.out.println(new Date() + ", id: " + content.getId() + ",desc: " + content.getDesc());
            System.out.println(content.toString());
            return true;
        }

        @Override
        public Map<String, Set<String>> subscribeTopicTags() {
            Map<String, Set<String>> map = new HashMap<>();
            Set<String> tags = new HashSet<>();
            tags.add(TEST_TAG);
            map.put(TEST_TOPIC, tags);
            return map;
        }

        @Override
        public String getConsumerGroup() {
            return TEST_CONSUMER_GROUP;
        }
    }

    private static class DemoMqTopic implements RocketMqTopic{

        @Override
        public String getTopic() {
            return TEST_TOPIC;
        }
    }

    private static class DemoMqContent extends RocketMqContent {
        private int id;
        private String desc;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    private void load(boolean refresh, String... environment) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(RocketMqAutoConfiguration.class);
        EnvironmentTestUtils.addEnvironment(ctx, environment);
        if (refresh) {
            ctx.refresh();
        }
        this.context = ctx;
    }

    private void load(String... environment) {
        load(true, environment);
    }

}