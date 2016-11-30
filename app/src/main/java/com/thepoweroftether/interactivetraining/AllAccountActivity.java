package com.thepoweroftether.interactivetraining;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class AllAccountActivity extends ListActivity {

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> accountList;

    private static String url_all_accounts = Server.URL + Server.readAccount;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ACCOUNTS = "accounts";
    private static final String TAG_ACCOUNT_ID = "account_id";
    private static final String TAG_USERNAME = "name";

    JSONArray accounts = null;

    class LoadAllAccount extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllAccountActivity.this);
            pDialog.setMessage("Loading All Accounts");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            JSONObject jsonObject = null;
            try {
                jsonObject = jParser.makeHttpRequest(url_all_accounts, "GET", args);
            } catch (IOException e) {
                Log.d("Networking", e.getLocalizedMessage());
            }

            Log.d("All accounts: ", jsonObject.toString());

            try {
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1) {
                    //account found
                    accounts = jsonObject.getJSONArray(TAG_ACCOUNTS);

                    for (int i=0; i<accounts.length(); i++) {
                        JSONObject c = accounts.getJSONObject(i);

                        String account_id = c.getString(TAG_ACCOUNT_ID);
                        String username = c.getString(TAG_USERNAME);

                        HashMap<String, String> map =
                                new HashMap<String, String>();

                        map.put(TAG_ACCOUNT_ID, account_id);
                        map.put(TAG_USERNAME, username);

                        accountList.add(map);
                    }
                }
                else {
                    //no accounts found
                    Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            AllAccountActivity.this,
                            accountList,
                            R.layout.list_account,
                            new String[] {TAG_ACCOUNT_ID, TAG_USERNAME},
                            new int[]{R.id.account_id, R.id.username}
                    );
                    setListAdapter(adapter);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_account);

        accountList = new ArrayList<HashMap<String, String>>();

        new LoadAllAccount().execute();

        ListView listView = getListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String account_id = ((TextView) view.findViewById(R.id.account_id)).getText().toString();
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);

                i.putExtra(TAG_ACCOUNT_ID, account_id);
                startActivityForResult(i, 100);
            }
        });
        /*final ListView account_list = (ListView) findViewById(R.id.account_list);
        account_list.setAdapter(new AccountAdapter(
                this,
                new ArrayList<String[]>()
                ));*/
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
