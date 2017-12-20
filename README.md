# Spring Boot RocketMQ Starter
[![Build Status](https://travis-ci.org/rhwayfun/spring-boot-rocketmq-starter.svg?branch=master)](https://travis-ci.org/rhwayfun/spring-boot-rocketmq-starter)
[![Coverage Status](https://coveralls.io/repos/github/rhwayfun/spring-boot-rocketmq-starter/badge.svg?branch=master)](https://coveralls.io/github/rhwayfun/spring-boot-rocketmq-starter?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.rhwayfun/spring-boot-rocketmq-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.rhwayfun/spring-boot-rocketmq-starter/badge.svg)

Open Source Spring Boot Starter for Apache RocketMQ, develop with RocketMQ easily.

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
