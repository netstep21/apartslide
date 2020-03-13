package com.zslide.network;

import com.zslide.models.Faq;
import com.zslide.models.Notice;
import com.zslide.models.PaginationData;
import com.zslide.network.service.NoticeService;

import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class NoticeApi {

    private NoticeService noticeService;

    protected NoticeApi(Retrofit retrofit) {
        noticeService = retrofit.create(NoticeService.class);
    }

    public Observable<Notice> latestNotice() {
        return noticeService.getLatestNotice();
    }

    public Observable<Notice> item(long id) {
        return noticeService.getNotice(id);
    }

    public Observable<PaginationData<Notice>> items(int page) {
        return noticeService.getNotices(page);
    }

    public Observable<Object> suggest(String title, String content) {
        return noticeService.suggest(title, content);
    }

    public Observable<List<Faq.Category>> faqCategories() {
        return noticeService.getFaqCategories();
    }

    public Observable<PaginationData<Faq>> faqs(Faq.Category category, int page) {
        if (category == null) {
            return faqs(page);
        } else {
            return noticeService.getFaq(category.getId(), page);
        }
    }

    public Observable<PaginationData<Faq>> faqs(int page) {
        return noticeService.getFaqAll(page);
    }
}
