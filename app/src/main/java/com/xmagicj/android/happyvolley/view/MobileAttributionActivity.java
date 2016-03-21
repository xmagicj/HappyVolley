package com.xmagicj.android.happyvolley.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xmagicj.android.happyvolley.R;
import com.xmagicj.android.happyvolley.model.entity.MobileInfo;
import com.xmagicj.android.happyvolley.presenter.IMobileAttributionPresenter;
import com.xmagicj.android.happyvolley.presenter.MobileAttributionPresenter;

/**
 * MobileAttributionActivity
 */
public class MobileAttributionActivity extends AppCompatActivity implements IMobileAttributionView {

    // UI references.
    private EditText etPhoneNum;
    private TextView tvAttribution;
    private View mProgressView;
    private View mLoginFormView;

    private IMobileAttributionPresenter mobileAttributionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_attribution);

        mobileAttributionPresenter = new MobileAttributionPresenter(MobileAttributionActivity.this, this);

        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        tvAttribution = (TextView) findViewById(R.id.tv_attribution);
        mLoginFormView = findViewById(R.id.form);
        mProgressView = findViewById(R.id.progress);

        Button button = (Button) findViewById(R.id.btn_search);
        if (button != null) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mobileAttributionPresenter.getMobileAttribution(etPhoneNum.getText().toString());
                }
            });
        }

    }


    /**
     * Android studio创建项目自动生成代码 懒得动了
     * 按道理 这些逻辑也应该拆分到 Presenter去处理
     * UI只负责 hide 或 show 对应的 控件即可
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void setData(MobileInfo mobileInfo) {
        tvAttribution.setText(mobileInfo.getCarrier());
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        etPhoneNum.setError(errorMsg);
    }

    @Override
    public void showLoading() {
        showProgress(true);
    }

    @Override
    public void hideLoading() {
        showProgress(false);
    }
}

