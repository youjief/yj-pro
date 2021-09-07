package cn.thread;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class J88 {
    public static void main(String[] args) {
        String testStr="a,bb,ff,gg,h4h  , jj,rt,ui";
        List<String> list= Arrays.stream(testStr.split(","))
                .map(String::trim)
                .filter(s->{
            if (s.length()==2){
                return true;
            }else {
                return false;
            }
        }).distinct().collect(Collectors.toList());


        System.out.println(list);
    }
}
