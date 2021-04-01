package com.xinda.xiaoxing;

import com.xinda.xiaoxing.entity.domain.T;
import com.xinda.xiaoxing.entity.domain.Tag;
import com.xinda.xiaoxing.util.InfluxDbUtil;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootTest
class XiaoxingApplicationTests {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    InfluxDbUtil influxDbUtil;

    @Test
    void contextLoads() {

    }

    @Test
    void beans(){
        int beanDefinitionCount = applicationContext.getBeanDefinitionCount();
        System.out.println(beanDefinitionCount);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

    @Test
    void read(){
        QueryResult queryResult = influxDbUtil.query("select * from \"tag\" TZ('Asia/Shanghai')");
        List<Tag> tags = influxDbUtil.queryResultToEntityList(queryResult, Tag.class);
        for (Tag tag : tags) {
            System.out.println(tag);
        }
    }

    @Test
    void sum(){
        QueryResult queryResult =
                influxDbUtil.query(" select sum(value) from \"tag\" where time<'2021-03-24T08:52:00+08:00' group by time(1m) TZ('Asia/Shanghai')");
        List<T> res = influxDbUtil.queryResultToEntityList(queryResult, T.class);
        for(T t:res){
            System.out.println(t);
        }
    }

}
