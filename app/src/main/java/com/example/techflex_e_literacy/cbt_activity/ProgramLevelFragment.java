package com.example.techflex_e_literacy.cbt_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.quiz.Bill;
import com.example.techflex_e_literacy.quiz.FBQAcitvity;
import com.example.techflex_e_literacy.quiz.TrialActivity;
import com.example.techflex_e_literacy.quiz.TrialActivity2;
import com.example.techflex_e_literacy.quiz.TrialActivity3;
import com.example.techflex_e_literacy.quiz.TrialActivity4;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class ProgramLevelFragment extends Fragment {
    Button mcq,fbq,currentpq2019;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_three, container, false);
        mcq = view.findViewById(R.id.mcq);
        mcq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TrialActivity.class));
            }
        });
        fbq = view.findViewById(R.id.fbq);
        fbq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TrialActivity2.class);
                view.getContext().startActivity(intent);

            }
        });
        currentpq2019 = view.findViewById(R.id.currentpq2019);
        currentpq2019.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showPopUp2();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Bill.class);
                view.getContext().startActivity(intent);
            }
        });

        return view;
    }
    void showPopUp2() {
        new AlertDialog.Builder(getActivity())
                .setTitle("!ATTENTION")
                .setMessage("SELECT  CATEGORY")
                .setPositiveButton("MULTI CHOICE QUIZ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getContext(), TrialActivity3.class);
                        startActivity(intent);

                    }

                }).setNegativeButton("FILLING THE GAP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getContext(), TrialActivity4.class);
                startActivity(intent);
            }
        }).setCancelable(false).show();
    }

}
