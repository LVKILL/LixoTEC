/*
 * PARG Desenvolvimento de Sistemas
 * Pablo Alexander - pablo@parg.com.br
 * 
 * Obtem um CEP no ViaCEP
 */
package com.example.rauber.lixotec.viacep;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe java para obter um CEP no ViaCEP
 *
 * @author Pablo Alexander da Rocha Gonçalves
 */
public class ViaCEP {

    private String CEP;
    private String Logradouro;
    private String Complemento;
    private String Bairro;
    private String Localidade;
    private String Uf;
    private String Ibge;
    private String Gia;

    /**
     * Constrói uma nova classe
     */
    public ViaCEP() {
        this.CEP = null;
        this.Logradouro = null;
        this.Complemento = null;
        this.Bairro = null;
        this.Localidade = null;
        this.Uf = null;
        this.Ibge = null;
        this.Gia = null;
    }

    public ViaCEP(String cep) throws ViaCEPException {
        this.buscar(cep);
    }


    public final void buscar(String cep) throws ViaCEPException {
        this.CEP = cep;

        // define a url
        String url = "http://viacep.com.br/ws/" + cep + "/json/";
try {
    // define os dados
    JSONObject obj = new JSONObject(this.get(url));

    if (!obj.has("erro")) {
        this.CEP = obj.getString("cep");
        this.Logradouro = obj.getString("logradouro");
        this.Complemento = obj.getString("complemento");
        this.Bairro = obj.getString("bairro");
        this.Localidade = obj.getString("localidade");
        this.Uf = obj.getString("uf");
        this.Ibge = obj.getString("ibge");
        this.Gia = obj.getString("gia");
    } else {
        throw new ViaCEPException("Não foi possível encontrar o CEP", cep);
    }
}catch (JSONException je){
    Log.v("Tag",""+je.getMessage());
}
    }

    /**
     * Retonar o CEP
     *
     * @return
     */
    public String getCep() {
        return this.CEP;
    }

    /**
     * Retorna o nome da rua, avenida, travessa, ...
     *
     * @return
     */
    public String getLogradouro() {
        return this.Logradouro;
    }

    /**
     * Retorna se tem algum complemento Ex: lado impar
     *
     * @return
     */
    public String getComplemento() {
        return this.Complemento;
    }

    /**
     * Retorna o Bairro
     *
     * @return
     */
    public String getBairro() {
        return this.Bairro;
    }

    /**
     * Retorna a Cidade
     *
     * @return
     */
    public String getLocalidade() {
        return this.Localidade;
    }

    /**
     * Retorna o UF
     *
     * @return
     */
    public String getUf() {
        return this.Uf;
    }

    /**
     * Retorna o Ibge
     *
     * @return
     */
    public String getIbge() {
        return this.Ibge;
    }

    /**
     * Retorna a Gia
     *
     * @return
     */
    public String getGia() {
        return this.Gia;
    }

    public final String get(String urlToRead) throws ViaCEPException {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            
        } catch (MalformedURLException | ProtocolException ex) {
            throw new ViaCEPException(ex.getMessage());
        } catch (IOException ex) {
            throw new ViaCEPException(ex.getMessage());
        }
        
        return result.toString();
    }
}
