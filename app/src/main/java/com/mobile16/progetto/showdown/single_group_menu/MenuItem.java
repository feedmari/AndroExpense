package com.mobile16.progetto.showdown.single_group_menu;

import android.view.View;

/**
 * Elemento del menu del gruppo.
 */
public class MenuItem{

    private int itemImage;
    private String itemName;
    private int itemBackgroundColor;
    private View.OnClickListener onClickListener;

    /**
     * Costruttore pubblico della classe
     *
     * @param itemImage L'immagine usata come sfondo per il menu
     * @param itemName Il nome del menu
     * @param itemBackgroundColor Il colore di background del menu
     * @param onClickListener L'OnClickListener contenente l'azione da intraprendere al momento
     *                        del click sul bottone nel menu.
     */
    public MenuItem(int itemImage, String itemName, int itemBackgroundColor,
                    View.OnClickListener onClickListener){
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemBackgroundColor = itemBackgroundColor;
        this.onClickListener = onClickListener;
    }

    public int getItemImage() {
        return itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemBackground(){
        return itemBackgroundColor;
    }

    public View.OnClickListener getOnClickListener(){
        return onClickListener;
    }
}
