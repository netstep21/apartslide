package com.zslide.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.model.Address;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 2017. 5. 30..
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    public interface AddressSelectListener {
        void onAddressSelected(Address address);
    }

    private final List<Address> addresses;
    private AddressSelectListener listener;

    public AddressAdapter(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void setOnAddressSelectListener(AddressSelectListener listener) {
        this.listener = listener;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        Address addr = addresses.get(position);
        holder.address.setText(addr.getFullAddress());
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.address) TextView address;

        public AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                Address addr = addresses.get(getAdapterPosition());
                if (listener != null) {
                    listener.onAddressSelected(addr);
                }
            });
        }
    }
}
