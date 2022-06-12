package server;

import web.WebApp;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 17:25
 * @description: 服务器类,实现服务器最开始的内容,main函数入口
 **/
public class Server {

    private ServerSocket server;
    private boolean isShutDown;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    /**
     * 开启一个默认的端口号,这里从配置文件中取
     */
    public void start(){
        start(WebApp.getPortNumber());
    }

    /**
     * 开启一个端口
     * @param port 这个端口
     */
    public void start(int port) {
        try {
            server = new ServerSocket(port);
            isShutDown =false;
            receive();
        } catch (IOException e) {
            System.out.println("failed to start server");
            stop();
        }
    }

    /**
     * 开始接收请求
     */
    public void receive() {
        while (!isShutDown) {
            try {
                new Thread(new Dispatcher(server.accept())).start();
            } catch (IOException e) {
                System.out.println("failed to start socket");
                isShutDown = true;
            }
        }
    }

    /**
     * 停止接收请求
     */
    public void stop() {
        isShutDown = true;
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
