package web;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: WebServer
 * @author: 陈博文
 * @create: 2022-05-01 19:08
 * @description: mapping from url to servlet
 **/
public class ServletNamePatterns {
    private String servletName;
    private Set<String> patterns;

    public ServletNamePatterns(){
        patterns = new HashSet<>();
        servletName = "";
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public Set<String> getPatterns() {
        return patterns;
    }

    public void addPattern(String pattern){
        patterns.add(pattern);
    }
}
