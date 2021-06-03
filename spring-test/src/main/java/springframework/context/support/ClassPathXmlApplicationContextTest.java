// Copyright (C) 2021 Meituan
// All rights reserved
package springframework.context.support;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/24 4:37 下午
 **/
@Slf4j
public class ClassPathXmlApplicationContextTest {

    @Test
    public void testMain() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        int beanDefinitionCount = context.getBeanDefinitionCount();
        log.info("cnt: {}", beanDefinitionCount);
//        DemoService bean = context.getBean(DemoService.class);
//        log.info(bean.hello());
    }
}