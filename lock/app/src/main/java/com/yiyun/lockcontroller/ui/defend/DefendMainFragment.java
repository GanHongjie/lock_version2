package com.yiyun.lockcontroller.ui.defend;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.yiyun.lockcontroller.R;
import com.yiyun.lockcontroller.bean.lock.LockKeysBean;
import com.yiyun.lockcontroller.bean.lock.SearchVideoBean;
import com.yiyun.lockcontroller.presenter.defend.DefendCollectPresenter;
import com.yiyun.lockcontroller.presenter.defend.contract.DefendCollectContract;
import com.yiyun.lockcontroller.ui.MainActivity;
import com.yiyun.lockcontroller.ui.base.BaseMvpFragment;
import com.yiyun.lockcontroller.utils.DialogUtil;
import com.yiyun.lockcontroller.utils.LogUtil;
import com.yiyun.lockcontroller.utils.ToastUtil;
import com.yiyun.lockcontroller.view.SublimePickerFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Layo on 2018-2-2.
 */

public class DefendMainFragment extends BaseMvpFragment<DefendCollectContract.Presenter>
        implements DefendCollectContract.View, View.OnClickListener {

    private static WebView mWbSearchVideo;
    private RelativeLayout mRlSearchMyKey;
    private RelativeLayout mRlSelectStartTime;
    private RelativeLayout mRlSelectEndTime;
    private TextView mTvSearchMyKey;
    private TextView mTvSelectStartTime;
    private TextView mTvSelectEndTime;
    private Button mBtnSearch;
    private long mStartTime;
    private long mEndTime;
    private boolean mSelectStartFlag;
    private boolean mSelectEndFlag;

    private List<LockKeysBean> mLockKeysBeans = new ArrayList<>();

    private Dialog mDialog;
    private SublimePickerFragment pickerFragStart = new SublimePickerFragment();
    private SublimePickerFragment pickerFragEnd = new SublimePickerFragment();
    private int lockIndex;

    private final long BASE_ONE_DAY_LONG = 86400000;
    private final long BASE_EIGHT_HOURS = 28800000;

    @Override
    protected DefendCollectContract.Presenter initPresenter() {
        return new DefendCollectPresenter(this);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_defend_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFindId(view);
        initConfig();
    }


    private void initFindId(View view) {
        mWbSearchVideo = view.findViewById(R.id.wb_searchVideo);
        mRlSelectStartTime = view.findViewById(R.id.rl_select_start_time);
        mRlSelectEndTime = view.findViewById(R.id.rl_select_end_time);
        mRlSearchMyKey = view.findViewById(R.id.rl_search_my_key);
        mTvSearchMyKey = view.findViewById(R.id.tv_search_my_key);
        mTvSelectStartTime = view.findViewById(R.id.tv_select_start_time);
        mTvSelectEndTime = view.findViewById(R.id.tv_select_end_time);
        mBtnSearch = view.findViewById(R.id.btn_search_video);

        mRlSearchMyKey.setOnClickListener(this);
        mRlSelectStartTime.setOnClickListener(this);
        mRlSelectEndTime.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);


    }

    private void initConfig() {
        WebSettings setting = mWbSearchVideo.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);
        setting.setSupportZoom(true);

        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);


        setting.setUseWideViewPort(true);
        mWbSearchVideo.setWebViewClient(new WebViewClient());


        // DialogFragment to host SublimePicker
        pickerFragStart.setCallback(mFragmentCallbackStart);
        pickerFragEnd.setCallback(mFragmentCallbackEnd);
        // Valid optionsStart
        Bundle bundleStart = new Bundle();
        bundleStart.putParcelable("SUBLIME_OPTIONS", getOptions(true));
        pickerFragStart.setArguments(bundleStart);
        pickerFragStart.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        //Valid optionsEnd
        Bundle bundleEnd = new Bundle();
        bundleEnd.putParcelable("SUBLIME_OPTIONS", getOptions(false));
        pickerFragEnd.setArguments(bundleEnd);
        pickerFragEnd.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        mPresenter.searchMyKey(mActivity);
    }

    SublimePickerFragment.Callback mFragmentCallbackStart = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {

        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate,
                                            int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {
            mSelectStartFlag = true;
            mStartTime = selectedDate.getStartDate().getTimeInMillis();
            long nowTime = Calendar.getInstance().getTimeInMillis();
            mStartTime = formatStartTime(mStartTime);
            LogUtil.log("" + mStartTime);

            if (!mSelectEndFlag) {
                if (selectedDate.getType() == SelectedDate.Type.SINGLE) {
                    if (mStartTime <= nowTime)
                        mTvSelectStartTime.setText(DateFormat.getDateInstance().format(mStartTime));
                    else
                        ToastUtil.showToast("初始日期大于当前日期");
                }
            } else {
                if (nowTime < mStartTime)
                    ToastUtil.showToast("初始日期大于当前日期");
                else if (mEndTime < mStartTime)
                    ToastUtil.showToast("初始日期大于终止日期");
                else
                    mTvSelectStartTime.setText(DateFormat.getDateInstance().format(mStartTime));
            }
        }


    };


    SublimePickerFragment.Callback mFragmentCallbackEnd = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {

        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate,
                                            int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {
            mSelectEndFlag = true;
            mEndTime = selectedDate.getStartDate().getTimeInMillis();
            long nowTime = Calendar.getInstance().getTimeInMillis();
            mEndTime = formatEndTime(mEndTime);
            nowTime = formatEndTime(nowTime);
            if (mEndTime < mStartTime) {
                ToastUtil.showToast("终止日期小于初始日期");

            } else if (nowTime < mEndTime) {
                ToastUtil.showToast("终止日期大于当前日期");
            } else {
                mTvSelectEndTime.setText(DateFormat.getDateInstance().format(mEndTime));
            }
        }
    };

    private long formatStartTime(long time) {
        return time - (time % BASE_ONE_DAY_LONG) - BASE_EIGHT_HOURS;
    }

    private long formatEndTime(long time) {
        return time + (BASE_ONE_DAY_LONG - time % BASE_ONE_DAY_LONG - BASE_EIGHT_HOURS - 1);
    }


    @Override
    public void onPause() {
        super.onPause();
        mWbSearchVideo.onPause();
        mWbSearchVideo.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWbSearchVideo.onResume();
        mWbSearchVideo.resumeTimers();
    }

    @Override
    public void showError(CharSequence msg) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_start_time: {
                if (pickerFragStart.isAdded()) {
                    pickerFragStart.dismiss();
                }
                pickerFragStart.show(((FragmentActivity) mActivity).getSupportFragmentManager(), "SUBLIME_PICKER");
            }
            break;
            case R.id.rl_select_end_time: {
                if (pickerFragEnd.isAdded()) {
                    pickerFragEnd.dismiss();
                }
                if (mSelectStartFlag)
                    pickerFragEnd.show(((FragmentActivity) mActivity).getSupportFragmentManager(), "SUBLIME_PICKER");
                else
                    ToastUtil.showToast("请先选择初始日期");
            }
            break;
            case R.id.btn_search_video: {
                if (mSelectStartFlag && mSelectEndFlag && mLockKeysBeans.size() != 0) {
                    mWbSearchVideo.setVisibility(View.VISIBLE);
                    MainActivity.setCvInvisible();
                    SearchVideoBean searchVideoBean = new SearchVideoBean(mLockKeysBeans.get(lockIndex).getLockNo(), mStartTime, mEndTime);
                    mPresenter.searchVideo(getActivity(), searchVideoBean);
                } else
                    ToastUtil.showToast("请选择完整日期或选择钥匙");
            }
            break;
            case R.id.rl_search_my_key: {
                ArrayList<String> dataList = new ArrayList<>();
                for (int i = 0; i < mLockKeysBeans.size(); i++) {
                    dataList.add(mLockKeysBeans.get(i).getAddress());
                }
                mDialog = DialogUtil.showList(mActivity, "选择钥匙", dataList,
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                lockIndex = position;
                                mTvSearchMyKey.setText(mLockKeysBeans.get(lockIndex).getAddress());
                                mDialog.dismiss();
                            }
                        });
            }
        }
    }

    // Validates & returns SublimePicker options
    private SublimeOptions getOptions(boolean isRange) {
        //初始化设定
        SublimeOptions options = new SublimeOptions();
        options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
        options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER);
        options.setCanPickDateRange(isRange);

        return options;
    }

    public static void setWbInvisible() {
        mWbSearchVideo.setVisibility(View.GONE);
    }

    @Override
    public void searchVideoSuccess(String videoUrl) {
        mWbSearchVideo.loadUrl(videoUrl);
    }

    @Override
    public void searchVideoFail() {

    }

    @Override
    public void searchMyKeysSuccess(List<LockKeysBean> logBeans) {
        mLockKeysBeans = logBeans;
    }

}
