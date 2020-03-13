package com.zslide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zslide.R;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.view.base.BaseViewHolder;
import com.zslide.widget.UserProfileView;

import lombok.Setter;

/**
 * Created by chulwoo on 2018. 1. 12..
 */

public class MemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Setter private Family family;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_family_member, parent, false);
        return new UserViewHolder((UserProfileView) view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = family.getMembers().get(position);
        ((UserProfileView) holder.itemView).setUser(user);
    }

    @Override
    public int getItemCount() {
        return family == null ? 0 : family.getMembers().size();
    }

    class UserViewHolder extends BaseViewHolder {

        public UserViewHolder(UserProfileView itemView) {
            super(itemView);
        }
    }

}
