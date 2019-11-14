package com.example.techflex_e_literacy.cbt_activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.quiz.FBQAcitvity;
import com.example.techflex_e_literacy.quiz.TrialActivity;
import com.example.techflex_e_literacy.quiz.TrialActivity2;

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

        return view;
    }

}
