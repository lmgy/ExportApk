package com.lmgy.exportapk.mvp.contract;

import com.lmgy.exportapk.bean.SettingsBean;

import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public interface SettingsContract {

    interface Model {

        List<SettingsBean> getSettingList();

    }

    interface View {

        void setAdapter(List<SettingsBean> settingsBeanList);

    }

    interface Presenter {

        void loadSettingList();

    }

}
