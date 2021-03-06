package pt.IPG.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pt.IPG.messenger.recylcerchat.ChatData;
import pt.IPG.messenger.recylcerchat.ConversationRecyclerView;


public class Conversation extends BaseActivity  {

    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private EditText text;
    private Button send;

    // IPG - Alteração -------------- Dinis
    private Encryption encryption;

    String room = "";
    String ID = "";

    // IPG - Alteração -------------- Daey
    private Socket mSocket;
    {
        try {
            //mSocket = IO.socket("http://chat-ipg.azurewebsites.net");
            mSocket = IO.socket("http://chat-ipg-04.azurewebsites.net");
        } catch (URISyntaxException e) {}
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Conversation.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject dataIO = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = dataIO.getString("username");
                        message = dataIO.getString("message");

                    } catch (JSONException e) {
                        return;
                    }


                    if(!username.equals(ID)){
                        //problema com broadcast to self

                        List<ChatData> data = new ArrayList<ChatData>();
                        ChatData item = new ChatData();
                        item.setTime("6:00pm");
                        item.setType("1");

                        // IPG - Alteração -------------- Dinis
                        try {
                            item.setText(message);
                            // DINIS .. não funciona aqui quando recebo do servidor
                            // item.setText(new String(encryption.Decrypt(message)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        data.add(item);
                        mAdapter.addItem(data);
                        try {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //text.setText("");

                    }

                }

            });
        }


    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("refresh messages", onNewMessage);
    }
    // IPG - Alteração -------------- Daey



    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ConversationRecyclerView(this,setData());
        room = getIntent().getExtras().getString("roomName",null);
        ID = getIntent().getExtras().getString("ID",null);

        setContentView(R.layout.activity_conversation);
        setupToolbarWithUpNav(R.id.toolbar, "Alterar para API getuser" , R.drawable.ic_action_back);



        // receber conversa do mongodb
        AsyncTask.execute(new Runnable() {
            List<ChatData> data = new ArrayList<ChatData>();

            @Override
            public void run() {
                //TODO your background code
                //retrieve
                String result =  getJSONFromUrl(room);

                try {
                    JSONObject jsonRoot  = new JSONObject(result);
                    JSONArray jsonData = jsonRoot.getJSONArray("conversation");


                    for (int i = 0; i < jsonData.length(); i++) {
                        ChatData item = new ChatData();
                        String time = jsonData.getJSONObject(i).getString("createdAt");
                        //2019-02-19T12:24:06.557Z
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date = format.parse(time.replaceAll("Z$", "+0000"));


                        item.setTime( newFormat.format(date));
                        String author = jsonData.getJSONObject(i).getJSONObject("author").getString("_id");
                        if (!author.equals(ID)) {
                            item.setType("1");
                        }else{item.setType("2");}
                        String body = jsonData.getJSONObject(i).getString("body");
                        item.setText(body);

                        data.add(item);
                    }
                    // update do UI deve ser feito pelo UI
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Collections.reverse(data);
                            mAdapter.addItem(data);
                            // Stuff that updates the UI

                        }
                    });

                } catch (JSONException e) {
                    //   System.out.println(e.getMessage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        //--fim receber conversas
        // IPG - Alteração -------------- Dinis
        encryption = new Encryption();

        // IPG - Alteração -------------- Daey
        mSocket.emit("enter conversation", room);
        mSocket.on("refresh messages", onNewMessage);
        ///mSocket.emit("new message", "Hello !!!!!");


        mSocket.connect();
        // IPG - Alteração -------------- Daey


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);

        text = (EditText) findViewById(R.id.et_message);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
            }
        });
        send = (Button) findViewById(R.id.bt_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!text.getText().equals("")){
                    List<ChatData> data = new ArrayList<ChatData>();
                    ChatData item = new ChatData();
                    item.setTime("6:00pm");
                    item.setType("2");
                    item.setText(text.getText().toString());
                    data.add(item);
                    mAdapter.addItem(data);

                    // IPG - Alteração -------------- Dinis
                    try {
                        // background para fazer cenas na base de dados mongop
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //TODO your background code
                                // mongoDB save stuff
                                sendReplyToConversation(room, text.getText().toString());
                            }
                        });


                        //mSocket.emit("new message", room, encryption.Encrypt(text.getText().toString(), Encryption.MessageType.Encrypted), ID);
                        mSocket.emit("new message", room, text.getText() , ID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    //mSocket.emit("refresh messages", text.getText().toString());

                    try {
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    text.setText("");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
       // Toast.makeText(this,"Fechar Socket!!",Toast.LENGTH_LONG).show();
        // fechar socket!!!
       // mSocket.disconnect();
      //  mSocket.emit("leave-room", room);
      //  mSocket.off("new message", onNewMessage);
        finish();
        return;
    }


    public List<ChatData> setData(){
        List<ChatData> data = new ArrayList<>();
        return data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userphoto, menu);
        return true;
    }


    public String getJSONFromUrl(String ConversationID) {
        SharedPreferences settings = getApplication().getSharedPreferences("myPrefs", 0);
        String tokenOK = settings.getString("token", ""/*default value*/);
        String result ="";
        try {
            //Connect
            // cache problema .... + "?_=" + System.currentTimeMillis()
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://chat-ipg-04.azurewebsites.net/api/chat/"+ConversationID+ "?_=" + System.currentTimeMillis()).openConnection());
            //   urlConnection.setDoOutput(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(false);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", tokenOK);

            urlConnection.connect();
            urlConnection.setConnectTimeout(10000);


            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            result = sb.toString();
            urlConnection.disconnect();

        } catch (UnsupportedEncodingException e){
            return result;
            //  e.printStackTrace();
        } catch (IOException e) {
            return result;
            // e.printStackTrace();
        }

        return result;

    }


    public String sendReplyToConversation(String ConversationID, String msg) {
        SharedPreferences settings = getApplication().getSharedPreferences("myPrefs", 0);
        String tokenOK = settings.getString("token", ""/*default value*/);

        String result ="";
        try {
            //Connect
            HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://chat-ipg-04.azurewebsites.net/api/chat/"+ConversationID).openConnection());
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", tokenOK);

            String params =  "composedMessage="+msg;
            urlConnection.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));

            urlConnection.connect();
            urlConnection.setConnectTimeout(10000);

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(params);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
            result = sb.toString();



        } catch (UnsupportedEncodingException e){
            return result;
            //  e.printStackTrace();
        } catch (IOException e) {
            return result;
            // e.printStackTrace();
        }
        return result;

    }


}
