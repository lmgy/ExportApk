package com.lmgy.exportapk.mvp.model;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.SettingsBean;
import com.lmgy.exportapk.mvp.contract.SettingsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class SettingsModel implements SettingsContract.Model {
    @Override
    public List<SettingsBean> getSettingList() {
        List<SettingsBean> settingsBeanList = new ArrayList<>();
        settingsBeanList.add(new SettingsBean("导出路径", R.drawable.ic_settings_export));
        settingsBeanList.add(new SettingsBean("导出规则", R.drawable.ic_settings_rule));
        settingsBeanList.add(new SettingsBean("关于", R.drawable.ic_settings_about));
        return settingsBeanList;
    }
}
