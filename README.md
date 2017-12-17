# Spring Boot RocketMQ Starter
Open Source Spring Boot Starter for Apache RocketMQ, make it easier to develop with RocketMQ.

# Quick Start

1. Maven Dependency

```xml

    <dependency>
        <groupId>io.github.rhwayfun</groupId>
        <artifactId>spring-boot-rocketmq-starter</artifactId>
        <version>0.0.1</version>
    </dependency>

```

2. Consumer

继承自`AbstractRocketMqConsumer`

使用示例：

```java
@Component
public class DemoRocketMqConsumerExample
        extends AbstractRocketMqConsumer<DemoRocketMqTopic, DemoRocketMqTag, DemoRocketMqContent> {

    @Override
    public Map<String, Set<String>> subscribeTopicTags() {
        Map<String, Set<String>> topicSetMap = new HashMap<>();
        Set<String> tagSet = new HashSet<>();
        tagSet.add("TagA");
        tagSet.add("TagB");
        topicSetMap.put("TopicA", tagSet);
        return topicSetMap;
    }

    @Override
    public boolean handle(String topic, String tag, DemoRocketMqContent content, MessageExt msg) {
        logger.info("receive msg[{}], topic:{}, tag:{}, content:{}", msg, topic, tag, content);
        return true;
    }

}
```

3. Producer
待补充。。。
