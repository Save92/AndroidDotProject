package com.sncf.itnovem.dotandroidapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.NotificationDetailActivity;
import com.sncf.itnovem.dotandroidapplication.R;
import com.sncf.itnovem.dotandroidapplication.Adapters.CustomViewHolder;

import retrofit2.http.PUT;


/**
 * Created by Save92 on 24/03/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<CustomViewHolder>{
    private List<Notification> notificationItemList;
    private Context mContext;

    /** declaration du callback */
    private CallbackAdapter callback;

    public interface CallbackAdapter {
        void showDetail(Notification n);
    }

    public RecyclerAdapter(Context context, List<Notification> notificationItemList) {
        this.notificationItemList = notificationItemList;
        this.mContext = context;
        this.callback = (CallbackAdapter) context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        view.setOnClickListener(clickListener);
        view.setTag(viewHolder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Notification notification = notificationItemList.get(position);
Log.v("Recycler", notification.getTitle());
        //Setting text view title
        holder.textView.setText(Html.fromHtml(notification.getTitle()));
        if(!notification.getDisplayAt().isEmpty()){
            holder.dateInfo.setText(Html.fromHtml(notification.getDisplayAt()));
        } else {
            holder.dateInfo.setText(Html.fromHtml(notification.getCreatedAt()));
        }
//        holder.type.setText(Html.fromHtml(notification.getType()));
        holder.userName.setText(Html.fromHtml(notification.getUser()));

        if(notification.getType().equals("memo")) {
            holder.typeLogo.setImageResource(R.drawable.ic_chat_black_24dp);
        } else if (notification.getType().equals("alert")) {

            holder.typeLogo.setImageResource(R.drawable.ic_notifications_black_24dp);
        }
        if(notification.getDisplayed()) {
            holder.displayedLogo.setImageResource(R.drawable.ic_eye_black_24dp);
        } else  {
            holder.displayedLogo.setImageResource(R.drawable.ic_eye_off_black_24dp);
        }
        holder.textView.setTag(holder);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomViewHolder holder = (CustomViewHolder) view.getTag();
            int position = holder.getPosition();

            Notification notif = notificationItemList.get(position);

            Toast.makeText(mContext, notif.getTitle(), Toast.LENGTH_SHORT).show();
            if (callback != null) {
                // il faut tester avec Log.v(...) si le callback n est pas null
                callback.showDetail(notif);
            }
//            Intent myIntent = new Intent(this, NotificationDetailActivity.class);
//            myIntent.putExtra("firstKeyName","FirstKeyValue");
//            myIntent.putExtra("secondKeyName","SecondKeyValue");
//            startActivity(myIntent);
        }
    };

    @Override
    public int getItemCount() {
        return (null != notificationItemList ? notificationItemList.size() : 0);
    }

}


