package com.lmgy.exportapk.mvp.presenter;

import com.lmgy.exportapk.base.BasePresenter;
import com.lmgy.exportapk.mvp.contract.AboutContract;
import com.lmgy.exportapk.mvp.model.AboutModel;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class AboutPresenter extends BasePresenter<AboutContract.View> implements AboutContract.Presenter {

    private AboutContract.Model model;

    public AboutPresenter() {
        model = new AboutModel();
    }

    @Override
    public void loadAboutList() {
        mView.setAdapter(model.getAboutList());
    }
}
