package com.mobile16.progetto.showdown.model;

/**
 * Enumerazione che modella il concetto di stato della transazione.
 */
public enum TransactionState {

    IN_CREATION("In-Creation Transactions", "All in-creation transactions"),
    PENDING("Pending Transactions", "All pending transactions"),
    TAKEN("Taken Transactions", "All taken transactions"),
    ACTIVE("Active Transactions", "All pending transactions"),
    CLOSED("Closed Transactions", "All closed transactions");


    private String title;
    private String description;

    /**
     * The constructor for the enum TransactionState
     * @param title the transaction Title
     * @param description the transaction description
     */
    TransactionState(final String title, final String description){
        this.title = title;
        this.description = description;
    }

    /**
     * Get the title from TransactionState
     * @return the title
     */
    public String getTitle(){
        return this.title;
    }

    /**
     * Get the description from transactionState
     * @return the description
     */
    @SuppressWarnings("unused")
    public String getDescription(){
        return this.description;
    }

}
