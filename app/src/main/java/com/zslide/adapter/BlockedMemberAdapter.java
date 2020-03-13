package com.zslide.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zslide.R;
import com.zslide.ZummaApp;
import com.zslide.data.UserManager;
import com.zslide.data.model.User;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.BaseRecyclerView;
import com.zslide.widget.RequestButton;
import com.zslide.widget.UserProfileView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by chulwoo on 15. 8. 13..
 */
public class BlockedMemberAdapter extends BaseRecyclerView.BaseListAdapter<User> {

    private Context context;

    public BlockedMemberAdapter(@NonNull Context context, @NonNull List<User> blockedMembers) {
        super();
        this.context = context;
        addAll(blockedMembers);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blocked_member, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        holder.unblockButton.setAutoProgressing(false);
        return holder;
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        super.onBindContentItemViewHolder(contentViewHolder, position);
        ItemViewHolder holder = (ItemViewHolder) contentViewHolder;
        User user = getItem(position);
        holder.userProfileView.setUser(user);
        holder.unblockButton.action(ZummaApi.user().unblock(user.getId()),
                family -> onSuccessUnblock(user), this::onFailureUnblock);
        holder.unblockButton.setOnClickListener(v -> {
            AppCompatActivity activity = ZummaApp.getCurrentActivity();
            if (activity == null) {
                return;
            }

            SimpleAlertDialog.newInstance(context.getString(R.string.message_confirm_unblock, user.getDisplayName(context)), true)
                    .setOnConfirmListener(holder.unblockButton::request)
                    .show(activity.getSupportFragmentManager(), "dialog");
        });
    }

    private void onSuccessUnblock(User user) {
        UserManager.getInstance().fetchFamily().subscribe();
        int position = getAll().indexOf(user);
        remove(user);
        try {
            notifyItemRemoved(position);
        } catch (Exception e) {
            notifyDataSetChanged();
        }
    }

    private void onFailureUnblock(Throwable e) {
        ZummaApiErrorHandler.handleError(e);
    }

    class ItemViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.userProfile) UserProfileView userProfileView;
        @BindView(R.id.unblock) RequestButton unblockButton;

        ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}