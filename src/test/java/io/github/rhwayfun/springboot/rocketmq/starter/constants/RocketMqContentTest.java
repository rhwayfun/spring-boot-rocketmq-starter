package io.github.rhwayfun.springboot.rocketmq.starter.constants;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author rhwayfun
 * @since 0.0.1
 */
public class RocketMqContentTest {

    @Test
    public void test() throws Exception {
        DemoRocketMqContent content = new DemoRocketMqContent();
        assertNotNull(content.toString());
    }

    private class DemoRocketMqContent extends RocketMqContent {
        private int cityId;
        private String desc;

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}