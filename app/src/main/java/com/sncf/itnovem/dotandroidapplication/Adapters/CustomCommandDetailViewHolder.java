package com.sncf.itnovem.dotandroidapplication.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sncf.itnovem.dotandroidapplication.R;

/**
 * Created by Journaud Nicolas on 24/03/16.
 */
public class CustomCommandDetailViewHolder extends RecyclerView.ViewHolder {
    protected TextView title;


    protected CustomCommandDetailViewHolder custom;

    public CustomCommandDetailViewHolder(View view) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.title);
        this.custom = this;

    }
}
