package mgorecki.pl.plan.activity;

import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import mgorecki.pl.plan.R;
import mgorecki.pl.plan.domain.PlanItem;
import mgorecki.pl.plan.utils.MyDbHelper;
import mgorecki.pl.plan.utils.Validator;

public class AddItemActivity extends AppCompatActivity {
    public static final String TAG = "AddItemActivity";
    EditText nameEditText;
    EditText headingEditText;
    EditText teacherEditText;
    Calendar myCalendar;
    Button clearButton;
    Button submitButton;
    Spinner weekdaySpinner;
    Spinner timeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        myCalendar = Calendar.getInstance();
        clearButton = (Button) findViewById(R.id.clearButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        headingEditText = (EditText) findViewById(R.id.headingEditText);
        teacherEditText = (EditText) findViewById(R.id.teacherEditText);
        weekdaySpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> weekdaySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.weekdays_array, android.R.layout.simple_spinner_item);

        timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
        ArrayAdapter<CharSequence> timeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.hours_spinner_array, android.R.layout.simple_spinner_item);
        timeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeSpinnerAdapter);

        weekdaySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(weekdaySpinnerAdapter);

        clearButton.setOnClickListener((v) -> {
            clearForm();
        });
    }

    public void submitOnClick(View v) {
        Log.d(TAG, "Adding item to db");
        String time = timeSpinner.getSelectedItem().toString();
        Log.d(TAG, "Time string: " + time);
        String name = nameEditText.getText().toString();
        String heading = headingEditText.getText().toString();
        String teacher = teacherEditText.getText().toString();
        String weekday = weekdaySpinner.getSelectedItem().toString();
        if (name.isEmpty() || heading.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(this, "There's an empty field!", Toast.LENGTH_SHORT).show();
        } else {
            PlanItem item = new PlanItem(name, heading, teacher, time,weekday);
            if (Validator.isDateUnique(item, this)) {
                MyDbHelper.addItem(this, item);
                clearForm();
                goBackToMain();
            } else {
                Toast.makeText(this, "This date is currently taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearForm() {
        nameEditText.getText().clear();
        headingEditText.getText().clear();
        teacherEditText.getText().clear();
    }

    private void goBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        AddItemActivity.this.startActivity(intent);
    }


}
