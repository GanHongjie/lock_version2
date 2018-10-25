package com.yiyun.lockcontroller.presenter.defend.contract;

import android.content.Context;

import com.yiyun.lockcontroller.bean.lock.LockKeysBean;
import com.yiyun.lockcontroller.bean.lock.SearchVideoBean;
import com.yiyun.lockcontroller.ui.base.BasePresenter;
import com.yiyun.lockcontroller.ui.base.BaseView;

import java.util.List;

/**
 * Created by Layo on 2018-1-3.
 */

public interface DefendCollectContract {
    interface View extends BaseView {
        //查询成功
        void searchVideoSuccess(String videoUrl);
        //查询失败
        void searchVideoFail();

        void  searchMyKeysSuccess(List<LockKeysBean> logBeans);
    }

    interface Presenter extends BasePresenter {
        //网络端口请求收集数据
        void searchVideo(Context context, SearchVideoBean searchVideoBean);

        void searchMyKey(Context context);
    }
}
