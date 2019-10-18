package com.lmgy.exportapk.bean;

/**
 * @author lmgy
 * @date 2019/10/18
 */
public class SettingsBean {

    private String name;
    private int image;

    public SettingsBean(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

}
