package com.xinda.xiaoxing.util;

import com.xinda.xiaoxing.constant.DataTypeConstant;
import org.influxdb.dto.QueryResult;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InfluxDBUtil {

    /**
     * 将查询到的结果转化为实体列表
     *
     * @param queryResult 结果集
     * @param elementType 实体类型
     * @param <T>         泛型
     * @return 实体列表
     */
    public static <T> List<T> queryResultToEntityList(QueryResult queryResult, Class<T> elementType) {
        List<QueryResult.Result> results = queryResult.getResults();
        List<QueryResult.Series> series = results.get(0).getSeries();
        List<T> entityList = new ArrayList<>();

        if (series != null) {
            List<String> columns = series.get(0).getColumns();
            List<List<Object>> values = series.get(0).getValues();

            for (List<Object> valueList : values) {
                try {
                    T element = elementType.newInstance();
                    for (int i = 0; i < columns.size(); i++) {
                        Field field = elementType.getDeclaredField(columns.get(i));
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
                                case DataTypeConstant.LONG:{
                                    BigDecimal bigDecimal=new BigDecimal(strVal);
                                    field.set(element,bigDecimal.longValue());
                                    break;
                                }
                                case DataTypeConstant.STRING: {
                                    field.set(element, strVal);
                                    break;
                                }
                            }
                        }
                    }
                    entityList.add(element);
                } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return entityList;
    }

}
