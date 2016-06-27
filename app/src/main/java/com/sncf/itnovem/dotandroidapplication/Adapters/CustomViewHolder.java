package com.sncf.itnovem.dotandroidapplication.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sncf.itnovem.dotandroidapplication.R;

/**
 * Created by Journaud Nicolas on 24/03/16.
 */
public class CustomViewHolder extends RecyclerView.ViewHolder {
    protected TextView textView;
    protected TextView dateInfo;
    protected TextView type;
    protected TextView userName;
    protected ImageView typeLogo;
    protected ImageView displayedLogo;

    protected CustomViewHolder custom;

    public CustomViewHolder(View view) {
        super(view);
        this.textView = (TextView) view.findViewById(R.id.title);
        this.dateInfo = (TextView) view.findViewById(R.id.date);
        this.userName = (TextView) view.findViewById(R.id.userName);
        this.typeLogo = (ImageView) view.findViewById(R.id.typeLogo);
        this.displayedLogo = (ImageView) view.findViewById(R.id.displayedLogo);
        this.custom = this;

    }
}
