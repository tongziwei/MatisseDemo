package com.goertek.matissedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.goertek.matissedemo.adapter.ImageShowAdapter;
import com.goertek.matissedemo.constant.Config;
import com.goertek.matissedemo.util.GifSizeFilter;
import com.goertek.matissedemo.util.Glide4Engine;
import com.goertek.matissedemo.util.OnRecyclerViewItemClickListener;
import com.goertek.matissedemo.view.ShowImagesDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.ui.widget.MediaGridInset;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final int UPDATE_DATA = 1;
    private int spanCount = 3;
    private Button mBtnShowImage;
    private RecyclerView mRvImage;
    private ImageShowAdapter mAdapter;
   // private List<Uri> mUriList = new ArrayList<>();
   private List<String> mPathList = new ArrayList<>();

    private final int MY_PERMISSIONS_REQUEST = 200;
    private List<String> mPermissionList = new ArrayList<>();
   /* public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_DATA:
                    mAdapter.notifyDataSetChanged();
                  //  mRvImage.setAdapter(mAdapter);
                    break;
                default:
                    break;

            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDeviceDensity();
        if (Build.VERSION.SDK_INT >= 23) {//6.0才用动态权限
            getPermission();
        }

        mBtnShowImage = (Button)findViewById(R.id.btn_choose);
        mRvImage = (RecyclerView)findViewById(R.id.rv_image_show);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mRvImage.setLayoutManager(gridLayoutManager);
        int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
        mRvImage.addItemDecoration(new MediaGridInset(spanCount, spacing, false)); //设置recyclerView间隔样式
        mAdapter = new ImageShowAdapter(this,mPathList,mRvImage);
        mRvImage.setAdapter(mAdapter);

        mBtnShowImage.setOnClickListener(this);
        //点击单个图片
        mAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mPathList != null){
                   new ShowImagesDialog(MainActivity.this,mPathList,position).show();
                }
            }
        });

    }

    private void getPermission(){
        String[] locationPermissions = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mPermissionList.clear();
        for (int i = 0; i < locationPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, locationPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(locationPermissions[i]);
            }
        }

        if (mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this, mPermissionList.toArray(new String[1]), MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose:
                  Matisse.from(MainActivity.this)
                         .choose(MimeType.ofImage())//只显示照片
                         .capture(true)
                          .captureStrategy(new CaptureStrategy(true,"com.goertek.matissedemo.fileprovider"))
                        .countable(true)//有序选择图片
                        .maxSelectable(9)//最大选择数量为9
                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小getResources()
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)//图像选择和预览活动所需的方向。
                        .thumbnailScale(0.85f)//缩放比例
                        .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                        .imageEngine(new Glide4Engine())//加载方式
                        .forResult(REQUEST_CODE_CHOOSE);//请求码
                break;
                default:
                    break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
          //  mUriList.addAll(Matisse.obtainResult(data));
            mPathList.clear();
            mPathList.addAll(Matisse.obtainPathResult(data));
          /*  Message msg = new Message();
            msg.what = UPDATE_DATA;
            msg.obj = mPathList;
            mHandler.sendMessage(msg);*/
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取当前设备的屏幕密度等基本参数
     */
    protected void getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Config.EXACT_SCREEN_HEIGHT = metrics.heightPixels;
        Config.EXACT_SCREEN_WIDTH = metrics.widthPixels;
    }

}
