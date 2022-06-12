package servlet;

import server.Request;
import server.Response;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 17:21
 * @description: a register servlet for web server
 **/
public class RegisterServlet implements Servlet {
    @Override
    public int service(Request request, Response response) {
        String method = request.getMethod();
        if (method.equals("get")) {
            doGet(request, response);
        } else if (method.equals("post")) {
            doPost(request, response);
        }else{
            return 405;
        }
        return 200;
    }

    @Override
    public void doGet(Request request, Response response) {
        response.println("<html><head><title>注册成功 " +
                request.getMethod() + " " + request.getUri() +
                " username : " + request.getParameter("username") +
                " password : " + request.getParameter("password") +
                "</title><head><body>hello world</body></html>");
    }

    @Override
    public void doPost(Request request, Response response) {
        response.println("<html><head><title>注册成功 " +
                request.getMethod() + " " + request.getUri() +
                " username : " + request.getParameter("username") +
                " password : " + request.getParameter("password") +
                "</title><head><body>hello world</body></html>");
    }
}

