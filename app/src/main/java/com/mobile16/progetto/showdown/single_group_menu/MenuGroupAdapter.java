package com.mobile16.progetto.showdown.single_group_menu;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile16.progetto.showdown.R;
import java.util.List;

/*
 * Disegna come cardview gli elementi di tipo MenuItem passati al costruttore della classe.
 */
public class MenuGroupAdapter extends RecyclerView.Adapter<MenuGroupAdapter.MenuViewHolder>{

    // Lista contenente gli elementi del menu
    private List<MenuItem> menuItemsList;

    public MenuGroupAdapter(List<MenuItem> customizedListView) {
        menuItemsList = customizedListView;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_menu, parent, false);
        return new MenuViewHolder(v);
    }

    // Setta lo stato dell' item_group_menu
    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.groupImage.setImageResource(menuItemsList.get(position).getItemImage());
        holder.groupName.setText(menuItemsList.get(position).getItemName());
        holder.cardView.setBackgroundColor(menuItemsList.get(position).getItemBackground());
        holder.setMenuClickListener(menuItemsList.get(position).getOnClickListener());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return menuItemsList.size();
    }


    // Mantiene lo stato del singolo item_group_menu
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView groupImage;
        private TextView groupName;
        private CardView cardView;
        private View itemView;

        MenuViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            groupImage = (ImageView)itemView.findViewById(R.id.menu_image);
            groupName = (TextView)itemView.findViewById(R.id.menu_item_name);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
        }

        void setMenuClickListener(View.OnClickListener onClickListener){
            itemView.setOnClickListener(onClickListener);
        }
    }
}