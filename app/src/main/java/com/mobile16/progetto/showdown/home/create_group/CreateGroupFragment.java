package com.mobile16.progetto.showdown.home.create_group;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mobile16.progetto.showdown.R;

/**
 * Fragment utilizzto per la creazione del gruppo.
 */
public class CreateGroupFragment extends Fragment {

    private EditText name, description;
    private OnCreateGroupListener listener;

    public interface OnCreateGroupListener {
        void onCreateButtonClicked();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCreateGroupListener)
            setListener((OnCreateGroupListener) activity);
    }

    // Setta il listener per questo fragment
    public void setListener(OnCreateGroupListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        name = (EditText) view.findViewById(R.id.group_name);
        description = (EditText) view.findViewById(R.id.group_description);

        view.findViewById(R.id.avantiButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().length() > 0 && description.getText().toString().length() > 0){
                    listener.onCreateButtonClicked();
                } else {
                    Toast.makeText(getActivity(), R.string.insert_group_data, Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    public String getGroupName(){
        return this.name.getText().toString();
    }

    public String getGroupDescription(){
        return this.description.getText().toString();
    }

}