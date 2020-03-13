package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;
import com.zslide.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulwoo on 16. 2. 12..
 * <p>
 * todo: 방문, 배달 적립/사용 방식에 따라 다시 정리(Usage, Reward 값)
 */
public class ZummaStore extends ApiData implements Parcelable {

    public static final int MAX_RECENT_REVIEW_SIZE = 5;

    public enum Type {
        VISIT,
        DELIVERY;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    @SerializedName("id") private long id;
    @SerializedName("logo_image_url") private String logoImageUrl;
    @SerializedName("title") private String name;
    @SerializedName("like_count") private int likeCount;
    @SerializedName("is_liked") private boolean isLike;
    @SerializedName("order_count") private int orderCount;
    @SerializedName("review_count") private int reviewCount;
    @SerializedName("business_hours") private List<String> businessHours;//[0] 평일, [1] 휴일
    @SerializedName("redirect_url") private String redirectUrl;
    @SerializedName("detail_main_image_url") private String detailMainImageUrl;
    @SerializedName("detail_image_urls") private List<String> detailImageUrls;
    @SerializedName("latitude") private double lat;
    @SerializedName("longitude") private double lng;
    @SerializedName("shop_address") private String shopAddress;
    @SerializedName("how_to_find") private String howToFind;
    @SerializedName("tellink_number") private String telLinkNumber;
    @SerializedName("usages") private List<Usage> usages;
    @SerializedName("sale_extra") private String saleExtra;
    @SerializedName("events") private List<String> events;
    @SerializedName("description") private String description;
    @SerializedName("rating") private float rating;

    // delivery attributes
    @SerializedName("use_menu_system") private boolean useMenuSystem;
    @SerializedName("use_payment_system") private boolean usePaymentSystem;
    @SerializedName("category") private int category;
    @SerializedName("small_category") private String detailCategory;
    @SerializedName("menucategories") private List<MenuCategory> menuCategories;
    MenuCategory popularMenuCategory;
    @SerializedName("dongs") private List<Long> dongs;
    @SerializedName("delivery_area") private String deliveryArea;
    @SerializedName("minimum_order_amount") private int minimumPayments;
    @SerializedName("is_business_hour") private boolean isBusinessHour;
    @SerializedName("origin") private String origin;
    private AvailableAddresses deliveryAvailableAddresses;

    protected ZummaStore(Parcel in) {
        id = in.readLong();
        logoImageUrl = in.readString();
        name = in.readString();
        likeCount = in.readInt();
        isLike = in.readByte() != 0;
        orderCount = in.readInt();
        reviewCount = in.readInt();
        businessHours = in.createStringArrayList();
        redirectUrl = in.readString();
        detailMainImageUrl = in.readString();
        detailImageUrls = in.createStringArrayList();
        lat = in.readDouble();
        lng = in.readDouble();
        shopAddress = in.readString();
        howToFind = in.readString();
        telLinkNumber = in.readString();
        usages = in.createTypedArrayList(Usage.CREATOR);
        saleExtra = in.readString();
        events = in.createStringArrayList();
        description = in.readString();
        rating = in.readFloat();
        useMenuSystem = in.readByte() != 0;
        usePaymentSystem = in.readByte() != 0;
        category = in.readInt();
        detailCategory = in.readString();
        menuCategories = in.createTypedArrayList(MenuCategory.CREATOR);
        popularMenuCategory = in.readParcelable(MenuCategory.class.getClassLoader());
        deliveryAvailableAddresses = in.readParcelable(AvailableAddresses.class.getClassLoader());
        dongs = deliveryAvailableAddresses.asList();
        deliveryArea = in.readString();
        minimumPayments = in.readInt();
        isBusinessHour = in.readByte() != 0;
        origin = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(logoImageUrl);
        dest.writeString(name);
        dest.writeInt(likeCount);
        dest.writeByte((byte) (isLike ? 1 : 0));
        dest.writeInt(orderCount);
        dest.writeInt(reviewCount);
        dest.writeStringList(businessHours);
        dest.writeString(redirectUrl);
        dest.writeString(detailMainImageUrl);
        dest.writeStringList(detailImageUrls);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(shopAddress);
        dest.writeString(howToFind);
        dest.writeString(telLinkNumber);
        dest.writeTypedList(usages);
        dest.writeString(saleExtra);
        dest.writeStringList(events);
        dest.writeString(description);
        dest.writeFloat(rating);
        dest.writeByte((byte) (useMenuSystem ? 1 : 0));
        dest.writeByte((byte) (usePaymentSystem ? 1 : 0));
        dest.writeInt(category);
        dest.writeString(detailCategory);
        dest.writeTypedList(menuCategories);
        if (menuCategories != null && popularMenuCategory == null) {
            popularMenuCategory = createPopularMenuCategory();
        }
        dest.writeParcelable(popularMenuCategory, flags);
        if (dongs == null) {
            dongs = new ArrayList<>();
        }
        if (deliveryAvailableAddresses == null) {
            deliveryAvailableAddresses = new AvailableAddresses(dongs);
        }
        dest.writeParcelable(deliveryAvailableAddresses, flags);
        dest.writeString(deliveryArea);
        dest.writeInt(minimumPayments);
        dest.writeByte((byte) (isBusinessHour ? 1 : 0));
        dest.writeString(origin);
    }

