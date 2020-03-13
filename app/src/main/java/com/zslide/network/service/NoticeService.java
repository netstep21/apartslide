package com.zslide.network.service;

import com.zslide.models.Faq;
import com.zslide.models.Notice;
import com.zslide.models.PaginationData;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chulwoo on 15. 12. 24..
 * <p>
 * 공지사항 관련 API
 */
public interface NoticeService {

    /**
     * 최근 등록된 공지사항을 가져온다.
     *
     * @return 최근 등록된 공지사항
     */
    @GET("v2/notices/latest_notice/")
    Observable<Notice> getLatestNotice();

    /**
     * 공지사항을 페이지네이션 가능한 형태로 가져온다.
     *
     * @param id 공지사항 아이디
     * @return 아이디가 {@param id}인 공지사항
     */
    @GET("v2/notices/notice/{id}")
    Observable<Notice> getNotice(@Path("id") long id);

    /**
     * 공지사항을 페이지네이션 가능한 형태로 가져온다.
     *
     * @param page 가져올 페이지
     * @return {@param page}에 있는 공지사항
     */
    @GET("v2/notices/notice/")
    Observable<PaginationData<Notice>> getNotices(@Query("page") int page);

    @GET("v2/notices/faq/")
    Observable<PaginationData<Faq>> getFaqAll(@Query("page") int page);

    @GET("v2/notices/faq/")
    Observable<PaginationData<Faq>> getFaq(@Query("category_id") int categoryId, @Query("page") int page);

    @GET("v2/notices/faq_category/")
    Observable<List<Faq.Category>> getFaqCategories();

    @FormUrlEncoded
    @POST("v2/notices/user_suggestion/")
    Observable<Object> suggest(@Field("title") String title, @Field("suggestion") String content);
}
