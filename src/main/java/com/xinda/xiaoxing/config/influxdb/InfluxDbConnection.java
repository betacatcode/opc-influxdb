package com.xinda.xiaoxing.config.influxdb;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import javax.annotation.PostConstruct;

public class InfluxDbConnection {
    private String username;
    private String password;
    private String url;
    public String database;
    private String retentionPolicy;
    private String retentionPolicyTime;
    private BatchOptions batchOptions;
    private InfluxDB influxdb;

    @PostConstruct
    public InfluxDB buildInfluxDb() {
        if (influxdb == null) {
            influxdb = InfluxDBFactory.connect(url, username, password);
            influxdb.setDatabase(database);
            influxdb.enableBatch(batchOptions);
        }
        return influxdb;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getDatabase() {
        return database;
    }

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public String getRetentionPolicyTime() {
        return retentionPolicyTime;
    }

    public InfluxDB getInfluxdb() {
        return influxdb;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setRetentionPolicy(String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    public void setRetentionPolicyTime(String retentionPolicyTime) {
        this.retentionPolicyTime = retentionPolicyTime;
    }

    public void setInfluxdb(InfluxDB influxdb) {
        this.influxdb = influxdb;
    }

    public BatchOptions getBatchOptions() {
        return batchOptions;
    }

    public void setBatchOptions(BatchOptions batchOptions) {
        this.batchOptions = batchOptions;
    }
}