    public static final Creator<ZummaStore> CREATOR = new Creator<ZummaStore>() {
        @Override
        public ZummaStore createFromParcel(Parcel in) {
            return new ZummaStore(in);
        }

        @Override
        public ZummaStore[] newArray(int size) {
            return new ZummaStore[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public String getName() {
        return name;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        this.isLike = like;
    }

    public void like() {
        likeCount++;
        isLike = true;
    }

    public void unlike() {
        likeCount--;
        isLike = false;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void increaseReviewCount() {
        reviewCount++;
    }

    public void decreaseReviewCount() {
        reviewCount--;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<String> getBusinessHours() {
        return businessHours;
    }

    public boolean isBusinessHours() {
        /*if (!usePaymentSystem) {
            return true;
        }

        if (businessHours == null ||
                (TextUtils.isEmpty(businessHours.get(0)) &&
                        TextUtils.isEmpty(businessHours.get(1)))) {
            return false;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String[] times;
            if (dayOfWeek == Calendar.SUNDAY ||
                    dayOfWeek == Calendar.SATURDAY) {
                times = businessHours.get(1).split(" - ");
            } else {
                times = businessHours.get(0).split(" - ");
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            Date start = format.parse(times[0]);
            startCalendar.setTime(start);
            Date end = format.parse(times[1]);
            endCalendar.setTime(end);
            startCalendar.set(year, month, date);
            endCalendar.set(year, month, date);

            if (endCalendar.before(startCalendar)) {
                endCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            start = startCalendar.getTime();
            end = endCalendar.getTime();

            return now.compareTo(start) >= 0 && now.compareTo(end) <= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;*/

        return isBusinessHour;
   }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getDetailMainImageUrl() {
        return detailMainImageUrl;
    }

    public List<String> getDetailImageUrls() {
        return detailImageUrls;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public String getHowToFind() {
        return howToFind;
    }

    public String getTelLinkNumber() {
        return telLinkNumber;
    }

    public List<Usage> getUsages() {
        return usages;
    }

    public String getSaleExtra() {
        return saleExtra;
    }

    public List<String> getEvents() {
        return events;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public int getCategory() {
        return category;
    }

    public String getDetailCategory() {
        return detailCategory;
    }

    public boolean isUseMenuSystem() {
        return useMenuSystem;
    }

    public boolean isUsePaymentSystem() {
        return usePaymentSystem;
    }

    public MenuCategory getPopularMenuCategory() {
        if (popularMenuCategory == null) {
            popularMenuCategory = createPopularMenuCategory();
        }

        return popularMenuCategory;
    }

    private MenuCategory createPopularMenuCategory() {
        ArrayList<Menu> popularMenuList = new ArrayList<>();
        for (MenuCategory menuCategory : getMenuCategories()) {
            for (Menu menu : menuCategory.getMenuList()) {
                if (menu.isPopular()) {
                    popularMenuList.add(menu);
                }
            }
        }

        return new MenuCategory(-1L, "인기 메뉴", "", popularMenuList);
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    private List<Long> getDongs() {
        if (dongs == null) {
            dongs = new ArrayList<>();
        }
        return dongs;
    }

    public AvailableAddresses getDeliveryAvailableAddresses() {
        if (deliveryAvailableAddresses == null) {
            deliveryAvailableAddresses = new AvailableAddresses(getDongs());
        }
        return deliveryAvailableAddresses;
    }

    public String getDeliveryArea() {
        return deliveryArea;
    }

    public int getMinimumPayments() {
        return minimumPayments;
    }

    public String getOrigin() {
        return origin;
    }

    public Type getType() {
        Usage usage = usages.get(0);
        switch (usage.getType()) {
            case Usage.TYPE_VISIT:
                return Type.VISIT;
            case Usage.TYPE_ALL:
            case Usage.TYPE_DELIVERY:
            default:
                return Type.DELIVERY;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ZummaStore)) return false;
        ZummaStore zummaStore = (ZummaStore) obj;
        return zummaStore.id == id;

    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public boolean hasLocation() {
        return lat != 0L && lng != 0L;
    }

    /**
     * 방문가게, 배달가게 분리되면서 크게 구분 필요 없음
     */
    @Deprecated
    public static class Usage implements Parcelable {
        public static final int TYPE_VISIT = 0x00000001;
        public static final int TYPE_DELIVERY = 0x00000002;
        public static final int TYPE_ALL = TYPE_VISIT | TYPE_DELIVERY;

        @SerializedName("type") int type;
        @SerializedName("reward") Reward reward;

        protected Usage(Parcel in) {
            type = in.readInt();
            reward = in.readParcelable(Reward.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(type);
            dest.writeParcelable(reward, flags);
        }

        public static final Creator<Usage> CREATOR = new Creator<Usage>() {
            @Override
            public Usage createFromParcel(Parcel in) {
                return new Usage(in);
            }

            @Override
            public Usage[] newArray(int size) {
                return new Usage[size];
            }
        };

        public int getType() {
            return type;
        }

        public boolean hasReward() {
            return reward != null;
        }

        public Reward getReward() {
            return reward;
        }

        public int getTypeIndex() {
            return (int) (Math.log(type) / Math.log(2));
        }

        @Override
        public int describeContents() {
            return hashCode();
        }
    }

    public static class Category implements Parcelable {

        // -1: 기타
        public static final Category ALL = new Category(0, "전체", 0);

        @SerializedName("id") private long id;
        @SerializedName("name") private String name;
        @SerializedName("count") private int count;

        @DrawableRes private int imageRes; // todo

        public Category(int id, String name, int imageRes) {
            this.id = id;
            this.name = name;
            this.imageRes = imageRes;
        }

        public static List<Category> visitCategories() {
            // TODO: 2017. 8. 8. id 정리, -> arrays.xml

            ArrayList<Category> items = new ArrayList<>();
            items.add(new Category(20, "맛집", R.drawable.img_c_s_food));
            items.add(new Category(21, "생활/편의", R.drawable.img_c_s_apt));
            items.add(new Category(24, "식품", R.drawable.img_c_s_fish));
            items.add(new Category(26, "홈/데코", R.drawable.img_c_s_curtain));
            items.add(new Category(25, "패션/뷰티", R.drawable.img_c_s_t));
            items.add(new Category(22, "가전/디지털", R.drawable.img_c_s_phone));
            items.add(new Category(23, "레포츠/자동차", R.drawable.img_c_s_car));
            items.add(new Category(27, "기타", R.drawable.img_c_s_etc));

            return items;
        }

        public static List<Category> deliveryCategories() {
            // TODO: 2017. 8. 8. id 정리, -> arrays.xml

            ArrayList<Category> items = new ArrayList<>();
            items.add(new Category(-1, "전체", R.drawable.ic_all));
            items.add(new Category(1, "치킨", R.drawable.img_c_d_chken));
            items.add(new Category(4, "피자", R.drawable.img_c_d_pzz));
            items.add(new Category(9, "중식", R.drawable.img_c_d_china));
            items.add(new Category(2, "한식/분식", R.drawable.img_c_d_ko));
            items.add(new Category(5, "족발/보쌈", R.drawable.img_c_d_pig));
            items.add(new Category(6, "돈까스/일식", R.drawable.img_c_d_jp));
            items.add(new Category(46, "죽/도시락", R.drawable.img_c_d_soup));
            items.add(new Category(47, "패스트푸드", R.drawable.img_c_d_fast));
            items.add(new Category(8, "야식", R.drawable.img_c_d_moon));
            items.add(new Category(7, "찜/탕", R.drawable.img_c_d_steam));
            items.add(new Category(10, "기타", R.drawable.img_c_d_etc));

            return items;
        }

        protected Category(Parcel in) {
            id = in.readLong();
            name = in.readString();
            count = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeInt(count);
        }

        public static final Creator<Category> CREATOR = new Creator<Category>() {
            @Override
            public Category createFromParcel(Parcel in) {
                return new Category(in);
            }

            @Override
            public Category[] newArray(int size) {
                return new Category[size];
            }
        };

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getImageResource() {
            return imageRes;
        }

        @Override
        public int describeContents() {
            return hashCode();
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof Category)) return false;

            Category category = (Category) obj;
            return id == category.id;
        }

        @Override
        public int hashCode() {
            int result = 31;
            result = result * 17 + (int) id;

            return result;
        }
    }
}
