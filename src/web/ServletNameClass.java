package web;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 19:11
 * @description: ever servlet has a entity to mapping its name and class
 **/
public class ServletNameClass {
    private String name;
    private String className;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
