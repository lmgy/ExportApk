package com.lmgy.exportapk.bean;

/**
 * @author lmgy
 * @date 2019/10/19
 */
public class AboutBean {

    private String name;
    private String path;

    public AboutBean(String name, String path){
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
