package mgorecki.pl.plan.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.List;
import mgorecki.pl.plan.R;
import mgorecki.pl.plan.domain.PlanItem;
import mgorecki.pl.plan.utils.MyDbHelper;
import mgorecki.pl.plan.utils.PlanItemAdapter;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    Toolbar toolbar;
    ListView listView;
    List<PlanItem> planList;
    PlanItemAdapter adapter;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ENTER: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.planlistview);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        listView = (ListView) findViewById(R.id.planlistview);

        planList = MyDbHelper.getAllItems(this);
        Log.d(TAG, "Total rows count retrieved from DB: " + MyDbHelper.getCount(this));

        adapter = new PlanItemAdapter(this, planList);
        Log.d(TAG, "Rows count from adapter: " + adapter.getCount());
        listView.setAdapter(adapter);
        Log.d(TAG, "Rows count from listview: " + listView.getCount());

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item clicked...");
                buildDialog((PlanItem) listView.getItemAtPosition(position));
            }
        });
        setSupportActionBar(toolbar);
        Log.d(TAG, "RETURN: onCreate()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.weekday_spinner_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "ENTER: onItemSelected()");
                populateListView();
                Log.d(TAG, "RETURN: onItemSelected()");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }

    public void populateListView() {
        Log.d(TAG, "ENTER: populateListView()");
        String weekday = spinner.getSelectedItem().toString();

        if (weekday.equals("All")) {
            planList = MyDbHelper.getAllItems(MainActivity.this);
        } else {
            planList = MyDbHelper.getAllItemsPerDay(MainActivity.this, weekday);
        }
        for (PlanItem planItem : planList) {
            Log.d(TAG, planItem.getName() + " " + planItem.getWeekday() + " " + planItem.getTime());
        }
        adapter.clear();
        adapter.addAll(planList);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        Log.d(TAG, "RETURN: populateListView()");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addAction:
                Intent intent = new Intent(this, AddItemActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void buildDialog(PlanItem planItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(planItem.getName())
                .setMessage(buildDescription(planItem))
                .setPositiveButton(R.string.delete_btn, (dialogInterface, i) -> {
                    MyDbHelper.removeItem(this,planItem);
                    planList = MyDbHelper.getAllItems(this);
                    populateListView();
                }).setNegativeButton(R.string.cancel_btn, (dialogInterface, i) -> {

                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String buildDescription(PlanItem planItem){
        return new StringBuilder().append(planItem.getHeading())
                .append(", ")
                .append(planItem.getWeekday())
                .append(", ")
                .append(planItem.getTime())
                .append("--> ")
                .append(planItem.getTeacher()).toString();
    }


}
