package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.models.Notice;
import com.zslide.network.ZummaApi;
import com.zslide.widget.HtmlContentView;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by chulwoo on 2017. 9. 29..
 */

public class NoticeActivity extends BaseLoadingActivity<Notice> {

    @BindView(R.id.content) HtmlContentView contentView;

    private long id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getLongExtra(IntentConstants.EXTRA_ID, -1);
        if (id == -1) {
            Toast.makeText(this, R.string.message_invalid, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected Observable<Notice> getData() {
        return ZummaApi.notice().item(id);
    }

    @Override
    protected void onSuccessLoading(Notice notice) {
        setToolbarTitle(notice.getTitle());
        contentView.setHtml(notice.getContent());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_notice;
    }
}
