package com.chinacreator.zw.update_app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinacreator.zw.update_app.listener.IUpdateDialogFragmentListener;
import com.chinacreator.zw.update_app.service.DownloadService;
import com.chinacreator.zw.update_app.utils.AppUpdateUtils;
import com.chinacreator.zw.update_app.view.NumberProgressBar;

import java.io.File;

/**
 */

public class UpdateDialog extends Dialog implements View.OnClickListener  {

    public static final String TIPS = "请授权访问存储空间权限，否则App无法更新";
    private Context mContext;
    public static boolean isShow = false;

    private TextView mContentTextView;
    private TextView mUpdateOkButton;
    private UpdateAppBean mUpdateApp;
    private NumberProgressBar mNumberProgressBar;
    private View mBtnCancel;
    private TextView mTitleTextView;
    private ImageView mTopIv;
    //private TextView mIgnore;
    private View btn_space;

    private IUpdateDialogFragmentListener mUpdateDialogFragmentListener;
    private DownloadService.DownloadBinder mDownloadBinder;

    public void setUpdateDialogFragmentListener(IUpdateDialogFragmentListener updateDialogFragmentListener) {
        this.mUpdateDialogFragmentListener = updateDialogFragmentListener;
    }
    /**
     * 回调
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            startDownloadApp((DownloadService.DownloadBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    public static UpdateDialog newInstance(Context context) {
        UpdateDialog fragment = new UpdateDialog(context,R.style.UpdateAppDialog);

        return fragment;
    }
    public UpdateDialog updateApp(UpdateAppBean _updateApp) {
        mUpdateApp = _updateApp;
        Log.d("UpdateDialog",mUpdateApp.toString());
        return this;
    }
    public UpdateDialog(Context context, int resStyle) {
        super(context, resStyle);
        mContext = context;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isShow = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.update_app_dialog, null);
        setContentView(contentView);
        initView(contentView);
        initData();
        isShow = true;
        boolean cancelTouchout = false;
        setCanceledOnTouchOutside(cancelTouchout);

        Window dialogWindow =getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        lp.height = (int) (displayMetrics.heightPixels * 0.8f);
        dialogWindow.setAttributes(lp);

        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //禁用
                    if (mUpdateApp != null && mUpdateApp.isConstraint()) {
                        //返回桌面
                        mContext.startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });

    }

    private void initView(View view) {
        //提示内容
        mContentTextView = (TextView) view.findViewById(R.id.tv_update_info);
        //标题
        mTitleTextView = (TextView)view.findViewById(R.id.tv_title);
        //更新按钮
        mUpdateOkButton =  (TextView)view.findViewById(R.id.btn_ok);
        //进度条
        mNumberProgressBar = (NumberProgressBar) view.findViewById(R.id.npb);
        //关闭按钮
        mBtnCancel =  view.findViewById(R.id.btn_cancel);
        //关闭按钮+线 的整个布局
        //mLlClose = view.findViewById(R.id.ll_close);
        //顶部图片
        mTopIv = (ImageView) view.findViewById(R.id.iv_top);
        //忽略
       // mIgnore = (TextView) view.findViewById(R.id.tv_ignore);
        btn_space = view.findViewById(R.id.btn_space);
    }

    private void initData() {
        if (mUpdateApp != null) {
            //弹出对话框
            final String dialogTitle = mUpdateApp.getUpdateDefDialogTitle();
            final String newVersion = mUpdateApp.getNewVersion();
            final String targetSize = mUpdateApp.getTargetSize();
            final String updateLog = mUpdateApp.getUpdateLog();
            String msg = "";
            if (!TextUtils.isEmpty(targetSize)) {
                msg = "新版本大小：" + targetSize + "\n\n";
            }
            if (!TextUtils.isEmpty(updateLog)) {
                msg += updateLog;
            }

            //更新内容
            mContentTextView.setText(msg);
            //标题
            mTitleTextView.setText(TextUtils.isEmpty(dialogTitle) ? String.format("是否升级到%s版本？", newVersion) : dialogTitle);
            //强制更新
            if (mUpdateApp.isConstraint()) {
                //mLlClose.setVisibility(View.GONE);
                btn_space.setVisibility(View.GONE);
                mBtnCancel.setVisibility(View.GONE);
            } else {
                //不是强制更新时，才生效
                if (mUpdateApp.isShowIgnoreVersion()) {
                   // mIgnore.setVisibility(View.VISIBLE);
                }
            }

            initEvents();
        }
    }
    private void initEvents() {
        mUpdateOkButton.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
      //  mIgnore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ok) {
            //权限判断是否有访问外部存储空间权限
            installApp();
        } else if (i == R.id.btn_cancel) {
            // TODO @WVector 这里是否要对UpdateAppBean的强制更新做处理？不会重合，当强制更新时，就不会显示这个按钮，也不会调这个方法。
            cancelDownloadService();
            if (mUpdateDialogFragmentListener != null) {
                // 通知用户
                mUpdateDialogFragmentListener.onUpdateNotifyDialogCancel(mUpdateApp);
            }
            dismiss();
        } else if (i == R.id.tv_ignore) {
            AppUpdateUtils.saveIgnoreVersion(mContext, mUpdateApp.getNewVersion());
            dismiss();
        }
    }
    public void cancelDownloadService() {
        if (mDownloadBinder != null) {
            // 标识用户已经点击了更新，之后点击取消
            mDownloadBinder.stop("取消下载");
        }
    }

    private void installApp() {
        if (AppUpdateUtils.appIsDownloaded(mUpdateApp)) {
            AppUpdateUtils.installApp(mContext, AppUpdateUtils.getAppFile(mUpdateApp));
            //安装完自杀
            //如果上次是强制更新，但是用户在下载完，强制杀掉后台，重新启动app后，则会走到这一步，所以要进行强制更新的判断。
            if (!mUpdateApp.isConstraint()) {
                dismiss();
            } else {
                showInstallBtn(AppUpdateUtils.getAppFile(mUpdateApp));
            }
        } else {
            downloadApp();
            //这里的隐藏对话框会和强制更新冲突，导致强制更新失效，所以当强制更新时，不隐藏对话框。
            if (mUpdateApp.isHideDialog() && !mUpdateApp.isConstraint()) {
                dismiss();
            }

        }
    }

    /**
     * 开启后台服务下载
     */
    private void downloadApp() {
        //使用ApplicationContext延长他的生命周期
        DownloadService.bindService(mContext, conn);
    }

