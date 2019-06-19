package com.example.rauber.lixotec.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rauber.lixotec.Model.Endereco;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.ViewModel.EnderecoViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserAddressAdapter extends RecyclerView.Adapter<UserAddressAdapter.AddressHolder> {

    private List<Endereco> enderecos = new ArrayList<>();
    private EnderecoViewModel enderecoViewModel;
    private Context context;
    private OnItemClickListener listener;

    public UserAddressAdapter(EnderecoViewModel enderecoViewModel, Context context){
        this.enderecoViewModel = enderecoViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_address  , viewGroup, false);
        AddressHolder holder = new AddressHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder addressHolder, int i) {
        if (enderecos.size() > 0){
            Endereco endereco = enderecos.get(i);
            addressHolder.idEndereco = Integer.toString(endereco.getIdEndereco());
            addressHolder.logradouro = endereco.getLogradouro();
            addressHolder.numero = endereco.getNumero();
            addressHolder.cep = endereco.getCEP();
            addressHolder.bairro = endereco.getBairro();
            String address = endereco.getLogradouro()+ ", " +
                    endereco.getNumero() + " - " +
                    endereco.getBairro() + " , " +
                    endereco.getCEP();
            addressHolder.fullAddress.setText(address);
        }
    }

    @Override
    public int getItemCount() {
        return enderecos.size();
    }

    public Endereco getEnderecos(int position){
        return this.enderecos.get(position);
    }

    public void setEnderecos(List<Endereco> enderecos){
        this.enderecos = enderecos;
        notifyDataSetChanged();
    }

    public void delete(int position){
        this.enderecos.remove(position);
    }



    class AddressHolder extends RecyclerView.ViewHolder{
        private String idEndereco, logradouro, numero, cep, bairro;

        private TextView fullAddress;
        public AddressHolder(@NonNull View itemView) {
            super(itemView);
            fullAddress = itemView.findViewById(R.id.TextViewAddress);
            itemView.setOnClickListener((View v) ->  {

                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(enderecos.get(position));
                    }
            });
        }



    }



    public interface OnItemClickListener{
        void onItemClick(Endereco endereco);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.listener = onItemClickListener;
    }
}
