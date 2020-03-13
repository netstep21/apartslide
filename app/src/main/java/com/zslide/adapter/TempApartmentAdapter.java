package com.zslide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.model.Address;
import com.zslide.dialogs.TempApartmentSelectDialog.OnTempApartmentSelectListener;
import com.zslide.models.TempApartment;
import com.zslide.utils.TimeUtil;
import com.zslide.widget.BaseRecyclerView;

import butterknife.BindView;

/**
 * Created by chulwoo on 16. 3. 8..
 */
public class TempApartmentAdapter extends BaseRecyclerView.BaseListAdapter<TempApartment> {

    private Address address;
    private OnTempApartmentSelectListener listener;

    public TempApartmentAdapter(Address address, OnTempApartmentSelectListener listener) {
        super();
        this.address = address;
        this.listener = listener;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        return new TempApartmentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_temp_apartment, parent, false));
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        super.onBindContentItemViewHolder(contentViewHolder, position);
        TempApartment tempApartment = getItem(position);
        TempApartmentViewHolder holder = (TempApartmentViewHolder) contentViewHolder;
        holder.nameView.setText(tempApartment.toString());
        holder.addressView.setText(address.toString());
        holder.countView.setText(
                holder.getContext().getString(R.string.format_apartment_timestamp,
                        TimeUtil.timestamp(tempApartment.getPubDate()), tempApartment.getCount()));
        holder.familyRegistrationButton.setOnClickListener(v ->
                listener.onTempApartmentSelected(tempApartment));
    }

    class TempApartmentViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.name) TextView nameView;
        @BindView(R.id.address) TextView addressView;
        @BindView(R.id.count) TextView countView;
        @BindView(R.id.familyRegistration) Button familyRegistrationButton;

        TempApartmentViewHolder(View itemView) {
            super(itemView);
        }
    }
}
