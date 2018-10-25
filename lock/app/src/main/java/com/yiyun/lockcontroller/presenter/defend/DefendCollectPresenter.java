package com.yiyun.lockcontroller.presenter.defend;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yiyun.lockcontroller.App;
import com.yiyun.lockcontroller.bean.lock.LockDownBean;
import com.yiyun.lockcontroller.bean.lock.LockKeysBean;
import com.yiyun.lockcontroller.bean.lock.SearchVideoBean;
import com.yiyun.lockcontroller.net.ApiException;
import com.yiyun.lockcontroller.net.HTTPResultFunc;
import com.yiyun.lockcontroller.net.NetHelper;
import com.yiyun.lockcontroller.net.NetService;
import com.yiyun.lockcontroller.net.lock.PublicGetJsonObject;
import com.yiyun.lockcontroller.net.lock.PublicPutJsonObject;
import com.yiyun.lockcontroller.presenter.defend.contract.DefendCollectContract;
import com.yiyun.lockcontroller.rxjava.RxOperator;
import com.yiyun.lockcontroller.ui.base.BaseMvpPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.yiyun.lockcontroller.bean.lock.LockKeysBean.USER_TYPE_COMMON;

/**
 * 云卫一号p层的收集视频数据
 * Created by Layo on 2018-2-2.
 */

public class DefendCollectPresenter extends BaseMvpPresenter<DefendCollectContract.View>
        implements DefendCollectContract.Presenter {
    public DefendCollectPresenter(DefendCollectContract.View mView) {
        super(mView);
    }


    @Override
    public void searchVideo(Context context, SearchVideoBean searchVideoBean) {
        PublicPutJsonObject publicJsonObject = new PublicPutJsonObject(context);
        publicJsonObject.getEnData().addProperty("lockNo", searchVideoBean.getLockNo());
        publicJsonObject.getEnData().addProperty("startTime", searchVideoBean.getStartTime());
        publicJsonObject.getEnData().addProperty("endTime", searchVideoBean.getEndTime());
        Disposable disposable = NetHelper.getInstance().searchVideo(publicJsonObject.toStringAES())
                .map(new HTTPResultFunc<LockDownBean>())
                .compose(RxOperator.<LockDownBean>rxSchedulerTransformer())
                .subscribe(new Consumer<LockDownBean>() {
                    @Override
                    public void accept(LockDownBean bean) throws Exception {
                        PublicGetJsonObject publicGetJsonObject = new PublicGetJsonObject(bean);
                        JsonObject jsonObject = publicGetJsonObject.getEnData();
                        String videoUrl = new Gson().fromJson(jsonObject, String.class);
                        getView().searchVideoSuccess(videoUrl);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof ApiException) {
                            int errCode = ((ApiException) throwable).getErrCode();
                            getView().showError("api错误" + errCode);
                        } else {
                            getView().showError("错误" + throwable.getMessage());
                        }
                    }
                });
        addSubscription(disposable);
    }

    @Override
    public void searchMyKey(Context context) {
        PublicPutJsonObject publicJsonObject = new PublicPutJsonObject(context);
        publicJsonObject.getUnData().addProperty("startRecord", 0);
        publicJsonObject.getUnData().addProperty("endRecord", 999);

        Disposable subscribe = NetHelper.getInstance().searchMyKeys(publicJsonObject.toStringAES())
                .map(new HTTPResultFunc<LockDownBean>())
                .compose(RxOperator.<LockDownBean>rxSchedulerTransformer())
                .subscribe(new Consumer<LockDownBean>() {
                    @Override
                    public void accept(@NonNull LockDownBean bean) throws Exception {
                        PublicGetJsonObject publicGetJsonObject = new PublicGetJsonObject(bean);
                        JsonArray list = publicGetJsonObject.getList();
                        List<LockKeysBean> logBeans = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            LockKeysBean authorizeLogBean = new Gson().fromJson(list.get(i), LockKeysBean.class);
                            if (App.DEBUG) {
                                Log.i("lock", "authorizeLogBean =" + new Gson().toJson(authorizeLogBean));
                            }
                            if (authorizeLogBean.getUserType() == USER_TYPE_COMMON) {
                                logBeans.add(authorizeLogBean);
                            }
                        }
                        getView().searchMyKeysSuccess(logBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
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

