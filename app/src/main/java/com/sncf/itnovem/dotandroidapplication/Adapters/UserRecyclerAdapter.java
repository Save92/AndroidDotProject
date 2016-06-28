package com.sncf.itnovem.dotandroidapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.R;

import java.util.List;

/**
 * Created by Journaud Nicolas on 27/05/16.
 */
public class UserRecyclerAdapter  extends RecyclerView.Adapter<CustomUserViewHolder> {
    private List<User> userItemList;
    private Context mContext;

    /** declaration du callback */
    private CallbackAdapter callback;

    public UserRecyclerAdapter(Context context, List<User> userItemList) {
        this.userItemList = userItemList;
        this.mContext = context;

        /** initialisation du callback */
        this.callback = (CallbackAdapter) context;
    }

    public interface CallbackAdapter {
        void showDetail(User u);
    }

    @Override
    public CustomUserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_user_row, null);

        CustomUserViewHolder viewHolder = new CustomUserViewHolder(view);
        view.setOnClickListener(clickListener);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomUserViewHolder holder, int position) {
        User user = userItemList.get(position);

        //Setting text view title
        holder.name.setText(Html.fromHtml(user.getFirstname()));
        holder.name.setTag(holder);
        holder.itemView.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomUserViewHolder holder = (CustomUserViewHolder) view.getTag();

            int position = holder.getPosition();
            User user = userItemList.get(position);

            /** declenchement du callback vers l activity parente */
            if (callback != null) {
                callback.showDetail(user);
            }
        }
    };

    @Override
    public int getItemCount() {
        return (null != userItemList ? userItemList.size() : 0);
    }
}
