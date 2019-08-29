package com.bestom.whitelist;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List<IPackage> mPackages;
    private LayoutInflater mInflater;
    private List<PackageInfo> mAppList;

    public MyAdapter(Context context, List<IPackage> packages) {
        this.mContext = context;
        this.mPackages = packages;
        this.mInflater = LayoutInflater.from(mContext);
        this.mAppList = mContext.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
    }

    @Override
    public int getCount() {
        return mPackages.size();
    }

    @Override
    public IPackage getItem(int position) {
        return mPackages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_list_app, null);
            viewHolder = new ViewHolder();
            viewHolder.appIcon = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.appName = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        IPackage mPackage = mPackages.get(position);
        Drawable icon = null;
        for (PackageInfo info : mAppList) {
            if (info.packageName.equals(mPackage.getName())) {
                icon = info.applicationInfo.loadIcon(mContext.getPackageManager());
            }
        }
        if (icon != null) {
            viewHolder.appIcon.setImageDrawable(icon);
        }
        viewHolder.appName.setText(mPackage.getName());
        return view;
    }

    private static class ViewHolder {
        ImageView appIcon;
        TextView appName;
    }
}
