package servlet;

import server.Request;
import server.Response;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 17:17
 * @description: a servlet interface for all the servlet
 **/
public interface Servlet {
    /**
     * 服务方法
     * @param request 服务的请求
     * @param response 服务的回复
     * @return 返回一个状态码
     */
    int service(Request request, Response response);

    /**
     * get方法的服务
     * @param request
     * @param response
     */
    void doGet(Request request, Response response);

    /**
     * post方法的回复
     * @param req
     * @param res
     */
    void doPost(Request req, Response res);

    /**
     * 打印相关的状态(用来测试)
     * @param req 正在服务的请求
     */
    default void printInfo(Request req){
        System.out.println(req);
    }
}
