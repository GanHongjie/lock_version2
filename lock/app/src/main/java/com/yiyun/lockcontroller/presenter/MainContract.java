package com.yiyun.lockcontroller.presenter;


import android.content.Context;

import com.yiyun.lockcontroller.bean.lock.LockKeysBean;
import com.yiyun.lockcontroller.ui.base.BasePresenter;
import com.yiyun.lockcontroller.ui.base.BaseView;

import java.util.List;

/**
 * Created by Layo on 2017-12-28.
 */

public interface MainContract {
    interface View extends BaseView {
        //查询成功
        void searchSuccess(List<LockKeysBean> logBeans);
    }

    interface Presenter extends BasePresenter {

        //查询我的所有钥匙
        void searchMyKeys(Context context);
    }
}
