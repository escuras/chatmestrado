package pt.IPG.messenger;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import pt.IPG.messenger.recylcerchat.ChatData;
import pt.IPG.messenger.recylcerchat.ConversationRecyclerView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;



import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Conversation extends BaseActivity  {

    private RecyclerView mRecyclerView;
    private ConversationRecyclerView mAdapter;
    private EditText text;
    private Button send;



    // IPG - Alteração -------------- Daey
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://chat-ipg.azurewebsites.net");
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
        mSocket.off("new message", onNewMessage);
    }
    // IPG - Alteração -------------- Daey

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        setupToolbarWithUpNav(R.id.toolbar, "Julia Harriss", R.drawable.ic_action_back);


        // IPG - Alteração -------------- Daey
        mSocket.on("new message", onNewMessage);
        mSocket.emit("add user", "Android");
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

                    mSocket.emit("new message", text.getText().toString());

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

    public List<ChatData> setData(){
        List<ChatData> data = new ArrayList<>();
/*
        String text[] = {"15 September","Hi, Julia! How are you?", "Hi, Joe, looks great! :) ", "I'm fine. Wanna go out somewhere?", "Yes! Coffe maybe?", "Great idea! You can come 9:00 pm? :)))", "Ok!", "Ow my good, this Kit is totally awesome", "Can you provide other kit?", "I don't have much time, :`("};
        String time[] = {"", "5:30pm", "5:35pm", "5:36pm", "5:40pm", "5:41pm", "5:42pm", "5:40pm", "5:41pm", "5:42pm"};
        String type[] = {"0", "2", "1", "1", "2", "1", "2", "2", "2", "1"};

        for (int i=0; i<text.length; i++){
            ChatData item = new ChatData();
            item.setType(type[i]);
            item.setText(text[i]);
            item.setTime(time[i]);
            data.add(item);
        }
*/
        return data;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userphoto, menu);
        return true;
    }
}
