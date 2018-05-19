package io.github.rhwayfun.springboot.rocketmq.starter.serialize;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public interface MsgBody {

    /**
     * parse msg to specific object
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getBody(Class<T> clazz);

}
