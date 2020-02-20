package com.example.techflex_e_literacy.Adapter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.model.TimetableModel;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private List<TimetableModel> list;

    public ListAdapter(List<TimetableModel> list) {
        this.list = list;
    }
    @NonNull
    @Override
    public ListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListAdapter.ListViewHolder listViewHolder, int position) {
        TimetableModel timetable = list.get(position);
        listViewHolder.subName.setText(timetable.subName);
        listViewHolder.subHour.setText(timetable.hour + "");
        listViewHolder.subCode.setText(timetable.subCode);
        listViewHolder.roomNo.setText(timetable.roomNo);
        listViewHolder.facName.setText(timetable.facName);
        listViewHolder.time.setText(timetable.time);
        listViewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(listViewHolder.getAdapterPosition(), 0, 0, "Delete");
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class ListViewHolder extends RecyclerView.ViewHolder{

        TextView subName, subHour, subCode, roomNo, facName, time;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            subName = itemView.findViewById(R.id.tv_subject_name);
            subHour = itemView.findViewById(R.id.tv_subject_hour);
            subCode = itemView.findViewById(R.id.tv_subject_code);
            roomNo = itemView.findViewById(R.id.tv_room_no);
            facName = itemView.findViewById(R.id.tv_faculty_name);
            time = itemView.findViewById(R.id.tv_time);
        }
    }
}
