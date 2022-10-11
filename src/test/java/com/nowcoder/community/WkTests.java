package com.nowcoder.community;

import java.io.IOException;

/**
 * @author: Tisox
 * @date: 2022/4/1 17:27
 * @description: 演示使用Java代码借助wktopdf工具生成图片和PDF
 * @blog:www.waer.ltd
 */
public class WkTests {
    public static void main(String[] args) throws IOException {
        String cmd = "E:/wkhtmltopdf/bin/wkhtmltoimage  https://www.nowcoder.com  " +
                "E:/nowcoder/woekspace/datas/toimage/WithJava.png";
        Runtime.getRuntime().exec(cmd);
        System.out.println("ok");
    }
}
