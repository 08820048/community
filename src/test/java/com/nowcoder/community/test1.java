package com.nowcoder.community;

/**
 * @author: Tisox
 * @date: 2022/2/13 12:03
 * @description:
 * @blog:www.waer.ltd
 */
public class test1 {
    public static void main(String[] args) {
        String s=  "hello";
        translate(s);
    }
    public static String translate(String str){
        String ss = "";
        try {
            ss = new String(str.getBytes("ISO-8859-1"),"GBK");
            ss= ss.trim();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return str;
    }
}
