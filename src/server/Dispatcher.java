package server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import servlet.*;
import web.WebApp;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-03 15:16
 * @description: 一个分发器,分发文件或者通过servlet进行服务
 **/
public class Dispatcher implements Runnable {
    private Socket client;
    private Request req;
    private Response res;
    private int code = 200;

    /**
     * 通过socket构造一个分发器,用来分发内容
     * @param client 需要分发器的socket
     */
    public Dispatcher(Socket client) {
        this.client = client;
        try {
            this.req = new Request(client);
            this.res = new Response(client);
        } catch (IOException e) {
            e.printStackTrace();
            code = 500;
            return;
        }
    }

    /**
     * 分发内容
     */
    @Override
    public void run() {
        // 先尝试处理这个请求
        try {
            WebApp app = new WebApp();
            Servlet servlet = app.getServlet(req.getUri());
            if (servlet != null) {
                code = servlet.service(req, res);
            }
        } catch (Exception e) {
            code = 404;
        }
        // 先正常发送一下
        boolean send = true;
        try {
            res.push(code);
            send = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发送出错就应该再此发送500错误，表达服务器出现错误;发送没有问题就关闭连接
        try {
            if (send) {
                res.push(500);
            }else{
                client.close();
            }
        } catch (IOException e) {
            // 服务器出现问题
            Logger.getGlobal().info("server error");
            e.printStackTrace();
        }
    }
}
