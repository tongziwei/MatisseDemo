package com.goertek.matissedemo.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goertek.matissedemo.R;
import com.goertek.matissedemo.util.OnRecyclerViewItemClickListener;
import com.zhihu.matisse.internal.entity.SelectionSpec;

import java.util.List;

public class ImageShowAdapter extends RecyclerView.Adapter<ImageShowAdapter.ViewHolder>{

   // private List<Uri> mUriList;
    private List<String> mPathList;
    private Context mContext;
    private int mImageResize;
    private RecyclerView mRecyclerView;
    private SelectionSpec mSelectionSpec;
    private OnRecyclerViewItemClickListener mRecyclerViewItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        OnRecyclerViewItemClickListener mItemClickListener;

        public ViewHolder(View itemView,OnRecyclerViewItemClickListener itemClickListener) {
            super(itemView);
            mItemClickListener = itemClickListener;
            imageView = (ImageView)itemView.findViewById(R.id.iv_item);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(v,getAdapterPosition());
                }
            });
        }
    }

    public ImageShowAdapter(Context context,List<String> pathList,RecyclerView recyclerView) {
        mSelectionSpec = SelectionSpec.getInstance();
       // this.mUriList = mUriList;
        this.mPathList = pathList;
        this.mContext = context;
        this.mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_show_rv_item,parent,false);
        ViewHolder holder = new ViewHolder(view,mRecyclerViewItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      /*  Uri uri = mUriList.get(position);
        Glide.with(mContext).load(uri).into(holder.imageView);*/
        String path = mPathList.get(position);
        Glide.with(mContext).load(path).override(getImageResize(mContext)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
       // return mUriList==null? 0: mUriList.size();
        return  mPathList == null ? 0 :mPathList.size();
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    com.zhihu.matisse.R.dimen.media_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * mSelectionSpec.thumbnailScale);
        }
        return mImageResize;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mRecyclerViewItemClickListener = listener;
    }
}
