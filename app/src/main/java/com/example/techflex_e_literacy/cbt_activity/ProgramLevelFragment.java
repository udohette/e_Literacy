package com.example.techflex_e_literacy.cbt_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.quiz.FBQAcitvity;
import com.example.techflex_e_literacy.quiz.QuizActivity;

public class ProgramLevelFragment extends Fragment {
    Button mcq,fbq;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        mcq = view.findViewById(R.id.mcq);
        mcq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), QuizActivity.class));
            }
        });
        fbq = view.findViewById(R.id.fbq);
        fbq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FBQAcitvity.class);
                view.getContext().startActivity(intent);

            }
        });

        return view;
    }

}
