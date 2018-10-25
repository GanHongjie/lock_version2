package com.yiyun.lockcontroller.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.yiyun.lockcontroller.bean.lock.LockDownBean;
import com.yiyun.lockcontroller.bean.lock.LockKeysBean;
import com.yiyun.lockcontroller.net.ApiException;
import com.yiyun.lockcontroller.net.HTTPResultFunc;
import com.yiyun.lockcontroller.net.NetHelper;
import com.yiyun.lockcontroller.net.lock.PublicGetJsonObject;
import com.yiyun.lockcontroller.net.lock.PublicPutJsonObject;
import com.yiyun.lockcontroller.rxjava.RxOperator;
import com.yiyun.lockcontroller.ui.base.BaseMvpPresenter;
import com.yiyun.lockcontroller.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.yiyun.lockcontroller.bean.lock.LockKeysBean.USER_TYPE_COMMON;
import static com.yiyun.lockcontroller.utils.ConstantsUtil.USER_LOCK_ADDRESS;
import static com.yiyun.lockcontroller.utils.ConstantsUtil.USER_LOCK_MAC;
import static com.yiyun.lockcontroller.utils.ConstantsUtil.USER_LOCK_NO;

/**
 * 主activity的逻辑层
 * 负责统领所有的内容 如查询用户拥有钥匙
 * Created by Layo on 2018-1-19.
 */

public class MainPresenter extends BaseMvpPresenter<MainContract.View> implements MainContract.Presenter {


    public MainPresenter(MainContract.View mView) {
        super(mView);
    }


    @Override
    public void searchMyKeys(Context context) {
        PublicPutJsonObject publicJsonObject = new PublicPutJsonObject(context);
        publicJsonObject.getUnData().addProperty("startRecord", 0);
        publicJsonObject.getUnData().addProperty("endRecord", 999);

        Disposable subscribe = NetHelper.getInstance().searchMyKeys(publicJsonObject.toStringAES())
                .map(new HTTPResultFunc<LockDownBean>())
                .compose(RxOperator.<LockDownBean>rxSchedulerTransformer())
                .subscribe(new Consumer<LockDownBean>() {
                    @Override
                    public void accept(@NonNull LockDownBean bean) {
                        PublicGetJsonObject publicGetJsonObject = new PublicGetJsonObject(bean);
                        JsonArray list = publicGetJsonObject.getList();
                        List<LockKeysBean> logBeans = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            LockKeysBean authorizeLogBean = new Gson().fromJson(list.get(i), LockKeysBean.class);
                            logBeans.add(authorizeLogBean);
                            if (authorizeLogBean.getUserType() == USER_TYPE_COMMON) {
                                SPUtil.getInstance().putString(USER_LOCK_ADDRESS, authorizeLogBean.getAddress());
                                SPUtil.getInstance().putString(authorizeLogBean.getAddress() + USER_LOCK_MAC, authorizeLogBean.getMac());
                                SPUtil.getInstance().putString(authorizeLogBean.getAddress() + USER_LOCK_NO, authorizeLogBean.getLockNo());
                            }
                        }
                        getView().searchSuccess(logBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) {
                        if (throwable instanceof ApiException) {
                            int errCode = ((ApiException) throwable).getErrCode();
                            getView().showError("api错误" + errCode);
                        } else {
                            getView().showError("错误" + throwable.getMessage());
                        }
                    }
                });
        addSubscription(subscribe);
    }
}
