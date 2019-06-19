package com.example.rauber.lixotec.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rauber.lixotec.Model.Coleta;
import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.R;

import java.util.ArrayList;
import java.util.List;

public class ColetasAdapter extends RecyclerView.Adapter<ColetasAdapter.ColetaHolder> {

    private List<Coleta> coletas = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public ColetasAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ColetaHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_address, viewGroup, false);
        ColetaHolder holder = new ColetaHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ColetaHolder coletaHolder, int i) {
        if(coletas.size() > 0){
            Coleta coleta = coletas.get(i);
            Endereco endereco = coleta.getEndereco();
            coletaHolder.idEndereco = Integer.toString(endereco.getIdEndereco());
            coletaHolder.logradouro = endereco.getLogradouro();
            coletaHolder.numero = endereco.getNumero();
            coletaHolder.cep = endereco.getCEP();
            coletaHolder.bairro = endereco.getBairro();
            String address = endereco.getLogradouro()+ ", " +
                    endereco.getNumero() + " - " +
                    endereco.getBairro() + " , " +
                    endereco.getCEP();
            coletaHolder.fullAddress.setText(address);

        }
    }

    @Override
    public int getItemCount() {
        return coletas.size();
    }

    public Coleta getColetas(int position){
        return this.coletas.get(position);
    }



    public void setColetas(List<Coleta> coletas){
        this.coletas = coletas;
    }

    class ColetaHolder extends RecyclerView.ViewHolder{
        private String idEndereco, logradouro, numero, cep, bairro;
        private TextView fullAddress;
        public ColetaHolder(@NonNull View itemView) {
            super(itemView);
            fullAddress = itemView.findViewById(R.id.TextViewAddress);
            itemView.setOnClickListener((View v) -> {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(coletas.get(position));
                    }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(Coleta coleta);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.listener = onItemClickListener;
    }
}
