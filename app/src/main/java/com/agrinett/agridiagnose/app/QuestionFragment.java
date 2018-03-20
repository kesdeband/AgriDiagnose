package com.agrinett.agridiagnose.app;

import android.os.Bundle;
import android.app.Fragment;
// import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.agrinett.agridiagnose.R;
import com.agrinett.agridiagnose.data.IRepository;
import com.agrinett.agridiagnose.data.Repository;
import com.agrinett.agridiagnose.models.Characteristic;
import com.agrinett.agridiagnose.models.DiseaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QuestionFragment extends Fragment {

    // Properties
    IRepository repository;
    List<Characteristic> questions;
    int questionIndex;
    private boolean isCompleted;
    List<DiseaseModel> diseaseModels;
    Map<String, Double> utilities;
    List<String> answeredQuestions;

    // View Controls
    private TextView tvQuestion;
    private ImageButton iBtnHelp;
    private Button btnResponse;
    private TextView tvResponseLabel;
    private TextView tvResponse;
    private Button btnNext;

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        tvQuestion = view.findViewById(R.id.tvQuestion);
        iBtnHelp = view.findViewById(R.id.iBtnHelp);
        btnResponse = view.findViewById(R.id.btnResponse);
        tvResponseLabel = view.findViewById(R.id.tvResponseLabel);
        tvResponse = view.findViewById(R.id.tvResponse);
        Button btnReset = view.findViewById(R.id.btnReset);
        btnNext = view.findViewById(R.id.btnNext);

        repository = new Repository(getActivity());
        initialize();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize();
            }
        });

        return view;
    }

    private void initialize() {
        // Load filter questions
        questions = repository.QueryFilterQuestions();
        questionIndex = 0;
        isCompleted = false;
        diseaseModels = new ArrayList<>();
        utilities = new HashMap<>(); //repository.QueryUtilities();
        answeredQuestions = new ArrayList<>();
        btnResponse.setEnabled(true);
        btnNext.setText(R.string.question_btn_next);

//        Log.d("Models First", String.valueOf(diseaseModels.size()));
//        Log.d("Questions", String.valueOf(questions.size()));

        nextQuestion();
    }

    private void nextQuestion() {
        hideResponse();
        Characteristic characteristic = questions.get(questionIndex++);
        final String characteristicId = characteristic.CharacteristicId();
        diseaseModels = repository.QueryDiseaseModels(characteristicId);

        // If this is the first question select any disease model
        // else select the disease model with highest utility

//        Predicate<DiseaseModel> filter = new Predicate<DiseaseModel>() {
//            @Override
//            public boolean apply(DiseaseModel input) {
//                return input.CharacteristicId().equals(characteristicId);
//            }
//        };

//        Collection<DiseaseModel> models = Collections2.filter(diseaseModels, filter);
//        String diseaseId = models.iterator().next().DiseaseId();

        String diseaseId = diseaseModels.get(0).DiseaseId();
        if(!characteristic.Type().equals("Filter") && !characteristic.Type().equals("Start")) {
            diseaseId = utilities.keySet().iterator().next();
        }
        String scaleId = repository.QueryScaleId(characteristicId, diseaseId);

        // Add characteristic to the answered question list
        if(!answeredQuestions.contains(characteristicId)) {
            answeredQuestions.add(characteristicId);
        }

        setView(characteristic, diseaseId, scaleId);
    }

    private void setView(final Characteristic characteristic, final String diseaseId, final String scaleId) {
        tvQuestion.setText(characteristic.Question());
        final double[] normalizedScaleValue = {0};
        final String characteristicId = characteristic.CharacteristicId();
        Log.d("PROPS", characteristicId + " " + diseaseId + " " + scaleId);
        final String reason = repository.QueryReason(characteristicId, diseaseId, scaleId);
        iBtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
//                        .icon(ContextCompat.getDrawable(getActivity(), R.drawable.help))
                        .title("Reason for asking?")
                        .content(reason)
                        .neutralText("Close")
                        .show();
            }
        });

        final List<String> responses = repository.QueryResponses(characteristicId, diseaseId);
        btnResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                    .title("Responses")
                    .items(responses)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            /*
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             */
                            if(text != null) {
                                String response = text.toString();
                                showResponse(response);
                                double propertyValue = repository.QueryPropertyValue(response, characteristicId, diseaseId);
                                double max = repository.QueryMaxPropertyValue(characteristicId, diseaseId, scaleId);
                                double min = repository.QueryMinPropertyValue(characteristicId, diseaseId, scaleId);
                                if(max - min == 0) {
                                    Toast.makeText(getActivity(), "DivisionByZero", Toast.LENGTH_SHORT).show();
                                    Log.d("DivisionByZero", characteristicId + " " + diseaseId + " " + scaleId);
                                }
                                normalizedScaleValue[0] = normalizedScaleValue(propertyValue, max, min);
                                Toast.makeText(getActivity(),  String.valueOf(propertyValue), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), "You did not select any response", Toast.LENGTH_SHORT).show();
                            }

                            return true;
                        }
                    })
                    .positiveText("Choose")
                    .show();
            }
        });

         btnNext.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String response = tvResponse.getText().toString();
                 if(response.isEmpty()) {
                     Toast.makeText(getActivity(), "Please select a response before proceeding", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     // calculate utility
                     if(!characteristic.Type().equals("Filter") && !isCompleted) {
                         calculateUtility(characteristic.CharacteristicId(), normalizedScaleValue[0]);
                     }

                     if(questionIndex < questions.size()) {
                         nextQuestion();
                     }
                     else {
                         // All questions answered
//                         Log.d("FINISH", "FINISH");
                         Toast.makeText(getActivity(), "All questions answered", Toast.LENGTH_SHORT).show();
                         isCompleted = true;
                         diagnoseDiseases();
                     }
                 }
             }
         });
    }

    private void showResponse(String response) {
        tvResponse.setText(response);
        tvResponseLabel.setVisibility(TextView.VISIBLE);
        tvResponse.setVisibility(TextView.VISIBLE);
    }

    private void hideResponse() {
        tvResponse.setText(null);
        tvResponseLabel.setVisibility(TextView.INVISIBLE);
        tvResponse.setVisibility(TextView.INVISIBLE);
    }

    private double normalizedScaleValue(double scaleValue, double maxScaleValue, double minScaleValue){
        if(maxScaleValue - minScaleValue == 0) return 0;
        return ((2*(scaleValue-minScaleValue)) / (maxScaleValue - minScaleValue)) - 1;
    }

    private void calculateUtility(String characteristicId, double normalizedScaleValue) {
//        String characteristicId = characteristic.CharacteristicId();
//        Log.d("Characteristic", characteristicId);

        for(DiseaseModel model : diseaseModels) {
            if(model.CharacteristicId().equals(characteristicId)) {
                double utilityValue = normalizedScaleValue * model.ProbabilityValue();
                if(utilities.containsKey(model.DiseaseId())) {
                    double currentUtility = utilities.get(model.DiseaseId());
                    utilityValue += currentUtility;
                }
                utilities.put(model.DiseaseId(), utilityValue);
            }
        }

        utilities = sortByValue(utilities);
//        Log.d("SIZE", String.valueOf(utilities.size()));

//        for (Map.Entry<String, Double> utility : utilities.entrySet()) {
//            Log.d(utility.getKey(), String.valueOf(utility.getValue()));
//        }

        String diseaseId = utilities.keySet().iterator().next();
        questions.clear();
        questionIndex = 0;
        questions = repository.QueryQuestions(diseaseId);

        List<Characteristic> oldQuestions = new ArrayList<>();
        for (Characteristic c : questions) {
            for (String ans : answeredQuestions) {
                if (c.CharacteristicId().equals(ans)) {
                    oldQuestions.add(c);
                }
            }
        }

        questions.removeAll(oldQuestions);

//        Log.d("TOP DISEASE", diseaseId);
    }

    private <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(Map<K, V> sortMap) {

        List<Map.Entry<K, V>> list = new LinkedList<>(sortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private void diagnoseDiseases() {
        String diseaseIds[] = new String[]{
                utilities.keySet().toArray()[0].toString(),
                utilities.keySet().toArray()[1].toString()
        };
        // Log.d("DiseaseIds", diseaseIds.toString());
        List<String> diseases = repository.QueryDiseases(diseaseIds);

        // Disable Choice Button
        btnResponse.setEnabled(false);
        btnNext.setText(R.string.question_btn_diagnose);

        new MaterialDialog.Builder(getActivity())
            .title("Disease Diagnosis Results")
            .items(diseases)
            .positiveText("Ok")
//                            .itemsCallback(new MaterialDialog.ListCallback() {
//                                @Override
//                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                }
//                            })
            .show();
    }
}
