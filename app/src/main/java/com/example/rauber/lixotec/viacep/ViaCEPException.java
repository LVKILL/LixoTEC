/*
 * PARG Desenvolvimento de Sistemas
 * Pablo Alexander - pablo@parg.com.br
 * 
 * Obtem um CEP no ViaCEP
 */
package com.example.rauber.lixotec.viacep;

/**
 * Classe para registrar uma exceção de CEP
 * @author Pablo Alexander da Rocha Gonçalves
 */
public class ViaCEPException extends Exception {
    private String CEP;
    
    /**
     * Gera uma nova exceção
     * 
     * @param message descrição do erro
     */
    public ViaCEPException(String message) {
        super(message);
        
        this.CEP = "";
    }
    
    /**
     * Gera uma nova exceção e define o CEP que foi solicitado
     * 
     * @param message descrição do erro
     * @param cep CEP que foi usado durante o processo
     */
    public ViaCEPException(String message, String cep) {
        super(message);
        
        this.CEP = cep;
    }
    
    /**
     * Define o CEP da exceção
     * 
     * @param cep 
     */
    public void setCEP(String cep) {
        this.CEP = cep;
    }
    
    /**
     * Retorna o CEP da exceção
     * 
     * @return String CEP
     */
    public String getCEP() {
        return this.CEP;
    }
    
    /**
     * Retorna se tem algum CEP
     * 
     * @return boolean
     */
    public boolean hasCEP() {
        return !this.CEP.isEmpty();
    }
}
