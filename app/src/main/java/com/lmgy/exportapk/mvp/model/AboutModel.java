package com.lmgy.exportapk.mvp.model;

import com.lmgy.exportapk.bean.AboutBean;
import com.lmgy.exportapk.mvp.contract.AboutContract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class AboutModel implements AboutContract.Model {
    @Override
    public List<AboutBean> getAboutList() {
        List<AboutBean> aboutBeanList = new ArrayList<>();
        aboutBeanList.add(new AboutBean("AndroidX", "https://source.google.com"));
        aboutBeanList.add(new AboutBean("Butterknife", "https://github.com/JakeWharton/butterknife"));
        aboutBeanList.add(new AboutBean("Rxjava", "https://github.com/ReactiveX/RxJava"));
        aboutBeanList.add(new AboutBean("SearchDialog", "https://github.com/wenwenwen888/SearchDialog"));
        return aboutBeanList;
    }
}
