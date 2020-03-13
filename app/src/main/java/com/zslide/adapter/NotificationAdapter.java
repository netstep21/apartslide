package com.zslide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.models.Notification;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.TimeUtil;
import com.zslide.widget.EmptyView;
import com.zslide.widget.BaseRecyclerView;
import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 3. 15..
 */
public class NotificationAdapter extends PaginationAdapter<Notification> {

    public NotificationAdapter(Loader<Notification> loader, RequestManager glideRequestManager) {
        super(loader);
        setGlideRequestManager(glideRequestManager);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Notification notification = getItem(position);
        NotificationViewHolder holder = (NotificationViewHolder) viewHolder;
        glide().load(notification.getThumbnailUrl())
                .bitmapTransform(new CropCircleTransformation(holder.getContext()))
                .into(holder.thumbnailView);
        holder.titleView.setText(notification.getTitle());
        holder.descriptionView.setText(TimeUtil.timestamp(notification.getDate()));
        holder.container.setBackgroundResource(notification.isAlreadyRead()
                ? R.color.white : R.color.notification_unread_background);
        holder.container.setOnClickListener(v -> {
            /*EventLogger.startActivityWithUrl(SettingItemAdapter.getContext(), notification.getDetail());


            public static void startActivityWithUrl(Context context, String url) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    context.startActivity(intent);
                } catch (URISyntaxException e) {
                    ZLog.e(e);
                }
            }*/
            ZummaApi.general().readNotification(notification.getId())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(n -> {
                        n.setRead();
                        notifyItemChanged(holder.getAdapterPosition());
                    }, ZummaApiErrorHandler::handleError);
        });
    }

    @Override
    protected View createEmptyView(Context context) {
        EmptyView emptyView = new EmptyView(context);
        emptyView.setMessage(context.getString(R.string.message_empty_notification));
        emptyView.setButtonVisibility(View.GONE);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        return emptyView;
    }

    class NotificationViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.container) View container;
        @BindView(R.id.thumbnail) ImageView thumbnailView;
        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.description) TextView descriptionView;

        NotificationViewHolder(View itemView) {
            super(itemView);
        }


    }
}
