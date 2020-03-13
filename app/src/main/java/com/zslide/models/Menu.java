package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.LongSparseArray;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chulwoo on 2017. 5. 17..
 * <p>
 * todo null 체크 안해도 되도록 Gson converter에서 작업해야 함
 */

public class Menu implements Parcelable {

    @SerializedName("id") long id;
    @SerializedName("image_url") String imageUrl;
    @SerializedName("price") int price;
    @SerializedName("content") String title;
    @SerializedName("description") String subtitle;
    @SerializedName("is_hot") boolean isPopular;
    @SerializedName("menuoptions") List<Option> options;
    private HashMap<Option, LongSparseArray<Option.Value>> selectedValues;
    private int count = 1;

    public Menu() {

    }

    protected Menu(Parcel in) {
        id = in.readLong();
        imageUrl = in.readString();
        price = in.readInt();
        title = in.readString();
        subtitle = in.readString();
        isPopular = in.readByte() != 0;
        options = in.createTypedArrayList(Option.CREATOR);
        count = in.readInt();
        int selectedOptionCount = in.readInt();
        selectedValues = new HashMap<>();
        for (int i = 0; i < selectedOptionCount; i++) {
            Menu.Option option = in.readParcelable(Option.class.getClassLoader());
            LongSparseArray<Option.Value> values = new LongSparseArray<>();
            ArrayList<Option.Value> valueList = in.createTypedArrayList(Option.Value.CREATOR);
            for (Option.Value value : valueList) {
                values.put(value.getId(), value);
            }
            selectedValues.put(option, values);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(imageUrl);
        dest.writeInt(price);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeByte((byte) (isPopular ? 1 : 0));
        dest.writeTypedList(options);
        dest.writeInt(count);
        if (selectedValues == null) {
            selectedValues = new HashMap<>();
        }
        dest.writeInt(selectedValues.size());
        for (Map.Entry<Option, LongSparseArray<Option.Value>> entry : selectedValues.entrySet()) {
            dest.writeParcelable(entry.getKey(), flags);
            LongSparseArray<Option.Value> values = entry.getValue();
            ArrayList<Option.Value> valueList = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                valueList.add(values.valueAt(i));
            }
            dest.writeTypedList(valueList);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    public Menu newInstance() {
        Menu menu = new Menu();
        menu.id = id;
        menu.imageUrl = imageUrl;
        menu.price = price;
        menu.title = title;
        menu.subtitle = subtitle;
        menu.isPopular = isPopular;
        menu.options = new ArrayList<>();
        menu.selectedValues = new HashMap<>();
        for (Option option : options) {
            Option newOption = option.newInstance();
            menu.selectedValues.put(newOption, newOption.getSelectedValues());
            menu.options.add(newOption);
            if (option.isRequired() && newOption.getValues().size() > 0) {
                menu.selectValue(newOption.getValues().get(0));
            }
        }

        menu.count = 1;
        return menu;
    }

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isPopular() {
        return isPopular;
    }

    private Option getOptionAt(Option.Value value) {
        Option valueOption = null;
        for (Option option : options) {
            if (option.getValues().contains(value)) {
                valueOption = option;
                break;
            }
        }

        return valueOption;
    }

    public void selectValue(Option.Value value) {
        Option valueOption = getOptionAt(value);
        if (valueOption == null) {
            throw new IllegalArgumentException("does not exists option(" + value.getName() + ") in this menu");
        }

        valueOption.selectValue(value);
    }

    public void unselectValue(Option.Value value) {
        Option valueOption = getOptionAt(value);
        if (valueOption == null) {
            throw new IllegalArgumentException("does not exists option(" + value.getName() + ") in this menu");
        }

        valueOption.unselectValue(value);
    }

    public LongSparseArray<Option.Value> getSelectedValues(Option option) {
        return getSelectedValues().get(option);
    }

    public void setSelectedValues(Option option) {
        getSelectedValues().put(option, option.selectedValues);
    }

    public HashMap<Option, LongSparseArray<Option.Value>> getSelectedValues() {
        if (selectedValues == null) {
            selectedValues = new HashMap<>();
        }
        return selectedValues;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        this.count++;
    }

    public void decreaseCount() {
        this.count--;
    }

    public int getTotalPrice() {
        int optionPrice = 0;
        for (Option option : options) {
            optionPrice += option.getTotalPrice();
        }
        return (price + optionPrice) * count;
    }

    public List<Option> getOptions() {
        return options;
    }

    @Override
    public int hashCode() {
        int result = 31;
        result += (17 * result) + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Menu)) {
            return false;
        }

        Menu menu = (Menu) obj;
        return this.id == menu.id;
    }

    public static class Option implements Parcelable {

        public static final int SELECT_TYPE_SINGLE = 0;
        public static final int SELECT_TYPE_MULTIPLE = 1;
        public static final int SELECT_TYPE_COUNT = 2;

        @SerializedName("id") long id;
        @SerializedName("name") String name;
        @SerializedName("required") boolean required;
        @SerializedName("menudetails") List<Value> values;
        @SerializedName("multi_select") boolean canMultipleItem;
        // @SerializedName("value_select_type") int valueSelectType;
        private LongSparseArray<Value> selectedValues;

        public Option() {

        }

