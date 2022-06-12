package web;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 17:26
 * @description: a xml loader handler for the web.xml
 **/
public class WebHandler extends DefaultHandler {
    private List<ServletNameClass> entities;
    private List<ServletNamePatterns> mappings;
    private List<String> accessiblePaths;
    private int portNumber;

    private String tag;
    private State state;

    private ServletNameClass entity;
    private ServletNamePatterns mapping;
    private String path;

    enum State {
        Servlet,
        Mapping,
        AccessiblePath,
        PortNumber
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String element = new String(ch, start, length).trim();
        if (!element.equals("") && state != null) {
            switch (state) {
                case Servlet:
                    if (tag.equals("servlet-name")) {
                        entity.setName(element);
                    } else if (tag.equals("servlet-class")) {
                        entity.setClassName(element);
                    }
                    break;
                case Mapping:
                    if (tag.equals("servlet-name")) {
                        mapping.setServletName(element);
                    } else if (tag.equals("url-pattern")) {
                        mapping.addPattern(element);
                    }
                    break;
                case AccessiblePath:
                    path = element;
                    break;
                case PortNumber:
                    portNumber = Integer.parseInt(element);
                    break;
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName != null) {
            if (qName.equals("servlet")) {
                entity = new ServletNameClass();
            } else if (qName.equals("servlet-mapping")) {
                mapping = new ServletNamePatterns();
            } else if (qName.equals("accessible-path")) {
                path = new String();
            } else if (qName.equals("servlets")) {
                entities = new ArrayList<>();
                state = State.Servlet;
            } else if (qName.equals("mappings")) {
                mappings = new ArrayList<>();
                state = State.Mapping;
            } else if (qName.equals("accessible-paths")) {
                accessiblePaths = new ArrayList<>();
                state = State.AccessiblePath;
            } else if(qName.equals("port-number")){
                portNumber = 0;
                state = State.PortNumber;
            }
            tag = qName;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName != null) {
            if (qName.equals("servlet")) {
                entities.add(entity);
            } else if (qName.equals("servlet-mapping")) {
                mappings.add(mapping);
            } else if (qName.equals("accessible-path")) {
                accessiblePaths.add(path);
            }
        }
        tag = null;
    }

    /**
     * 获取读取到的每一个servlet名称对应的servlet类名
     *
     * @return
     */
    public List<ServletNameClass> getEntities() {
        return entities;
    }

    /**
     * 获取读取到的每一个url对应的servlet名称
     *
     * @return
     */
    public List<ServletNamePatterns> getMappings() {
        return mappings;
    }

    /**
     * 获取得到的每个可以访问的文件路径前缀
     * @return
     */
    public List<String> getAccessiblePaths(){
        return accessiblePaths;
    }

    public int getPortNumber(){
        return portNumber;
    }
}
