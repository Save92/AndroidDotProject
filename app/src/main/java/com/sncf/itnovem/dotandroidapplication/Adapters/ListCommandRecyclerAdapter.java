package com.sncf.itnovem.dotandroidapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.CommandListDetailFragment;
import com.sncf.itnovem.dotandroidapplication.Models.Commande;
import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.Models.VoiceCommand;
import com.sncf.itnovem.dotandroidapplication.R;

/**
 * Created by Save92 on 13/04/16.
 */
public class ListCommandRecyclerAdapter extends RecyclerView.Adapter<CustomViewHolder>{
    private List<VoiceCommand> commmandeItemList;
    private Context mContext;

    /** declaration du callback */
    private CallbackAdapter callback;

    public ListCommandRecyclerAdapter(Context context, List<VoiceCommand> commmandeItemList) {
        this.commmandeItemList = commmandeItemList;
        this.mContext = context;

        /** initialisation du callback */
        this.callback = (CallbackAdapter) context;
    }

    public interface CallbackAdapter {
        void showDetail(VoiceCommand c);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_command_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        view.setOnClickListener(clickListener);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        VoiceCommand commande = commmandeItemList.get(position);


        //Setting text view title
        holder.textView.setText(Html.fromHtml(commande.getName()));
        holder.textView.setTag(holder);
        holder.itemView.setOnClickListener(clickListener);


    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomViewHolder holder = (CustomViewHolder) view.getTag();

            int position = holder.getPosition();

            VoiceCommand voiceCommand = commmandeItemList.get(position);
            Toast.makeText(mContext, voiceCommand.getName(), Toast.LENGTH_SHORT).show();

            /** declenchement du callback vers l activity parente */
            if (callback != null) {
                // il faut tester avec Log.v(...) si le callback n est pas null
                callback.showDetail(voiceCommand);
            }
        }
    };

    @Override
    public int getItemCount() {
        return (null != commmandeItemList ? commmandeItemList.size() : 0);
    }
}