        protected Option(Parcel in) {
            id = in.readLong();
            name = in.readString();
            required = in.readByte() != 0;
            values = in.createTypedArrayList(Value.CREATOR);
            canMultipleItem = in.readByte() != 0;
            //valueSelectType = in.readInt();
            List<Value> valueList = in.createTypedArrayList(Value.CREATOR);
            selectedValues = new LongSparseArray<>();
            for (Value value : valueList) {
                selectedValues.put(value.getId(), value);
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeByte((byte) (required ? 1 : 0));
            dest.writeTypedList(values);
            dest.writeByte((byte) (canMultipleItem ? 1 : 0));
            //dest.writeInt(valueSelectType);
            ArrayList<Value> valueList = new ArrayList<>();
            if (selectedValues == null) {
                selectedValues = new LongSparseArray<>();
            }
            for (int i = 0; i < selectedValues.size(); i++) {
                valueList.add(selectedValues.valueAt(i));
            }
            dest.writeTypedList(valueList);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Option> CREATOR = new Creator<Option>() {
            @Override
            public Option createFromParcel(Parcel in) {
                return new Option(in);
            }

            @Override
            public Option[] newArray(int size) {
                return new Option[size];
            }
        };

        private void validateValue(Value value) {
            if (!values.contains(value) && !Value.NO_SELECT.equals(value)) {
                throw new IllegalArgumentException("does not exists option(" + value.getName() + ") in this menu");
            }
        }

        public void selectValue(Value value) {
            // todo: valueSelectType 필드 적용시 제거
            int valueSelectType = getValueSelectType();
            if (valueSelectType == SELECT_TYPE_SINGLE) {
                validateValue(value);
                LongSparseArray<Value> selectedValues = getSelectedValues();
                selectedValues.clear();

                Value copyValue = value.newInstance();
                copyValue.setCount(1);
                selectedValues.put(value.getId(), copyValue);
            } else if (valueSelectType == SELECT_TYPE_MULTIPLE) {
                validateValue(value);
                LongSparseArray<Value> selectedValues = getSelectedValues();
                Value copyValue = value.newInstance();
                copyValue.setCount(1);
                selectedValues.put(value.getId(), copyValue);
            } else if (valueSelectType == SELECT_TYPE_COUNT) {
                // TODO: 2017. 6. 2.
            }
        }

        public void unselectValue(Value value) {
            // todo: valueSelectType 필드 적용시 제거
            int valueSelectType = getValueSelectType();
            if (valueSelectType == SELECT_TYPE_SINGLE) {
                // single select type can not unselect
            } else if (valueSelectType == SELECT_TYPE_MULTIPLE) {
                validateValue(value);
                LongSparseArray<Value> selectedValues = getSelectedValues();
                selectedValues.remove(value.getId());
            } else if (valueSelectType == SELECT_TYPE_COUNT) {
                // TODO: 2017. 6. 2.
            }
        }

        public LongSparseArray<Value> getSelectedValues() {
            if (selectedValues == null) {
                selectedValues = new LongSparseArray<>();
            }
            return selectedValues;
        }

        public boolean isSelected(Value value) {
            return getSelectedValues().indexOfKey(value.getId()) >= 0;
        }

        public Option newInstance() {
            Option option = new Option();
            option.id = id;
            option.name = name;
            option.required = required;
            option.values = new ArrayList<>();
            for (Value value : values) {
                option.values.add(value.newInstance());
            }
            option.canMultipleItem = canMultipleItem;
            option.selectedValues = new LongSparseArray<>();
            return option;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isRequired() {
            return required;
        }

        public List<Value> getValues() {
            return values;
        }

        public int getTotalPrice() {
            // todo: valueSelectType 필드 적용시 제거
            int valueSelectType = getValueSelectType();

            int totalPrice = 0;
            for (int i = 0; i < selectedValues.size(); i++) {
                Value value = selectedValues.valueAt(i);
                switch (valueSelectType) {
                    case SELECT_TYPE_SINGLE:
                    case SELECT_TYPE_MULTIPLE:
                        totalPrice += value.getPrice();
                        break;
                    case SELECT_TYPE_COUNT:
                        totalPrice += (value.getPrice() + value.getCount());
                        break;
                }
            }
            return totalPrice;
        }

        public int getValueSelectType() {
            return canMultipleItem ? SELECT_TYPE_MULTIPLE : SELECT_TYPE_SINGLE;
            //todo: return valueSelectType;, 현재 필드 없음
        }

        @Override
        public int hashCode() {
            int result = 31;
            result += (17 * result) + id;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof Option)) {
                return false;
            }

            Option menu = (Option) obj;
            return this.id == menu.id;
        }

        public void setSelectedValues(LongSparseArray<Value> selectedValues) {
            this.selectedValues = selectedValues;
        }

        public static class Value implements Parcelable {

            public static Value NO_SELECT;

            static {
                NO_SELECT = new Value();
                NO_SELECT.id = -1;
                NO_SELECT.name = "선택 안함";
                NO_SELECT.price = 0;
            }

            @SerializedName("id") long id;
            @SerializedName("content") String name;
            @SerializedName("price") int price;
            int count;

            public Value() {

            }

            protected Value(Parcel in) {
                id = in.readLong();
                name = in.readString();
                price = in.readInt();
                count = in.readInt();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeLong(id);
                dest.writeString(name);
                dest.writeInt(price);
                dest.writeInt(count);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<Value> CREATOR = new Creator<Value>() {
                @Override
                public Value createFromParcel(Parcel in) {
                    return new Value(in);
                }

                @Override
                public Value[] newArray(int size) {
                    return new Value[size];
                }
            };

            public Value newInstance() {
                Value value = new Value();
                value.id = id;
                value.name = name;
                value.price = price;
                value.count = 0;
                return value;
            }

            public long getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public int getPrice() {
                return price;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            @Override
            public int hashCode() {
                int result = 31;
                result += (17 * result) + id;
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }

                if (!(obj instanceof Value)) {
                    return false;
                }

                Value menu = (Value) obj;
                return this.id == menu.id;
            }
        }
    }
}
