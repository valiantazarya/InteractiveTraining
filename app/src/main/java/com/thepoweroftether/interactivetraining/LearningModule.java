package com.thepoweroftether.interactivetraining;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.BundleCompat;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.category;
import static android.R.attr.id;
import static android.R.attr.start;

public class LearningModule extends AppCompatActivity {
    private ProgressDialog pDialog;
    private static final int progress_bar_type = 0;
    ListView listModule;
    learningItemAdapter adapter;

    private static String file_url = "http://notes.azarya.xyz/gobs_enlightment/learning_module/";

    private static final String url_read_module = Server.URL + Server.readModule;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MODULE = "modules";
    private static final String TAG_MODULE_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CAPTION = "caption";
    private static final String TAG_FILENAME = "filename";
    private static final String TAG_UPLOADER = "uploader";
    JSONParser jsonParser = new JSONParser();

    ArrayList<ModuleItem> moduleItems;

    @Override
    protected void onPause() {
        super.onPause();
        adapter.data.clear();
        new LoadAllModules().execute(getApplicationContext());
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_module);

        listModule = (ListView) findViewById(R.id.listModule);
        moduleItems = new ArrayList<ModuleItem>();
        new LoadAllModules().execute(LearningModule.this);
    }


    public class ModuleItem {
        public String id;
        public String title;
        public String caption;
        public String filename;
        public String uploader;

        ModuleItem(String id,
                   String title,
                   String caption,
                   String filename,
                   String uploader
        ) {
            this.id = id;
            this.title = title;
            this.caption = caption;
            this.filename = filename;
            this.uploader = uploader;
        }
    }


    private void getAllModules() {
        int success;
        try {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            JSONObject jsonObject = null;

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                jsonObject = jsonParser.makeHttpRequest(url_read_module, "POST", args);
            } catch (IOException e) {
                Log.d("Networking", e.getLocalizedMessage());
            }


            Log.d("Login details", jsonObject.toString());

            success = jsonObject.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONArray moduleObj = jsonObject.getJSONArray(TAG_MODULE);

                for (int i = 0; i < moduleObj.length(); i++) {
                    JSONObject module = moduleObj.getJSONObject(i);
                    String id = module.getString(TAG_MODULE_ID);
                    String name = module.getString(TAG_TITLE);
                    ModuleItem item = new ModuleItem(
                            module.getString(TAG_MODULE_ID),
                            module.getString(TAG_TITLE),
                            module.getString(TAG_CAPTION),
                            module.getString(TAG_FILENAME),
                            module.getString(TAG_UPLOADER)
                    );
                    moduleItems.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            startActivity(
                    new Intent(
                            getApplicationContext(),
                            LoginActivity.class
                    )
            );
        }
    }


    class LoadAllModules extends AsyncTask<Context, String, String> {
        Context context;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter = new learningItemAdapter(
                    context,
                    moduleItems
            );
            //INSERTING DATA
            listModule.setAdapter(
                    adapter
            );
        }

        @Override
        protected String doInBackground(Context... params) {
            context = params[0];
            int success;
            try {
                List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
                JSONObject jsonObject = null;

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    jsonObject = jsonParser.makeHttpRequest(url_read_module, "POST", args);
                } catch (IOException e) {
                    Log.d("Networking", e.getLocalizedMessage());
                }


                Log.d("Login details", jsonObject.toString());

                success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray moduleObj = jsonObject.getJSONArray(TAG_MODULE);

                    for (int i = 0; i < moduleObj.length(); i++) {
                        JSONObject module = moduleObj.getJSONObject(i);
                        String id = module.getString(TAG_MODULE_ID);
                        String name = module.getString(TAG_TITLE);
                        ModuleItem item = new ModuleItem(
                                module.getString(TAG_MODULE_ID),
                                module.getString(TAG_TITLE),
                                module.getString(TAG_CAPTION),
                                module.getString(TAG_FILENAME),
                                module.getString(TAG_UPLOADER)
                        );
                        moduleItems.add(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                startActivity(
                        new Intent(
                                context,
                                LoginActivity.class
                        )
                );
            }
            return null;
        }


    }


    class learningItemAdapter extends BaseAdapter {

        Context context;
        ArrayList<ModuleItem> data;
        private LayoutInflater inflater = null;

        public learningItemAdapter(Context context, ArrayList<ModuleItem> data) {
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

            String title = data.get(position).title;
            String caption = data.get(position).caption;
            final String filename = data.get(position).filename;

            TextView txtTitle = (TextView) vi.findViewById(R.id.txtTitle);
            txtTitle.setText(title);
            TextView txtCaption = (TextView) vi.findViewById(R.id.txtCaption);
            txtCaption.setText(caption.length() > 40 ? caption.substring(0, 40) + " ..." : caption);

            final ImageButton btnOpen = (ImageButton) vi.findViewById(R.id.btnOpen);
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/GobsFiles");
            File file = new File(directory, "/" + filename);
            if (file.exists()) {
                btnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkFileisDownloaded(filename);
                    }
                });
            }else{
                btnOpen.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.transparent));
            }

            final ImageButton btnDownload = (ImageButton) vi.findViewById(R.id.btnDownload);
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DownloadFileFromURL().execute(
                            new String[]{file_url, filename}
                    );
                }
            });
            return vi;
        }
    }


    boolean checkFileisDownloaded(String filename) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/GobsFiles");

            File file = new File(directory, "/" + filename);

            if (file.exists()) {
                Intent target = new Intent(Intent.ACTION_VIEW);
                String extension = filename.split("\\.")[1];
                target.setDataAndType(Uri.fromFile(file), "application/"+extension);
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                    Toast.makeText(getApplicationContext(), "Please install any Reader, GOBS!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //Dialog untuk loading module
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading GOBS Module, " +
                        "\nPlease Wait..");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setProgress(0);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    //Download file modulenya
    protected class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
        String filename;

        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }


        @Override
        protected String doInBackground(String... f_url) {
            try {
                publishProgress(10);
                filename = f_url[1];
                URL url = new URL(f_url[0] + filename);

                publishProgress(20);
                URLConnection connection = url.openConnection();
                connection.connect();

                publishProgress(30);

                publishProgress(40);
                InputStream input = new BufferedInputStream(url.openStream(), 1024);

                publishProgress(50);
                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsolutePath() + "/GobsFiles");

                publishProgress(60);
                directory.mkdirs();

                publishProgress(70);
                File file = new File(directory, "/" + filename);
                FileOutputStream fOut = new FileOutputStream(file);

                publishProgress(80);
                try {
                    try {
                        try {
                            byte[] buffer = new byte[1024]; // or other buffer size
                            int read;

                            publishProgress(90);
                            while ((read = input.read(buffer)) != -1) {
                                fOut.write(buffer, 0, read);
                            }
                            fOut.flush();
                        } finally {
                            fOut.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // handle exception, define IOException and others
                    }
                } finally {
                    input.close();
                }

            } catch (Exception e) {
                Log.e("Error : ", e.getMessage());
            }

            publishProgress(100);
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            pDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);

            checkFileisDownloaded(filename);
        }
    }

}

