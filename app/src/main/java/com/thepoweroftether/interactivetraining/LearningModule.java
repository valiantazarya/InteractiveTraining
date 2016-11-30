package com.thepoweroftether.interactivetraining;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LearningModule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_module);

        //for dummy for kids
        ArrayList<String[]> data= new ArrayList<>();
        for(int i=1; i<=15 ; i++){
            data.add(new String[]{"Judul"+i,"Isi dari module "+i});
        }


        ListView listModule = (ListView) findViewById(R.id.listModule);
        listModule.setAdapter(
                new yourAdapter(
                        this,
                        data
                ));
    }
}

class yourAdapter extends BaseAdapter {

    Context context;
    ArrayList<String[]> data;
    private static LayoutInflater inflater = null;

    public yourAdapter(Context context, ArrayList<String[]> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.itemlearningmodule, null);
        TextView txtTitle = (TextView) vi.findViewById(R.id.txtTitle);
        txtTitle.setText(data.get(position)[0]);
        TextView txtCaption = (TextView) vi.findViewById(R.id.txtCaption);
        txtCaption.setText(data.get(position)[1]);

        Button btnOpen = (Button) vi.findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                    context,
                    "WOI ANCOL "+data.get(position)[0],
                    Toast.LENGTH_SHORT
                ).show();
            }
        });
        return vi;
    }

}
