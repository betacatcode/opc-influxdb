package com.xinda.xiaoxing.task;

import com.xinda.xiaoxing.config.opc.KepConnectionListener;
import com.xinda.xiaoxing.entity.domain.Tag;
import com.xinda.xiaoxing.util.DateTimeUtil;
import com.xinda.xiaoxing.util.OPCUtil;
import org.influxdb.dto.Point;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.da.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
public class KEPSyncTask {

    Logger logger = LoggerFactory.getLogger(KEPSyncTask.class);

    @Autowired
    Server kepServer;
    @Autowired
    KepConnectionListener kepConnectionListener;
    @Autowired
    Map<String, Item> kepIdItemMap;
    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    long reconnectionTimes = 0;//重连次数

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        try {
            kepServer.connect();
        } catch (UnknownHostException | AlreadyConnectedException | JIException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步kep服务器数据
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void kepSync() throws JIException {
        if (kepConnectionListener.isConnected()) {
            reconnectionTimes = 0;
            long startTime = System.currentTimeMillis();
            List<Tag> tags = OPCUtil.sync(kepServer, kepIdItemMap);
            long endTime = System.currentTimeMillis();
            long spend = endTime - startTime;
            logger.info(Thread.currentThread().getName()+"=========================读取时间:" + spend + "ms," + "读取数目:" + tags.size());

            List<Point> points=new ArrayList<>();
            for (Tag tag : tags) {
                Point point = Point.measurement("tag")
                        .time(DateTimeUtil.getNanoTime(), TimeUnit.NANOSECONDS)
                        .tag("item", tag.getItem())
                        .addField("value", tag.getValue())
                        .build();
                points.add(point);
            }

            startTime = System.currentTimeMillis();
            influxDBTemplate.write(points);
            endTime = System.currentTimeMillis();
            spend = endTime - startTime;
            logger.info(Thread.currentThread().getName()+"=========================存储时间:" + spend + "ms");
        }
        else {
            reconnectionTimes++;
            logger.info("=========================连接失败,尝试重新连接,重连次数:" + reconnectionTimes);
            try {
                kepServer.connect();
            } catch (UnknownHostException e) {
                logger.info("=========================重新连接失败,未知的host,5秒后重连");
                e.printStackTrace();
            } catch (AlreadyConnectedException e) {
                logger.info("=========================重新连接失败,已存在连接,5秒后重连");
                e.printStackTrace();
            }
        }
    }
}
