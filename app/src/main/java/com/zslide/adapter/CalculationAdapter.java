package com.zslide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.model.Payments;
import com.zslide.models.Account;

import org.threeten.bp.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chulwoo on 2017. 9. 12..
 */

public class CalculationAdapter extends PaginationAdapter<Payments> {

    public CalculationAdapter(Loader<Payments> loader) {
        super(loader);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CalculationHolder holder = (CalculationHolder) viewHolder;
        Context context = holder.itemView.getContext();
        Payments payments = getItem(position);
        holder.date.setText(payments.getDate().format(holder.format));
        holder.status.setText(payments.getState().getDisplayLabel(context));
        holder.payments.setText(context.getString(R.string.format_price, payments.getExpectedPayments()));
        switch (payments.getState()) {
            case ZUMMA_APART:
                holder.message.setText("관리비 청구서에서 확인하세요.");
                break;
            case APART:
            case OTHER:
                Account account = payments.getAccount();
                if (account != null && !account.isNull()) {
                    holder.message.setText(context.getString(R.string.format_bank_account,
                            account.getBank(), account.getBlurredAccount(), account.getBlurredOwner()));
                }
                break;
            default:
                holder.message.setText(payments.getCarryingReason());
        }
        holder.totalPayments.setText(context.getString(R.string.format_price, payments.getTotalPayments()));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calculation, parent, false);
        return new CalculationHolder(view);
    }


    class CalculationHolder extends RecyclerView.ViewHolder {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy. MM.");

        @BindView(R.id.date) TextView date;
        @BindView(R.id.status) TextView status;
        @BindView(R.id.payments) TextView payments;
        @BindView(R.id.message) TextView message;
        @BindView(R.id.totalPayments) TextView totalPayments;

        public CalculationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
