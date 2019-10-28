package com.lmgy.exportapk.mvp.contract;

import com.lmgy.exportapk.bean.AboutBean;

import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public interface AboutContract {

    interface Model {

        List<AboutBean> getAboutList();

    }

    interface View {

        void setAdapter(List<AboutBean> aboutBeanList);

    }

    interface Presenter {

        void loadAboutList();

    }

}
