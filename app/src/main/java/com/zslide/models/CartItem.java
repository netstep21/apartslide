package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * {@link com.mobitle.zmoney.managers.CartManager}를 통해  관리되며,
 * 아이템 정보와 함께 갯수를 저장한다.
 */
public class CartItem extends ZummaApiData implements Parcelable {

    /**
     * 이 값 이상일 경우 무료배송으로 판단
     */
    public static final int FREE_DELIVERY_PRICE = 25000;
    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
    @SerializedName("id") private int mId;
    /**
     * 아이템 개수
     */
    @SerializedName("count") private int mCount;
    /**
     * 아이템
     */
    @SerializedName("ad") private MarketItem mMarketItem;
    /**
     * {@link #setForceFreeDelivery(boolean)} 메소드를 통해 설정 가능하며,
     * true일 경우 저장된 배송비와 상관 없이 배송비는 0원이다.
     */
    private boolean mForceFreeDelivery;

    public CartItem(int count, MarketItem marketItem) {
        mCount = count;
        mMarketItem = marketItem;
    }

    protected CartItem(Parcel src) {
        readFromParcel(src);
    }

    @Override
    public int hashCode() {
        return (int) mMarketItem.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CartItem) {
            return mMarketItem.getId() == ((CartItem) o).getMarketItem().getId();
        }

        return false;
    }

    public int getId() {
        return mId;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public MarketItem getMarketItem() {
        return mMarketItem;
    }

    /**
     * 배송비를 제외한 순수 아이템 가격, 개수에 따라 증가한다.
     *
     * @return 계산된 아이템 가격
     */
    public int getTotalItemPrice() {
        return getMarketItem().getPrice() * mCount;
    }

    /**
     * 아이템을 구매할 때 적립되는 줌머니, 개수에 따라 증가한다.
     *
     * @return 계산된 줌머니
     */
    public int getTotalDeductionPrice() {
        return getMarketItem().getDeductionPrice() * mCount;
    }

    /**
     * 가격이 {@link CartItem#FREE_DELIVERY_PRICE} 이상이 아니더라도 배송비를 0원으로 계산하도록 할지 설정한다.
     *
     * @param force 강제 무료배송 설정 여부
     */
    public void setForceFreeDelivery(boolean force) {
        mForceFreeDelivery = force;
    }

    /**
     * 무료배송인지 아닌지 알려준다.
     * 순수 아이탬 금액이 {@link #FREE_DELIVERY_PRICE} 이상일 경우, 혹은 {@link #mForceFreeDelivery}가 true로 설정되어 있을 경우 무료배송이다.
     *
     * @return 무료배송 여부
     */
    public boolean isFreeDelivery() {
        return getTotalItemPrice() >= FREE_DELIVERY_PRICE || mForceFreeDelivery;
    }

    /**
     * @return 무료배송인 경우 0, 아닐 경우 아이템의 배송비
     */
    public int getDeliveryPrice() {
        return (isFreeDelivery()) ? 0 : getMarketItem().getDeliveryPrice();
    }

    /**
     * @return 배송비와 아이템 가격을 합친 금액
     */
    public int getTotalPrice() {
        return getTotalItemPrice() + getDeliveryPrice();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public void readFromParcel(Parcel src) {
        mId = src.readInt();
        mCount = src.readInt();
        mMarketItem = src.readParcelable(MarketItem.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mCount);
        dest.writeParcelable(mMarketItem, flags);
    }
}
