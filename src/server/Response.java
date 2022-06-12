package server;

import web.WebApp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 20:53
 * @description: a html response to response the client
 **/
public class Response {
    private final String BLANK = " ";
    private final String CRLF = "\r\n";

    private OutputStream outputStream;
    private BufferedWriter bufferedWriter;

    private StringBuilder header;

    private StringBuilder content;

    private Map<String, String> params;

    public Response() {
        header = new StringBuilder();
        content = new StringBuilder();
        params = new HashMap<>();
    }

    /**
     * 通过一个socket构造一个回复
     *
     * @param client 这个socket
     */
    public Response(Socket client) {
        this();
        try {
            this.outputStream = client.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过要给输出流构造一个回复
     *
     * @param outputStream 输出流
     */
    public Response(OutputStream outputStream) {
        this();
        this.outputStream = outputStream;
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }


    /**
     * 向回复的正文写入
     *
     * @param o
     */
    public void print(Object o) {
        if (o != null) {
            content.append(o);
        }
    }

    /**
     * 向回复的正文写入并换行
     *
     * @param o
     */
    public void println(Object o) {
        print(o);
        if (o != null) {
            content.append(CRLF);
        }
    }

    /**
     * 将回复推送导输出流中
     *
     * @param code 回复的状态码
     * @throws IOException 可能存在输出流异常
     */
    public void push(int code) throws IOException {
        StringBuilder response = new StringBuilder();

        createHeader(code);
        response.append(header);
        response.append(content);
        bufferedWriter.write(response.toString());
        bufferedWriter.flush();
    }

    /**
     * 再头部参数中添加条目
     *
     * @param key
     * @param value
     */
    public void addInHeader(String key, String value) {
        if (params.containsKey(key)) {
            params.get(key).concat(value);
        } else {
            params.put(key, value);
        }
    }

    /**
     * 构造要给回复的头部
     *
     * @param code 回复头部的状态码
     */
    private void createHeader(int code) {
        header.append("HTTP/1.1").append(BLANK);
        header.append(code).append(BLANK);

        switch (code) {
            case 200:
                header.append("OK").append(CRLF);
                break;
            case 404:
                header.append("NOT FOUND").append(CRLF);
                break;
            case 505:
                header.append("SERVER ERROR").append(CRLF);
                break;
            case 400:
                header.append("BAD REQUEST").append(CRLF);
                break;
            case 403:
                header.append("Forbidden").append(CRLF);
                break;
            case 304:
                header.append("NOT MODIFIED").append(CRLF);
                break;
            case 405:
                header.append("METHOD NOT ALLOWED").append(CRLF);
        }

        header.append("Data:").append(WebApp.DATE_FORMATTER.format(new Date())).append(CRLF);
        header.append("Server:").append("cbw server 1.0;encoding=utf-8").append(CRLF);
        header.append("Content-type:text/html").append(CRLF);
        header.append("Content-length:").append(content.toString().getBytes(StandardCharsets.UTF_8).length).append(CRLF);

        params.forEach((k, v) -> {
            header.append(k).append(v).append(CRLF);
        });

        header.append(CRLF);
    }
}
