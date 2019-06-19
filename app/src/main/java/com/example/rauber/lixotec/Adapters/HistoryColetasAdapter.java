package com.example.rauber.lixotec.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rauber.lixotec.Model.Coleta;
import com.example.rauber.lixotec.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryColetasAdapter extends RecyclerView.Adapter<HistoryColetasAdapter.HistoryColetasViewHolder> {

    private List<String> coletas = new ArrayList<>();

    @NonNull
    @Override
    public HistoryColetasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_coletas_history, viewGroup, false);
        HistoryColetasViewHolder holder = new HistoryColetasViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryColetasViewHolder holder, int i) {

        if(coletas.size() > 0){
            String[] coletaArray = coletas.get(i).split(";");
            holder.textViewDate.setText(coletaArray[0]);
            holder.textViewHour.setText(coletaArray[1]);
            holder.textViewState.setText(coletaArray[2]);

        }
    }

    @Override
    public int getItemCount() {
        return coletas.size();
    }

    public void setLista(List<String> coletas){
        this.coletas = coletas;
    }

    public void clearList(List<String> strings){
        this.coletas.removeAll(strings);
    }

    class HistoryColetasViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewDate;
        private TextView textViewHour;
        private TextView textViewState;

        public HistoryColetasViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.TextViewDate);
            textViewHour = itemView.findViewById(R.id.TextViewHour);
            textViewState = itemView.findViewById(R.id.TextViewStats);


        }
    }
}