    /**
     * 回调监听下载
     */
    private void startDownloadApp(DownloadService.DownloadBinder binder) {
        // 开始下载，监听下载进度，可以用对话框显示
        if (mUpdateApp != null) {

            this.mDownloadBinder = binder;

            binder.start(mUpdateApp, new DownloadService.DownloadCallback() {
                @Override
                public void onStart() {
                    if (UpdateDialog.this.isShowing()) {
                        mNumberProgressBar.setVisibility(View.VISIBLE);
                        mUpdateOkButton.setVisibility(View.GONE);
                        mBtnCancel.setVisibility(View.GONE);
                        btn_space.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onProgress(float progress, long totalSize) {
                    if (UpdateDialog.this.isShowing()) {
                        mNumberProgressBar.setProgress(Math.round(progress * 100));
                        mNumberProgressBar.setMax(100);
                    }
                }

                @Override
                public void setMax(long total) {

                }

                //TODO 这里的 onFinish 和 onInstallAppAndAppOnForeground 会有功能上的重合，后期考虑合并优化。
                @Override
                public boolean onFinish(final File file) {
                    if (UpdateDialog.this.isShowing()) {
                        if (mUpdateApp.isConstraint()) {
                            showInstallBtn(file);
                        } else {
                            dismiss();
                        }
                    }
                    return true;
                }

                @Override
                public void onError(String msg) {
                    if (UpdateDialog.this.isShowing()) {
                        dismiss();
                    }
                }

                @Override
                public boolean onInstallAppAndAppOnForeground(File file) {
                    // 如果应用处于前台，那么就自行处理应用安装
                    AppUpdateUtils.installApp(mContext, file);
                    if (!mUpdateApp.isConstraint()) {
                        dismiss();
                    }
                    return true;
                }
            });
        }
    }

    private void showInstallBtn(final File file) {
        mNumberProgressBar.setVisibility(View.GONE);
        mUpdateOkButton.setText("安装");
        mUpdateOkButton.setVisibility(View.VISIBLE);
        mUpdateOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateUtils.installApp(mContext, file);
            }
        });
    }
}
