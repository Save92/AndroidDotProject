package com.sncf.itnovem.dotandroidapplication.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sncf.itnovem.dotandroidapplication.R;

/**
 * Created by Journaud Nicolas on 28/05/16.
 */
public class CustomUserViewHolder extends RecyclerView.ViewHolder {
    protected TextView name;

    protected CustomUserViewHolder custom;

    public CustomUserViewHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.name);
        this.custom = this;

    }
}
