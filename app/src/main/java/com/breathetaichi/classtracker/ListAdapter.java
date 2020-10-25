package com.breathetaichi.classtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<String> {

    public ListAdapter(Context context, String[] resource) {
        super(context, R.layout.term_list_item, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.term_list_item, parent, false);

        String term = getItem(position);

        TextView tv = view.findViewById(R.id.term_text_view);

        tv.setText(term);

        return view;
    }
}
