package com.thepoweroftether.interactivetraining;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
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
    private static final String TAG_ACCOUNT_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_FULLNAME = "fullname";
    private static final String TAG_USERTYPE = "usertype";

    //Usertype Variable
    public static String tag_usertype = "";

    JSONArray accounts = null;

    class LoadAllAccount extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllAccountActivity.this);
            pDialog.setMessage("Loading all accounts");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            JSONObject jsonObject = null;
            try {
                jsonObject = jParser.makeHttpRequest(url_all_accounts, "POST", args);
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
                        String fullname = c.getString(TAG_FULLNAME);
                        String usertype = c.getString(TAG_USERTYPE);

                        HashMap<String, String> map =
                                new HashMap<String, String>();

                        map.put(TAG_ACCOUNT_ID, account_id);
                        map.put(TAG_USERNAME, username);
                        map.put(TAG_FULLNAME, fullname);
                        map.put(TAG_USERTYPE, usertype);

                        accountList.add(map);
                    }
                }
                else {
                    //no accounts found
                    Intent i = new Intent(getApplicationContext(),AdminActivity.class);
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

            if (TAG_USERTYPE.equals("1")){
                tag_usertype = "Admin";
            }
            else if (TAG_USERTYPE.equals("2")){
                tag_usertype = "Member";
            }

            Log.d("Usertype", tag_usertype);

            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            AllAccountActivity.this,
                            accountList,
                            R.layout.list_account,
                            new String[] {TAG_ACCOUNT_ID, TAG_USERNAME, TAG_FULLNAME, TAG_USERTYPE, tag_usertype},
                            new int[]{R.id.account_id, R.id.username_text, R.id.fullname_text, R.id.usertype, R.id.usertype_name}
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

        final ListView listView = getListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String account_id = ((TextView) view.findViewById(R.id.account_id)).getText().toString();
                Intent i = new Intent(getApplicationContext(), EditAccountActivity.class);

                i.putExtra(TAG_ACCOUNT_ID, account_id);
                startActivityForResult(i, 100);
            }
        });

        /*Button editButton = (Button) findViewById(R.id.edit_account_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account_id = ((TextView) listView.findViewById(R.id.account_id)).getText().toString();
                Intent i = new Intent(AllAccountActivity.this, EditAccountActivity.class);

                i.putExtra(TAG_ACCOUNT_ID, account_id);
                startActivityForResult(i, 100);
                //startActivity(i);
            }
        });*/
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
