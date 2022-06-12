package web;

import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-03 16:29
 * @description: 储存当前所有的配置，辅助web app更好的获取配置项
 **/
public class Context {
    private SAXParser parser;
    private DefaultHandler handler;
    private Map<String, String> servletMap;
    private Map<String, String> mappingMap;
    private List<String> accessiblePaths;
    private int portNumber;

    /**
     * 通过一个xml处理器读取xml文件获取配置文档
     * @param handler xml处理器
     * @throws Exception 可能的文件异常
     */
    public Context(DefaultHandler handler) throws Exception {
        this.handler = handler;
        servletMap = new HashMap<>();
        mappingMap = new HashMap<>();
        accessiblePaths = new ArrayList<>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
    }

    /**
     * 通过一个xml处理器读取xml文件获取配置文档
     * @param handler xml处理器
     * @param goOnParsing 直接进行读取
     * @throws Exception 可能的文件异常
     */
    public Context(DefaultHandler handler, boolean goOnParsing) throws Exception {
        this(handler);
        if (goOnParsing) {
            parse();
        }
    }

    /**
     * 读取操作
     * @throws Exception 可能的文件异常
     */
    public void parse() throws Exception {
        parser.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("web/web.xml"), handler);

        List<ServletNameClass> entities = ((WebHandler)handler).getEntities();
        List<ServletNamePatterns> mappings = ((WebHandler)handler).getMappings();
        accessiblePaths = ((WebHandler)handler).getAccessiblePaths();
        portNumber = ((WebHandler)handler).getPortNumber();

        for (ServletNameClass entity : entities) {
            servletMap.put(entity.getName(), entity.getClassName());
        }
        for (ServletNamePatterns mapping : mappings) {
            for (String pattern : mapping.getPatterns()) {
                mappingMap.put(pattern, mapping.getServletName());
            }
        }
    }

    /**
     * 通过一个url获取一个servlet类名
     * @param pattern 一个url
     * @return 这个url应该由哪一个servlet处理
     */
    public String getClassNameByPattern(String pattern){
        return servletMap.get(mappingMap.get(pattern));
    }

    /**
     * 检验文件路径是否在配置可以访问的文件目录中
     * @param path
     * @return
     */
    public boolean checkInAccessiblePaths(String path) {
        for (String accessiblePath : accessiblePaths) {
            if(path.startsWith(accessiblePath)) return true;
        }
        return false;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
