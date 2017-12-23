package io.github.rhwayfun.springboot.rocketmq.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
@ConfigurationProperties(prefix = RocketMqProperties.ROCKETMQ_PREFIX)
public class RocketMqProperties {

    static final String ROCKETMQ_PREFIX = "spring.rocketmq";

    /**
     * name server
     */
    private String nameServer;

    /**
     * name of producer
     */
    private String producerGroupName;

    /**
     * millis of send message timeout
     */
    private int producerSendMsgTimeout = 3000;

    /**
     * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
     */
    private int producerCompressMsgBodyOverHowMuch = 1024 * 4;

    /**
     * <p> Maximum number of retry to perform internally before claiming sending failure in synchronous mode. </p>
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int producerRetryTimesWhenSendFailed = 2;

    /**
     * <p> Maximum number of retry to perform internally before claiming sending failure in asynchronous mode. </p>
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int producerRetryTimesWhenSendAsyncFailed = 2;

    /**
     * Indicate whether to retry another broker on sending failure internally.
     */
    private boolean producerRetryAnotherBrokerWhenNotStoreOk = false;

    /**
     * Maximum allowed message size in bytes.
     */
    private int producerMaxMessageSize = 1024 * 1024 * 4; // 4M

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getProducerGroupName() {
        return producerGroupName;
    }

    public void setProducerGroupName(String producerGroupName) {
        this.producerGroupName = producerGroupName;
    }

    public int getProducerSendMsgTimeout() {
        return producerSendMsgTimeout;
    }

    public void setProducerSendMsgTimeout(int producerSendMsgTimeout) {
        this.producerSendMsgTimeout = producerSendMsgTimeout;
    }

    public int getProducerCompressMsgBodyOverHowMuch() {
        return producerCompressMsgBodyOverHowMuch;
    }

    public void setProducerCompressMsgBodyOverHowMuch(int producerCompressMsgBodyOverHowMuch) {
        this.producerCompressMsgBodyOverHowMuch = producerCompressMsgBodyOverHowMuch;
    }

    public int getProducerRetryTimesWhenSendFailed() {
        return producerRetryTimesWhenSendFailed;
    }

    public void setProducerRetryTimesWhenSendFailed(int producerRetryTimesWhenSendFailed) {
        this.producerRetryTimesWhenSendFailed = producerRetryTimesWhenSendFailed;
    }

    public int getProducerRetryTimesWhenSendAsyncFailed() {
        return producerRetryTimesWhenSendAsyncFailed;
    }

    public void setProducerRetryTimesWhenSendAsyncFailed(int producerRetryTimesWhenSendAsyncFailed) {
        this.producerRetryTimesWhenSendAsyncFailed = producerRetryTimesWhenSendAsyncFailed;
    }

    public boolean isProducerRetryAnotherBrokerWhenNotStoreOk() {
        return producerRetryAnotherBrokerWhenNotStoreOk;
    }

    public void setProducerRetryAnotherBrokerWhenNotStoreOk(boolean producerRetryAnotherBrokerWhenNotStoreOk) {
        this.producerRetryAnotherBrokerWhenNotStoreOk = producerRetryAnotherBrokerWhenNotStoreOk;
    }

    public int getProducerMaxMessageSize() {
        return producerMaxMessageSize;
    }

    public void setProducerMaxMessageSize(int producerMaxMessageSize) {
        this.producerMaxMessageSize = producerMaxMessageSize;
    }

}
