package com.example.rauber.lixotec.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.R;

import java.util.ArrayList;
import java.util.List;

public class FreeTimeAdapter extends RecyclerView.Adapter<FreeTimeAdapter.FreeTimeHolder> {

    private List<String> freeTimes = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public FreeTimeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FreeTimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_time, viewGroup, false);
        FreeTimeHolder holder = new FreeTimeHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FreeTimeHolder freeTimeHolder, int i) {
        if (freeTimes.size() > 0) {
            String hourMinute = freeTimes.get(i);
            freeTimeHolder.freeTimeButton.setText(hourMinute);
        }
    }

    @Override
    public int getItemCount() {
        return freeTimes.size();
    }

    public void setFreeTimes(List<String> freeTimes) {
        this.freeTimes = freeTimes;
        notifyDataSetChanged();
    }


    class FreeTimeHolder extends RecyclerView.ViewHolder {
        private TextView freeTimeButton;

        public FreeTimeHolder(@NonNull View itemView) {
            super(itemView);
            freeTimeButton = itemView.findViewById(R.id.ButtonFreeTime);

            itemView.setOnClickListener((View v) -> {

                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(freeTimes.get(position));

                    }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String String);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
