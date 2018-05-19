# Spring Boot RocketMQ Starter
[![Build Status](https://travis-ci.org/rhwayfun/spring-boot-rocketmq-starter.svg?branch=master)](https://travis-ci.org/rhwayfun/spring-boot-rocketmq-starter)
[![Coverage Status](https://coveralls.io/repos/github/rhwayfun/spring-boot-rocketmq-starter/badge.svg?branch=master)](https://coveralls.io/github/rhwayfun/spring-boot-rocketmq-starter?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.rhwayfun/spring-boot-rocketmq-starter/badge.svg)](http://search.maven.org/#artifactdetails%7Cio.github.rhwayfun%7Cspring-boot-rocketmq-starter%7C0.0.4%7Cjar)
[![License](https://img.shields.io/badge/license-Apache%202.0-orange.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Open Source Spring Boot Starter for Apache RocketMQ, develop with RocketMQ easily.

[中文版](README_zh_CN.md)

## Quick Start

### Maven Dependency

```xml

    <dependency>
        <groupId>io.github.rhwayfun</groupId>
        <artifactId>spring-boot-rocketmq-starter</artifactId>
        <version>0.0.4.RELEASE</version>
    </dependency>

```
### Configuration

You just need to add a few configurations on `application.properties`, configurations are like as follows:

```properties

# Apache RocketMQ
spring.rocketmq.nameServer=localhost:9876
spring.rocketmq.producer-group-name=spring-boot-test-producer-group

```

Look, it's easy, but in the real environment, you need modify above configurations.

### Consume message

For consume message, just inherit from class `io.github.rhwayfun.springboot.rocketmq.starter.core.AbstractRocketMqConsumer`

Example：

```java

@Component
    public class DemoMqConsumer extends AbstractRocketMqConsumer<DemoMqTopic, DemoMqContent> {


        @Override
        public boolean consumeMsg(RocketMqContent content, MessageExt msg) {
            System.out.println(new Date() + ", " + content);
            return true;
        }

        @Override
        public Map<String, Set<String>> subscribeTopicTags() {
            Map<String, Set<String>> map = new HashMap<>();
            Set<String> tags = new HashSet<>();
            tags.add("test-tag");
            map.put("test-topic", tags);
            return map;
        }

        @Override
        public String getConsumerGroup() {
            return "test-consumer-group";
        }
    }

    public class DemoMqTopic implements RocketMqTopic{

        @Override
        public String getTopic() {
            return "test-topic";
        }
    }

    public class DemoMqContent extends RocketMqContent {
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

```

### Send message

We encapsulate the part of the message sent for common use. The default implementation is `DefaultRocketMqProducer`.

if you need send message with RocketMQ, autowire this bean in your application. 

example: 

```java

@Component
public class DemoRocketMqProducerExample {

    @Resource
    private DefaultRocketMqProducer producer; //this bean is provided by default.

    @PostConstruct
    public void execute() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                DemoRocketMqContent content = new DemoRocketMqContent();
                content.setCityId(1);
                content.setDesc("城市");
                Message msg = new Message("TopicA", "TagA", content.toString().getBytes());
                boolean sendResult = producer.sendMsg(msg);
                System.out.println("发送结果：" + sendResult);
            }
        }, 0, 10000);
    }

}

```

## More configurations

|   num   | config                                       | description   | default  |
| ---- | ----------------------------------------     | ---- | -------- |
| 1 | spring.rocketmq.nameServer |  name server    |  |
| 2 | spring.rocketmq.producerGroupName |  name of producer    | |
| 3 | spring.rocketmq.producerSendMsgTimeout |  millis of send message timeout    | 3000 |
| 4 | spring.rocketmq.producerCompressMsgBodyOverHowMuch |  Compress message body threshold    | 4000 |
| 5 | spring.rocketmq.producerRetryTimesWhenSendFailed |  Maximum number of retry to perform internally before claiming sending failure in synchronous mode    |  2 |
| 6 | spring.rocketmq.producerRetryTimesWhenSendAsyncFailed |  Maximum number of retry to perform internally before claiming sending failure in asynchronous mode    | 2  |
| 7 | spring.rocketmq.producerRetryAnotherBrokerWhenNotStoreOk |  Indicate whether to retry another broker on sending failure internally    | false |
| 8 | spring.rocketmq.producerMaxMessageSize |  Maximum allowed message size in bytes    | 1024 * 4  |


## License

Adopting the Apache License 2.0 protocol for licensing

