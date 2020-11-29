package com.example.techflex_e_literacy.pojos;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.GoogleAPI;
import com.example.techflex_e_literacy.mainActivity.PlacesAPI;

public class MyAdapter extends BaseAdapter {
    GoogleAPI myActivity;
    Places places_;

    public MyAdapter(GoogleAPI googleAPI, Places places_) {
        this.myActivity = googleAPI;
        this.places_ = places_;
    }


    @Override
    public int getCount() {
        return places_.getList().size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(myActivity);
        View v = inflater.inflate(R.layout.indi_view, null);
        TextView tv1 = v.findViewById(R.id.tv1);
        TextView tv2 = v.findViewById(R.id.tv2);

        tv1.setText(places_.getList().get(position).getName().toString());
        Log.i("name_api", places_.getList().get(position).getName());
        tv2.setText(places_.getList().get(position).getVicinity().toString());
        Log.i("places_api", places_.getList().get(position).getName());
        return v;
    }
}
