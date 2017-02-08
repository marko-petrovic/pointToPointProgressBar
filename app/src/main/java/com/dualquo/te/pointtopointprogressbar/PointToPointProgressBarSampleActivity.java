package com.dualquo.te.pointtopointprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.dualquo.te.pointtopointprogressbar.PointToPointProgressBarView.Point.FOUR;
import static com.dualquo.te.pointtopointprogressbar.PointToPointProgressBarView.Point.ONE;

public class PointToPointProgressBarSampleActivity extends AppCompatActivity {

    private Button buttonPrevious;
    private Button buttonNext;
    private PointToPointProgressBarView pointToPointProgressBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_to_point_progress_bar_sample);

        pointToPointProgressBarView = (PointToPointProgressBarView) findViewById(R.id.point_to_point_progress_bar);

        buttonPrevious = (Button) findViewById(R.id.button_decrement_point);
        buttonNext = (Button) findViewById(R.id.button_increment_point);

        buttonPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pointToPointProgressBarView.setCurrentPointNumber(
                        pointToPointProgressBarView.getCurrentPointNumber() - 1
                );

                manageButtonsVisibility();
            }
        });

        buttonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pointToPointProgressBarView.setCurrentPointNumber(
                        pointToPointProgressBarView.getCurrentPointNumber() + 1
                );

                manageButtonsVisibility();
            }
        });

        manageButtonsVisibility();
    }

    private void manageButtonsVisibility() {
        if (pointToPointProgressBarView.getCurrentPointNumber() == ONE.getIntValue()) {
            buttonPrevious.setVisibility(INVISIBLE);
        } else if (pointToPointProgressBarView.getCurrentPointNumber() == FOUR.getIntValue()) {
            buttonNext.setVisibility(INVISIBLE);
        } else {
            buttonPrevious.setVisibility(VISIBLE);
            buttonNext.setVisibility(VISIBLE);
        }
    }
}
