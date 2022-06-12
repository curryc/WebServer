package servlet;

import server.Request;
import server.Response;
import web.WebApp;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-06-08 20:09
 * @description: a servlet to handle static files
 **/
public class StaticServlet implements Servlet {
    @Override
    public int service(Request request, Response response) {
        File file = new File("static" + request.getUri());
        // 静态文件不存在
        if (!file.exists()) {
            return 404;
        }
        // 判断项目是否可以访问
        if(!WebApp.isAvailable(file))
            return 403;
        // 判断静态文件是否需要重传
        String date = request.getParameter("if-modified-since");
        if (date != null && checkDate(file, date)) {
            try {
                fileTrans(new BufferedReader(new FileReader(file)), request, response);
            } catch (Exception e) {
                e.printStackTrace();
                return 505;
            }
            response.addInHeader("Last-Modified", new Date(file.lastModified()).toString());
        } else {
            return 304;
        }
        return 200;
    }

    /**
     * 向输出流中刷入文件
     *
     * @param br  文件
     * @param req 请求
     * @param res 回复
     */
    public void fileTrans(BufferedReader br, Request req, Response res) throws IOException {
        String str = "";
        while ((str = br.readLine()) != null) {
            res.println(str);
        }
    }

    /**
     * 验证文件修改的时间是否在date之后
     *
     * @param file
     * @param date
     * @return
     */
    private boolean checkDate(File file, String date) {
        Date lastModified = new Date(file.lastModified());
        Date request = null;
        try {
            request = WebApp.DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lastModified.after(request);
    }


    public static void main(String[] args) {
        String s1 = "EEE dd MM yyyy HH:mm:ss zzz";
        String s2 = "Thu 08 06 2022 13:07:07 GMT";
        String z1 = "EEE, dd MM yyyy HH:mm:ss z";
        String z2 = "Thu, 12 01 2022 13:07:07 GMT";
        String a1 = "yyyy-MM-dd HH:mm:ss";
        String a2 = "2022-06-08 13:07:07";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(z1, Locale.ENGLISH);
            Date request = sdf.parse(z2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(new SimpleDateFormat(z1, Locale.ENGLISH).format(new Date()));

        System.out.println("hello".indexOf("z"));
    }

    @Override
    public void doGet(Request request, Response response) {
    }

    @Override
    public void doPost(Request req, Response res) {
    }
}
