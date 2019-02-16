package pt.IPG.messenger;

import android.content.Context;
import android.os.Bundle;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
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

                 /*   ChatView chatView = (ChatView) findViewById(R.id.chat_view);
                    // add the message to view
                    //   chatView.addMessage(new ChatMessage("Message received", System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
                    chatView.addMessage(new ChatMessage(message,
                            System.currentTimeMillis(), ChatMessage.Type.RECEIVED, username));
                */

                 //problema com broadcast to self

                        List<ChatData> data = new ArrayList<ChatData>();
                        ChatData item = new ChatData();
                        item.setTime("6:00pm");
                        item.setType("1");
                        item.setText(message);
                        data.add(item);
                        mAdapter.addItem(data);
                        try {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() -1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //text.setText("");


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
        setContentView(R.layout.activity_conversation);

        setupToolbarWithUpNav(R.id.toolbar, "Julia do Trabalho", R.drawable.ic_action_back);

      //  room = getIntent().getExtras().getString("roomName","defaultKey");

        // IPG - Alteração -------------- Dinis
        encryption = new Encryption();

        // IPG - Alteração -------------- Daey
        mSocket.emit("enter conversation", "5c669ed2e43e3d3e244f4ae8");
        mSocket.on("refresh messages", onNewMessage);
        ///mSocket.emit("new message", "Hello !!!!!");


        mSocket.connect();
        // IPG - Alteração -------------- Daey


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationRecyclerView(this,setData());
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
                    mSocket.emit("new message", "5c669ed2e43e3d3e244f4ae8",text.getText().toString(), "USER");
                   // mSocket.emit("refresh messages", text.getText().toString());

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
        Toast.makeText(this,"Fechar Socket!!",Toast.LENGTH_LONG).show();
        // fechar socket!!!
       // mSocket.disconnect();
      //  mSocket.emit("leave-room", room);
      //  mSocket.off("new message", onNewMessage);
        finish();
        return;
    }


    public List<ChatData> setData(){
        List<ChatData> data = new ArrayList<>();
/*
        String text[] = {"15 September","Hi, Julia! How are you?", "Hi, Joe, looks great! :) ", "I'm fine. Wanna go out somewhere?", "Yes! Coffe maybe?", "Great idea! You can come 9:00 pm? :)))", "Ok!", "Ow my good, this Kit is totally awesome", "Can you provide other kit?", "I don't have much time, :`("};
        String time[] = {"", "5:30pm", "5:35pm", "5:36pm", "5:40pm", "5:41pm", "5:42pm", "5:40pm", "5:41pm", "5:42pm"};
        String type[] = {"0", "2", "1", "1", "2", "1", "2", "2", "2", "1"};



        String jsonText = new Gson().toJson(text);
        String jsonTime = new Gson().toJson(time);
        String jsonType = new Gson().toJson(type);

        create(this, "storage_text.json", jsonText);
        create(this, "storage_time.json", jsonTime);
        create(this, "storage_type.json", jsonType);
*/

/*
        String storagejsonText = read(this, "storage_text.json");
        String[] text = storagejsonText.substring(1, storagejsonText.length()-1).split("\",\""); //remove [ and ] , then split by ','
        String storagejsonTime = read(this, "storage_time.json");
        String[] time = storagejsonTime.substring(1, storagejsonTime.length()-1).split("\",\"");//remove [ and ] , then split by ','
        String storagejsonType = read(this, "storage_type.json");
        String[] type = storagejsonType.substring(1, storagejsonType.length()-1).split("\",\""); //remove [ and ] , then split by ','



        for (int i=0; i<text.length; i++){
            ChatData item = new ChatData();
            item.setType(type[i]);
            item.setText(text[i]);
            item.setTime(time[i]);
            data.add(item);
        }
*/
// ler
        return data;

    }

    public static class ArrayUtil
    {
        public static ArrayList<Object> convert(JSONArray jArr)
        {
            ArrayList<Object> list = new ArrayList<Object>();
            try {
                for (int i=0, l=jArr.length(); i<l; i++){
                    list.add(jArr.get(i));
                }
            } catch (JSONException e) {}

            return list;
        }

        public static JSONArray convert(Collection<Object> list)
        {
            return new JSONArray(list);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userphoto, menu);
        return true;
    }



    // UTIL
    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean create(Context context, String fileName, String jsonString){
        String FILENAME = "storage.json";
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }


}
