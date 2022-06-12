package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.*;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 21:14
 * @description: a request from the html
 **/
public class Request {
    private final String CRLF = "\r\n";

    private String request;
    private String method;
    private String uri;
    private String queryStr;
    private Map<String, List<String>> parameterMap;

    public Request(Socket client) throws IOException {
        this(client.getInputStream());
    }

    @Override
    public String toString() {
        return request;
    }

    /**
     * 通过一个输入流构造一个请求
     *
     * @param is
     */
    public Request(InputStream is) {
        queryStr = "";
        parameterMap = new HashMap<>();
        byte[] bytes = new byte[1024 * 1024];
        try {
            int len = is.read(bytes);
            request = new String(bytes, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println(request);
        parseRequest(request);
    }

    /**
     * 通过一个key来获取这个key对应的每个参数的value
     *
     * @param key
     * @return
     */
    public String[] getParameters(String key) {
        List<String> params = parameterMap.get(key.toLowerCase(Locale.ROOT));
        if (params != null && params.size() > 0) {
            return params.toArray(new String[0]);
        } else {
            return null;
        }
    }

    /**
     * 通过一个key来获取这个key对应的每个参数的一个value
     *
     * @param key
     * @return
     */
    public String getParameter(String key) {
        String[] values = getParameters(key);
        return values == null ? null : values[0];
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getQueryStr() {
        return queryStr;
    }

    /**
     * 处理中文的gbk编码
     *
     * @param src 需要处理的字符串
     * @param enc 处理的编码
     * @return 处理好的字符串
     */
    private String decode(String src, String enc) {
        try {
            return java.net.URLDecoder.decode(src, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取请求中的各种参数,方法等等
     *
     * @param request 需要读取的请求
     */
    private void parseRequest(String request) {
        int start = 0;
        int end = request.indexOf('/');
        method = request.substring(start, end).toLowerCase(Locale.ROOT).trim().toLowerCase(Locale.ROOT);

        start = end;
        end = request.indexOf("HTTP/");
        uri = request.substring(start, end).toLowerCase(Locale.ROOT).trim().toLowerCase(Locale.ROOT);

        // 有可能请求网址中带有参数，需要处理
        int query = uri.indexOf('?');
        if (query >= 0) {
            // 利用正则表达式讲问号两边分割开来,一边是uri,一边是参数
            String[] uriArray = uri.split("\\?");
            uri = uriArray[0];
            queryStr = uriArray[1];
//            queryStr = decode(request.substring(query), "gbk");
        }
        queryStr += request.substring(request.indexOf("\n"));
        toParameterMap(queryStr);
    }

    /**
     * 把参数放到一个hashMap中方便读取
     *
     * @param str 包含各种参数的字符串
     */
    private void toParameterMap(String str) {
        // 如果请求网址中带有参数
        String para = str.substring(0, str.indexOf("\n"));
        if (para.contains("&")) {
            // 处理请求网址中的参数
            tokenizeString(new StringTokenizer(para, "&"), this.parameterMap);
            str = str.substring(str.indexOf("\n"));
        }
        tokenizeString(new StringTokenizer(str, "\n"), this.parameterMap);
    }

    /**
     * 根据一个StringTokenizer抽离字符串中的参数并放如一个map中
     *
     * @param st
     * @param parameterMap
     */
    public void tokenizeString(StringTokenizer st, Map<String, List<String>> parameterMap) {
        if (st == null || parameterMap == null) {
            return;
        }
        int index = -1;
        String key = null;
        String value = null;
        int count = st.countTokens();
        for (int i = 0; i < count; i++) {
            String p = st.nextToken();
            if (p != null && !p.equals("")) {
                if ((p.indexOf('=') < p.indexOf(':') || !p.contains(":")) && p.contains("=")) {
                    index = p.indexOf("=");
                    key = p.substring(0, index).trim().toLowerCase(Locale.ROOT);
                    value = p.substring(index+1).trim();
                    if (!parameterMap.containsKey(key)) {
                        parameterMap.put(key.trim(), new ArrayList<>());
                    }
                    parameterMap.get(key).add(value);
                } else if ((p.indexOf(':') < p.indexOf('=')) || !p.contains("=") && p.contains(":")) {
                    index = p.indexOf(":");
                    key = p.substring(0, index).trim().toLowerCase(Locale.ROOT);
                    value = p.substring(index+1).trim();
                    if (!parameterMap.containsKey(key)) {
                        parameterMap.put(key.trim(), new ArrayList<>());
                    }
                    parameterMap.get(key).add(value);
                }
            }
        }
    }
}
