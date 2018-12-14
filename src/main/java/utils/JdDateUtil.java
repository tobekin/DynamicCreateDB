package utils;



import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class JdDateUtil extends DateUtils {
    public static final String dayPattern = "yyyy-MM-dd";

    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public static final String yyyyMMddmmhhss = "yyyyMMddHHmmss";

    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    /**
     * 取得当天时间字符串 yyyy-MM-dd
     *
     * @return
     */
    public static String getToday() {
        return formatDate(new Date(), dayPattern);
    }

    /**
     * 取得当天时间DAY yyyy-MM-dd
     *
     * @return
     * @throws ParseException
     */
    public static Date getNowDay() {
        try {
            return parseDate(formatDate(new Date(), dayPattern), dayPattern);
        } catch (ParseException e) {
            // 因为这个异常时直接通过date对象转化，不可能抛出这个异常，所以不处理
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得昨天时间字符串 yyyy-MM-dd
     *
     * @return
     */
    public static String getYesterdayStr() {
        Date fTime = addDays(new Date(), -1);
        return formatDate(fTime, dayPattern);
    }

    /**
     * 取得昨天时间 yyyy-MM-dd
     *
     * @return
     */
    public static Date getYesterday() {
        Date fTime = null;
        try {
            fTime = parseDate(formatDate(addDays(new Date(), -1), dayPattern), dayPattern);
        } catch (ParseException e) {
            // 这个异常不可能发生，直接捕捉
            e.printStackTrace();
        }
        return fTime;
    }

    /**
     * 取得第二天时间DAY yyyy-MM-dd
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getNextDay(String date) throws ParseException {
        Date d = parseDate(date, JdDateUtil.dayPattern);
        return addDays(d, 1);
    }

    /**
     * 取得第二天时间DAY yyyy-MM-dd
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getNextDay(Date date) throws ParseException {
        return addDays(date, 1);
    }

    /**
     * 取得第3个月时间DAY yyyy-MM-dd
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getThreeMonth(Date date) throws ParseException {
        return addMonths(date, -3);
    }

    /**
     * 格式化时间
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化当前日期
     * @param pattern      日期格式
     * @return
     */
    public static Date formatCurDate(String pattern)throws ParseException{

        return parseDate(formatDateString(new Date()),pattern);
    }

    /**
     * 格式化时间
     * @param date
     * @return
     */
    public static String formatDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dayPattern);
        return sdf.format(date);
    }

    /**
     * 将时间解析成天 yyyy-MM-dd
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseDay(Date date) throws ParseException {
        return parseDate(formatDate(date, dayPattern), dayPattern);
    }

    /**
     * 解析时间
     *
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        if (yyyy_MM_dd_HH_mm_ss.equals(pattern)) {
            if (!Pattern.matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}", date)) {
                throw new ParseException("type error:" + date, 0);
            }
        } else if (yyyyMMddmmhhss.equals(pattern)) {
            if (!Pattern.matches("[0-9]{14}", date)) {
                throw new ParseException("type error:" + date, 0);
            }
        } else if (yyyyMMddHHmmssSSS.equals(pattern)) {
            if (!Pattern.matches("[0-9]{17}", date)) {
                throw new ParseException("type error:" + date, 0);
            }
        } else if (dayPattern.equals(pattern)) {
            if (!Pattern.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}", date)) {
                throw new ParseException("type error:" + date, 0);
            }
        }
        return sdf.parse(date);
    }

    /**
     * 将日期中的 T 替换为空格
     */
    public static String relaceDate(String date) {
        if (date == null && "".equals(date)) {
            return "";
        }
        return date.replace("T", " ");

    }

    /**
     * 取得银行交易的时间[特别针对银行回传的交易时间没有年份，获取年份]/
     * 假设年是当前年，然后比较accountDateTime是否大于当前时间，若大于则属于上一年【比较通用】
     *
     * @param :MMdd
     * @param :HHmmss
     * @return
     * @throws Exception
     */
    public static Date getBankDateTime(String dateMMdd, String timeHHmmss) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddmmhhss);
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        int year = c.get(Calendar.YEAR);
        String dateTimeOld = dateMMdd + timeHHmmss;
        c.setTime(sdf.parse(String.valueOf(year) + dateTimeOld));
        Date accountDateTime = c.getTime();
        if (accountDateTime.after(now)) {
            c.setTime(sdf.parse(String.valueOf(year - 1) + dateTimeOld));
            accountDateTime = c.getTime();
        }
        return accountDateTime;
    }

    /**
     * 取得银行交易的时间[回传日期有年份的]
     *
     * @param :yyMMdd
     * @param :HHmmss
     */
    public static Date getBankYearDateTime(String dateyyMMdd, String timeHHmmss) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddmmhhss);
        Calendar c = Calendar.getInstance();
        String dateTimeOld = dateyyMMdd + timeHHmmss;
        c.setTime(sdf.parse(dateTimeOld));
        Date accountDateTime = c.getTime();
        return accountDateTime;
    }

    /**
     * 校验时间是否规范
     *
     * @param date
     * @param time
     * @return
     * @throws Exception
     */
    public static boolean bankDateTimeCheck(String date, String time) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddmmhhss);
            String dateTimeOld = date + time;
            sdf.parse(dateTimeOld);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Date stringToDate(String bKacctDate, String bKacctTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss);
        StringBuffer sf = new StringBuffer();
        sf.append(bKacctDate).append(" ").append(bKacctTime);
        Date date = null;
        try {
            date = formatter.parse(sf.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Long dateDiffHour(Date startTime, Date endTime) {
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60; // 一小时的毫秒数
        @SuppressWarnings("unused")
		long nm = 1000 * 60; // 一分钟的毫秒数
        @SuppressWarnings("unused")
		long ns = 1000; // 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        @SuppressWarnings("unused")
		long min = 0;
        @SuppressWarnings("unused")
		long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = endTime.getTime() - startTime.getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh + day * 24;// 计算差多少小时
            // min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
            // sec = diff % nd % nh % nm / ns;// 计算差多少秒
        } catch (Exception e) {
            return hour;
        }
        return hour;
    }

    public static String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat(yyyyMMddmmhhss);
        return format.format(new Timestamp(System.currentTimeMillis()));
    }
}
