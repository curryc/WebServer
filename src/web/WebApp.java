package web;

import servlet.Servlet;
import servlet.StaticServlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 17:23
 * @description: 通过这个类获取web app的各种属性，管理员应该将配置卸载web.xml文件中
 **/
public class WebApp {
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss z", Locale.ENGLISH);
    private static Context context;

    /**
     * 通过次静态代码加载出xml配置文件中的信息
     */
    static {
        try {
            WebHandler handler = new WebHandler();
            context = new Context(handler, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isAvailable(File file) {
        return context.checkInAccessiblePaths(file.getPath());
    }

    public static int getPortNumber(){
        return context.getPortNumber();
    }

    /**
     * 获取一个servlet
     *
     * @param url 需要处理的url
     * @return 这个url对应的servlet
     * @throws Exception 从配置文件中读取,可能存在文件异常
     */
    public Servlet getServlet(String url) throws Exception {
        if (url == null || url.equals("")) {
            return null;
        }
        if (url.contains(".")) {
            // 抛入静态文件的servlet
            return new StaticServlet();
        } else {
            return (Servlet) Class.forName(context.getClassNameByPattern(url)).getConstructor().newInstance();
        }
    }
}
