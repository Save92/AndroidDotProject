package com.sncf.itnovem.dotandroidapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sncf.itnovem.dotandroidapplication.Models.VoiceCommand;
import com.sncf.itnovem.dotandroidapplication.R;

import java.util.List;

/**
 * Created by Journaud Nicolas on 13/04/16.
 */
public class ListCommandDetailRecyclerAdapter extends RecyclerView.Adapter<CustomCommandDetailViewHolder>{
    private List<String> commmandeItemList;
    private Context mContext;

    /** declaration du callback */
    private CallbackAdapter callback;

    public ListCommandDetailRecyclerAdapter(Context context, List<String> commmandeItemList) {
        this.commmandeItemList = commmandeItemList;
        this.mContext = context;
    }

    public interface CallbackAdapter {
        void showDetail(VoiceCommand c);
    }

    @Override
    public CustomCommandDetailViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.line_vocal_command_detail_row, null);

        CustomCommandDetailViewHolder viewHolder = new CustomCommandDetailViewHolder(view);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomCommandDetailViewHolder holder, int position) {
        String commande = commmandeItemList.get(position);
        //Setting text view title
        holder.title.setText(Html.fromHtml(commande));
        holder.title.setTag(holder);
    }


    @Override
    public int getItemCount() {
        return (null != commmandeItemList ? commmandeItemList.size() : 0);
    }
}








