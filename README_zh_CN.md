## 快速开始

### 依赖

```xml

    <dependency>
        <groupId>io.github.rhwayfun</groupId>
        <artifactId>spring-boot-rocketmq-starter</artifactId>
        <version>0.0.3.RELEASE</version>
    </dependency>

```
### 配置

在`*.properties`配置文件中添加如下配置

```properties

# Apache RocketMQ
# 实际使用需要修改nameServer和producerGroup
spring.rocketmq.nameServer=localhost:9876
spring.rocketmq.producer-group-name=spring-boot-test-producer-group

```

### 消费者

消费者只需继承`io.github.rhwayfun.springboot.rocketmq.starter.common.AbstractRocketMqConsumer`即可

使用示例：

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

### 生产者

对于生产者，使用`DefaultRocketMqProducer`即可，封装了常用的发送消息的方法

## License

采用Apache License 2.0协议进行许可