package com.xinda.xiaoxing.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class DateTimeUtil {
    static List<DatePattern> datePatterns=new ArrayList();
    static {
        DatePattern datePattern1=new DatePattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3,7}.*","yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        DatePattern datePattern2=new DatePattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}$","yyyy-MM-dd'T'HH:mm:ss");
        datePatterns.add(datePattern1);
        datePatterns.add(datePattern2);
    }
    private static SimpleDateFormat sdf;

    public static Long getMicTime() {
        long cuTime = System.currentTimeMillis() * 1000; // 微秒
        long nanoTime = System.nanoTime(); // 纳秒
        return cuTime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
    }

    public static Long getNanoTime(){
        return System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L;
    }

    public static Date stringToDate(String dateString){
        Pattern p;
        for (DatePattern datePattern : datePatterns) {
            p=Pattern.compile(datePattern.getReg());
            if(p.matcher(dateString).matches()){
                sdf=new SimpleDateFormat(datePattern.getPattern());
                try {
                    return sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    static class DatePattern{
        public String reg;
        public String pattern;

        public DatePattern() {
        }

        public DatePattern(String reg, String pattern) {
            this.reg = reg;
            this.pattern = pattern;
        }

        public String getReg() {
            return reg;
        }

        public void setReg(String reg) {
            this.reg = reg;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }

    public static void main(String[] args) {
//        String pattern2="^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}$";
//
//        String dateStr1="2021-03-24T00:48:30.3265287Z";
//        String dateStr2="2021-03-24T08:48:00+08:00";
//
//        Pattern p= Pattern.compile(pattern1);
//        System.out.println(p.matcher(dateStr1).matches());
//        System.out.println(p.matcher(dateStr2).matches());
//
//        p= Pattern.compile(pattern2);
//        System.out.println(p.matcher(dateStr1).matches());
//        System.out.println(p.matcher(dateStr2).matches());

    }
}
