package io.github.rhwayfun.springboot.rocketmq.starter.constants;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public enum ConsumeMode {

    /**
     * receive asynchronously delivered messages concurrently
     */
    CONCURRENTLY,

    /**
     * receive asynchronously delivered messages orderly. one queue, one thread
     */
    Orderly

}
