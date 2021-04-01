package com.xinda.xiaoxing.util;

import com.xinda.xiaoxing.constant.DataTypeConstant;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.dto.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfluxDbUtil {

    private String database;
    private String retentionPolicy;
    private String retentionPolicyTime;
    private BatchOptions batchOptions;
    private InfluxDB influxdb;

    /**
     * 操作数据库
     *
     * @param command
     * @return
     */
    public QueryResult query(String command) {
        return influxdb.query(new Query(command, database));
    }

    /**
     * 插入数据库
     *
     * @param measurement
     * @param tags
     * @param fields
     */
    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        insert(measurement, tags, fields, 0, null);
    }

    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields, long time, TimeUnit timeUnit) {
        Point.Builder builder = Point.measurement(measurement);
        builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        builder.tag(tags);
        builder.fields(fields);
        if (0 < time) {
            builder.time(time, timeUnit);
        }
        System.out.println(("influxDB insert data:" + builder.build().toString()));
        influxdb.write(database, retentionPolicy, builder.build());
    }

    public void batchInsert(BatchPoints batchPoints) {
        influxdb.write(batchPoints);
    }

    /**
     * 批量操作结束时手动刷新数据
     */
    public void flush() {
        if (influxdb != null) {
            influxdb.flush();
        }
    }

    /**
     * 如果调用了enableBatch,操作结束时必须调用disableBatch或者手动flush
     */
    public void enableBatch() {
        if (influxdb != null) {
            influxdb.enableBatch(this.batchOptions);
        }
    }

    public void disableBatch() {
        if (influxdb != null) {
            influxdb.disableBatch();
        }
    }

    /**
     * 测试是否已正常连接
     *
     * @return
     */
    public boolean ping() {
        boolean isConnected = false;
        Pong pong;
        try {
            pong = influxdb.ping();
            if (pong != null) {
                isConnected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    /**
     * 设置数据保存策略:retentionPolicy策略名 /database 数据库名/ DURATION 数据保存时限/REPLICATION副本个数/结尾 DEFAULT
     * DEFAULT表示设为默认的策略
     */
    public void createRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                retentionPolicy, database, retentionPolicyTime, 1);
        this.query(command);
    }

    /**
     * 设置自定义保留策略
     *
     * @param policyName
     * @param duration
     * @param replication
     * @param isDefault
     */
    public void createRetentionPolicy(String policyName, String duration, int replication, boolean isDefault) {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s ", policyName,
                database, duration, replication);
        if (isDefault) {
            command = command + " DEFAULT";
        }
        this.query(command);
    }

    /**
     * 创建数据库
     *
     * @param database
     */
    public void createDatabase(String database) {
        influxdb.query(new Query("CREATE DATABASE " + database));
    }

    /**
     * 将查询到的结果集转化为实体列表
     *
     * @param queryResult 结果集
     * @param elementType 实体类型
     * @param <T>         泛型
     * @return 实体列表
     */
    public <T> List<T> queryResultToEntityList(QueryResult queryResult, Class<T> elementType) {
        List<QueryResult.Result> results = queryResult.getResults();
        List<QueryResult.Series> series = results.get(0).getSeries();
        List<T> entityList = null;
        if (series != null) {
            List<String> columns = series.get(0).getColumns();
            List<List<Object>> values = series.get(0).getValues();
            entityList = resultListToEntityList(columns, values, elementType);
        }
        return entityList;
    }


    /**
     * 将查询到的列和值转化为实体列表
     *
     * @param col         列名
     * @param values      值
     * @param elementType 实体类型
     * @param <T>         泛型
     * @return 实体列表
     */
    public <T> List<T> resultListToEntityList(List<String> col, List<List<Object>> values,
                                              Class<T> elementType) {
        List<T> results = new ArrayList<>();
        for (List<Object> valueList : values) {
            try {
                T element = elementType.newInstance();
                for (int i = 0; i < col.size(); i++) {
                    Field field = elementType.getDeclaredField(col.get(i));
                    field.setAccessible(true);

                    Object val = valueList.get(i);
                    if(val!=null){
                        String strVal = val.toString();
                        Class<?> aClass = field.getType();
                        String typeName = aClass.getSimpleName();

                        switch (typeName) {
                            case DataTypeConstant.FLOAT: {
                                field.set(element, Float.valueOf(strVal));
                                break;
                            }
                            case DataTypeConstant.DATE: {
                                field.set(element, DateTimeUtil.stringToDate(strVal));
                                break;
                            }
                            case DataTypeConstant.INTEGER: {
                                field.set(element, Integer.valueOf(strVal));
                                break;
                            }
                            case DataTypeConstant.STRING: {
                                field.set(element, strVal);
                                break;
                            }
                        }
                    }
                }
                results.add(element);
            } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return results;
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

    public void setBatchOptions(BatchOptions batchOptions) {
        this.batchOptions = batchOptions;
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

    public BatchOptions getBatchOptions() {
        return batchOptions;
    }

    public InfluxDB getInfluxdb() {
        return influxdb;
    }
}
