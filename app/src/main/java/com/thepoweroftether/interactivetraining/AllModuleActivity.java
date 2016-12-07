package com.thepoweroftether.interactivetraining;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllModuleActivity extends ListActivity {

    private static Boolean InternetConnection = true;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> moduleList;

    private static String url_all_modules = Server.URL + Server.readModules;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MODULES = "modules";
    private static final String TAG_MODULES_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CAPTION = "caption";
    private static final String TAG_USERNAME_UPLOAD = "username_upload";

    JSONArray modules = null;

    class LoadAllModule extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllModuleActivity.this);
            pDialog.setMessage("Loading all modules");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            JSONObject jsonObject = null;
            try {
                jsonObject = jParser.makeHttpRequest(url_all_modules, "POST", args);

                Log.d("All module: ", jsonObject.toString());
            } catch (IOException e) {
                Log.d("Networking", e.getLocalizedMessage());
            } catch (NullPointerException e) {
                InternetConnection = false;
            }

            try {
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1) {
                    //account found
                    modules = jsonObject.getJSONArray(TAG_MODULES);

                    for (int i=0; i<modules.length(); i++) {
                        JSONObject c = modules.getJSONObject(i);

                        String modules_id = c.getString(TAG_MODULES_ID);
                        String title = c.getString(TAG_TITLE);
                        String caption = c.getString(TAG_CAPTION);
                        String username_upload = c.getString(TAG_USERNAME_UPLOAD);

                        HashMap<String, String> map =
                                new HashMap<String, String>();

                        map.put(TAG_MODULES_ID, modules_id);
                        map.put(TAG_TITLE, title);
                        map.put(TAG_CAPTION, caption);
                        map.put(TAG_USERNAME_UPLOAD, "[By : "+username_upload+"]");

                        moduleList.add(map);
                    }
                }
                else {
                    //no modules found
                    Intent i = new Intent(getApplicationContext(),AdminActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                InternetConnection = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!InternetConnection) {
                finish();
                Toast.makeText(
                        AllModuleActivity.this,
                        "Error! No internet connection",
                        Toast.LENGTH_SHORT)
                        .show();
            }

            pDialog.dismiss();

            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            AllModuleActivity.this,
                            moduleList,
                            R.layout.list_modul,
                            new String[] {TAG_MODULES_ID, TAG_TITLE, TAG_CAPTION, TAG_USERNAME_UPLOAD},
                            new int[]{R.id.module_id, R.id.title_text, R.id.caption_text, R.id.username_upload_text}
                    );
                        setListAdapter(adapter);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_module);

        final String username;
        Intent i = getIntent();
        username = i.getStringExtra("USERNAME");

        moduleList = new ArrayList<HashMap<String, String>>();

        new LoadAllModule().execute();

        final ListView listView = getListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String module_id = ((TextView) view.findViewById(R.id.module_id)).getText().toString();
                Intent i = new Intent(getApplicationContext(), UploadModuleActivity.class);

                i.putExtra(TAG_MODULES_ID, module_id);
                i.putExtra("USERNAME", username);
                i.putExtra("METHOD", "2");
                startActivityForResult(i, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            Intent i = getIntent();
            finish();
            startActivity(i);
        }
    }
}
