package com.lmgy.exportapk.mvp.presenter;

import com.lmgy.exportapk.base.BasePresenter;
import com.lmgy.exportapk.mvp.contract.SettingsContract;
import com.lmgy.exportapk.mvp.model.SettingsModel;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class SettingsPresenter extends BasePresenter<SettingsContract.View> implements SettingsContract.Presenter {

    private SettingsContract.Model model;

    public SettingsPresenter() {
        model = new SettingsModel();
    }

    @Override
    public void loadSettingList() {
        mView.setAdapter(model.getSettingList());
    }
}
