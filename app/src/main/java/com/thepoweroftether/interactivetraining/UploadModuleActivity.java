package com.thepoweroftether.interactivetraining;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UploadModuleActivity extends AppCompatActivity {

    //Alert Message
    public static String alertMessage = "";

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    String fileSize;
    String filter = "PDF";
    String title, caption, filename, username;
    EditText otherFilter, titleEdit, captionEdit;
    TextView uploadText;
    Button uploadButton, pickButton;
    RadioGroup filterFile;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;

    String upLoadServerUri = null;

    /**********  File Path *************/
    //final String uploadFilePath = "/storage/emulated/0/Download/";
    //final String uploadFileName = "Yoona-SNSD-1.jpg";

    private static final String url_upload = Server.URL + Server.upload;
    private static final String url_insert_upload = Server.URL + Server.insert_upload;
    private static final String TAG_SUCCESS = "success";

    private static String fileUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_module);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Extra intent
        Intent i = getIntent();
        username = i.getStringExtra("USERNAME");

        uploadButton = (Button)findViewById(R.id.upload_button);
        uploadText  = (TextView)findViewById(R.id.upload_text);

        pickButton = (Button)findViewById(R.id.pick_button);

        uploadText.setText("Uploading file path :- ");

        /************* Php script path ****************/
        upLoadServerUri = url_upload;

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(UploadModuleActivity.this);
                dialog.setMessage("Uploading file");
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.show();

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                uploadText.setText("Uploading started");
                            }
                        });
                        //uploadFile(uploadFilePath + "" + uploadFileName);
                        uploadFile(fileUpload);
                    }
                }).start();

                titleEdit = (EditText) findViewById(R.id.title_edit);
                captionEdit = (EditText) findViewById(R.id.caption_edit);

                title = titleEdit.getText().toString();
                caption = captionEdit.getText().toString();

                if (title.equals("") || caption.equals("")) {
                    Toast.makeText(
                            UploadModuleActivity.this,
                            "Error! Your cannot submit an empty field(s).",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    new InsertUploadFile().execute();
                }
            }
        });

        //RADIO BUTTON FILTER
        filterFile = (RadioGroup) findViewById(R.id.filter_file);
        filterFile.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadio = (RadioButton) findViewById(
                        filterFile.getCheckedRadioButtonId()
                );

                otherFilter = (EditText) findViewById(R.id.other_filter);
                if (selectedRadio.getText().toString().equals("OTHER")){
                    otherFilter.setVisibility(View.VISIBLE);
                    filter = otherFilter.getText().toString();
                }
                else {
                    otherFilter.setVisibility(View.GONE);
                    filter = selectedRadio.getText().toString();
                }
            }
        });

        //#########################################

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileDialog fileDialog;
                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                fileDialog = new FileDialog(UploadModuleActivity.this, mPath, "." + filter.toLowerCase());
                //fileDialog = new FileDialog(UploadModuleActivity.this, mPath, ".*");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        Log.d(getClass().getName(), "selected file " + file.toString());
                        long size = file.length();
                        if (size > 5 * 1024 * 1024) {
                            Toast.makeText(
                                    UploadModuleActivity.this,
                                    "Cannot add this file, file size is too big",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            if (size/1024/1024 > 1)
                                fileSize = String.format("%.2f", (((float) size)/1024/1024)) + " MB";
                            else {
                                fileSize = String.format("%.2f", (((float) size)/1024)) + " KB";
                            }
                            fileUpload = file.toString();
                            int lastIndexSlash = fileUpload.lastIndexOf("/");
                            filename = fileUpload.substring(lastIndexSlash+1);
                            //filename = "Module_"+title;
                            //filename = fileUpload.substring(fileUpload.charAt());
                            uploadText.setText("Uploading file path :- '"+fileUpload+"' (Size : " + fileSize + ")");
                        }
                    }
                });
                fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                  public void directorySelected(File directory) {
                      Log.d(getClass().getName(), "selected dir " + directory.toString());
                  }
                });
                fileDialog.setSelectDirectoryOption(false);
                fileDialog.showDialog();
            }
        });
    }

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            /*Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);*/
            Log.e("uploadFile", "Source File not exist :"+fileUpload);

            runOnUiThread(new Runnable() {
                public void run() {
                    //uploadText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
                    uploadText.setText("Source File not exist :" +fileUpload);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename="
                                + fileName + "" + lineEnd);

                        dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "Upload file '"+fileUpload+"' completed";

                            uploadText.setText(msg);
                            Toast.makeText(UploadModuleActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        uploadText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(UploadModuleActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        uploadText.setText("Got Exception : see logcat ");
                        Toast.makeText(UploadModuleActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload server Exception", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    class InsertUploadFile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UploadModuleActivity.this);
            pDialog.setMessage("Saving file");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Pair<String, String>> args =
                    new ArrayList<Pair<String, String>>();
            args.add(new Pair<>("title", title));
            args.add(new Pair<>("caption", caption));
            args.add(new Pair<>("filename", filename));
            args.add(new Pair<>("username", username));
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonParser.makeHttpRequest(url_insert_upload, "POST", args);
            }catch (IOException e){
                Log.d("Networking", e.getLocalizedMessage());
            }

            Log.d("Create response", jsonObject.toString());

            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    alertMessage = "Upload file successful.";
                    //finish();
                }
                else{
                    alertMessage = "Error! Upload file failed.";
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            /*Toast.makeText(
                    getApplicationContext(),
                    alertMessage,
                    Toast.LENGTH_SHORT)
                    .show();*/
        }
    }
}

class FileDialog {
    private static final String PARENT_DIR = "..";
    private final String TAG = getClass().getName();
    private String[] fileList;
    private File currentPath;
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialog.DirectorySelectedListener>();
    private final Activity activity;
    private boolean selectDirectoryOption;
    private String fileEndsWith;

    /**
     * @param activity
     * @param initialPath
     */
    public FileDialog(Activity activity, File initialPath) {
        this(activity, initialPath, null);
    }

    public FileDialog(Activity activity, File initialPath, String fileEndsWith) {
        this.activity = activity;
        setFileEndsWith(fileEndsWith);
        if (!initialPath.exists()) initialPath = Environment.getExternalStorageDirectory();
        loadFileList(initialPath);
    }

    /**
     * @return file dialog
     */
    public Dialog createFileDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select directory", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, currentPath.getPath());
                    fireDirectorySelectedEvent(currentPath);
                }
            });
        }

        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
            }
        });

        dialog = builder.show();
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new ListenerList.FireHandler<DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(directory);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            //Check is root
            if (currentPath.getPath() != Environment.getExternalStorageDirectory().toString()){
                if (path.getParentFile() != null) r.add(PARENT_DIR);
            }
            else {
                r.remove(PARENT_DIR);
            }

            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    if (selectDirectoryOption) return sel.isDirectory();
                    else {
                        boolean endsWith = fileEndsWith != null ? filename.toLowerCase().endsWith(fileEndsWith) : true;
                        return endsWith || sel.isDirectory();
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            for (String file : fileList1) {
                r.add(file);
            }
        }
        fileList = (String[]) r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    private void setFileEndsWith(String fileEndsWith) {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase() : fileEndsWith;
    }
}

class ListenerList<L> {
    private List<L> listenerList = new ArrayList<L>();

    public interface FireHandler<L> {
        void fireEvent(L listener);
    }

    public void add(L listener) {
        listenerList.add(listener);
    }

    public void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<L>(listenerList);
        for (L l : copy) {
            fireHandler.fireEvent(l);
        }
    }

    public void remove(L listener) {
        listenerList.remove(listener);
    }

    public List<L> getListenerList() {
        return listenerList;
    }
}
