package com.example.resqfood;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.resqfood.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SupportFragment extends Fragment {

    private TextView question1, question2, question3, question4;
    private TextView answer1, answer2, answer3, answer4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        question1 = view.findViewById(R.id.question1);
        question2 = view.findViewById(R.id.question2);
        question3 = view.findViewById(R.id.question3);
        question4 = view.findViewById(R.id.question4);

        answer1 = view.findViewById(R.id.answer1);
        answer2 = view.findViewById(R.id.answer2);
        answer3 = view.findViewById(R.id.answer3);
        answer4 = view.findViewById(R.id.answer4);

        question1.setText(getString(R.string.question_1));
        question2.setText(getString(R.string.question_2));
        question3.setText(getString(R.string.question_3));
        question4.setText(getString(R.string.question_4));

        answer1.setText(getString(R.string.answer_1));
        answer2.setText(getString(R.string.answer_2));
        answer3.setText(getString(R.string.answer_3));
        answer4.setText(getString(R.string.answer_4));

        // Set OnClickListener for question1
        question1.setOnClickListener(v -> toggleAnswerVisibility(answer1));

        // Set OnClickListener for question2
        question2.setOnClickListener(v -> toggleAnswerVisibility(answer2));

        // Set OnClickListener for question3
        question3.setOnClickListener(v -> toggleAnswerVisibility(answer3));

        // Set OnClickListener for question4
        question4.setOnClickListener(v -> toggleAnswerVisibility(answer4));

        return view;
    }

    private void toggleAnswerVisibility(TextView answer) {
        if (answer.getVisibility() == View.VISIBLE) {
            answer.setVisibility(View.GONE);
        } else {
            answer.setVisibility(View.VISIBLE);
        }
    }
}
/*
public class SupportFragment extends Fragment {

    private ExpandableListView expandableListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        TextView question1 = view.findViewById(R.id.question1);
        TextView question2 = view.findViewById(R.id.question2);

        question1.setOnClickListener(v -> showAnswer(1));
        question2.setOnClickListener(v -> showAnswer(2));

        // Add more questions and set their OnClickListener as needed

        return view;
    }
    private void showAnswer(int questionIndex) {
        String questionKey = "question_" + questionIndex;
        String answerKey = "answer_" + questionIndex;
        int questionResId = getResources().getIdentifier(questionKey, "string", requireContext().getPackageName());
        int answerResId = getResources().getIdentifier(answerKey, "string", requireContext().getPackageName());

        if (questionResId == 0 || answerResId == 0) {
            // Question or answer not found
            return;
        }

        String question = getResources().getString(questionResId);
        String answer = getResources().getString(answerResId);

        // Update UI to show the question and answer
        // For simplicity, you can just show them in a Toast message
        String message = question + "\n\n" + answer;
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

}
  /*

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        expandableListView = view.findViewById(R.id.expandableListView);

        // Initialize data
        initData();

        // Initialize adapter
        supportAdapter = new SupportAdapter(getContext(), listDataHeader, listDataChild);

        // Set adapter
        expandableListView.setAdapter(supportAdapter);

        // Expand all groups by default
        int count = supportAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }

        // Set group click listener
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            // Do something when a group is clicked
            //Toast.makeText(getContext(), "Group clicked: " + listDataHeader.get(groupPosition), Toast.LENGTH_SHORT).show();
            return false;
        });

        // Set child click listener
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            // Do something when a child is clicked
            //Toast.makeText(getContext(), "Child clicked: " + listDataChild.get(listDataHeader.get(groupPosition)), Toast.LENGTH_SHORT).show();
            return false;
        });

        return view;
    }

    // Initialize data
    private void initData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Add frequently asked questions and answers for organizations
        listDataHeader.add("How can my organization donate food through ResQFood?");
        listDataChild.put(listDataHeader.get(0), "To donate food through ResQFood as an organization, follow these steps:\n1. Register or log in to your ResQFood organization account.\n2. Navigate to the 'Donate Food' section.\n3. Fill out the donation form with details such as food type, quantity, and pickup location.\n4. Submit your donation request.");

        listDataHeader.add("What types of food can my organization donate?");
        listDataChild.put(listDataHeader.get(1), "Your organization can donate any perishable or non-perishable food items that are safe for consumption. This includes surplus food from events, restaurants, cafeterias, etc.");

        listDataHeader.add("Is there a minimum quantity of food required for donation?");
        listDataChild.put(listDataHeader.get(2), "There is no minimum quantity requirement for food donations from organizations. Every donation, no matter how small or large, helps feed someone in need.");

        listDataHeader.add("How does ResQFood ensure food safety?");
        listDataChild.put(listDataHeader.get(3), "ResQFood partners with registered food banks and NGOs to ensure that all donated food items meet safety standards. Additionally, our volunteers undergo training in food handling and safety.");

        listDataHeader.add("Can my organization request a pickup for the food donation?");
        listDataChild.put(listDataHeader.get(4), "Yes, ResQFood provides pickup services for food donations from organizations. Simply provide your pickup location and preferred date/time when submitting your donation request.");

        // Add more FAQs and answers as needed
    }
}
*/