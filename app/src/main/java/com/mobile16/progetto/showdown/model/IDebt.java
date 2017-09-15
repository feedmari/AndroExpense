package com.mobile16.progetto.showdown.model;

/**
 * Interfaccia che modella il concetto di un debito all'interno dell'applicazione. Essa si discosta
 * leggermente dal concetto di transazione, poichè qui sono presenti in più il concetto del
 * debitore e del creditore della transazione. Utilizzato design pattern DECORATOR.
 */
@SuppressWarnings("unused")
public interface IDebt extends ITransaction {
    void setCreditoreUsername(String username);
    void setDebitoreUsername(String username);
    void setCreditoreId(String id);
    void setDebitoreId(String id);
    void setDebtId(String id);
    void setAmmontare(Float ammontare);
    void setDataChiusura(String dataChiusura);

    String getCreditoreUsername();
    String getDebitoreUsername();
    String getCreditoreId();
    String getDebitoreId();
    String getDebtId();
    Float getAmmontare();
    String getDataChiusura();
}
