package cn;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Random {
    public static void main(String[] args) {
        long l = timestampTen();
        int v = (int)randomNum();
        String sl = String.valueOf(l);
        String sv = String.valueOf(v);
        System.out.println(sl);
        System.out.println(sv);
        if (sv.length() < 6) {
            String vStr = String.format("%06d", v);
            String res = sl + vStr;
            System.out.println(res);
        }
    }

    /**
     * 10位时间戳
     */
    public static long timestampTen() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 0-1000随机数
     */
    public static double randomNum() {
        return  Math.random() * 10;
    }
}
