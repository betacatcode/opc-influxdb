package com.xinda.xiaoxing.config.influxdb;

import com.xinda.xiaoxing.util.InfluxDbUtil;
import org.influxdb.BatchOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfiguration{

    @Bean
    @ConfigurationProperties(prefix = "spring.influx.batch-options")
    public BatchProperties batchProperties(){
        return new BatchProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.influx.connection")
    public InfluxDbConnection influxDbConnection(BatchProperties batchProperties){
        InfluxDbConnection influxDbConnection=new InfluxDbConnection();

        BatchOptions batchOptions= BatchOptions.DEFAULTS;
        batchOptions.actions(batchProperties.getActions());
        batchOptions.flushDuration(batchProperties.getFlushDuration());
        batchOptions.jitterDuration(batchProperties.getJitterDuration());
        batchOptions.bufferLimit(batchProperties.getBufferLimit());
        influxDbConnection.setBatchOptions(batchOptions);
        return influxDbConnection;
    }

    @Bean
    public InfluxDbUtil influxDbUtil(InfluxDbConnection influxDbConnection){
        InfluxDbUtil influxDbUtil=new InfluxDbUtil();
        influxDbUtil.setDatabase(influxDbConnection.getDatabase());
        influxDbUtil.setRetentionPolicy(influxDbConnection.getRetentionPolicy());
        influxDbUtil.setRetentionPolicyTime(influxDbConnection.getRetentionPolicyTime());
        influxDbUtil.setInfluxdb(influxDbConnection.getInfluxdb());
        influxDbUtil.setBatchOptions(influxDbConnection.getBatchOptions());
        return influxDbUtil;
    }
}
