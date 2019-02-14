package pt.IPG.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.IPG.messenger.R;

import pt.IPG.messenger.recyclerview.Chat;
import pt.IPG.messenger.recyclerview.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dytstudio.
 */

public class FragmentHome extends Fragment implements ChatAdapter.ViewHolder.ClickListener {
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private TextView tv_selection;

    public FragmentHome(){
        setHasOptionsMenu(true);
    }
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);

        getActivity().supportInvalidateOptionsMenu();
        ((MainActivity)getActivity()).changeTitle(R.id.toolbar, "Messages");

        tv_selection = (TextView) view.findViewById(R.id.tv_selection);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ChatAdapter(getContext(),setData(),this);
        mRecyclerView.setAdapter (mAdapter);

        return view;
    }
    public List<Chat> setData(){
        List<Chat> data = new ArrayList<>();
        String name[]= {"Rita  Gomes", "Vicente  Pimentel", "Melissa  Andrade", "Filomena  Nascimento", "Carlos  Sá", "Cristiana  Valente", "Nádia  Lima", "Kevin  Cerqueira", "Manuel  Gonçalves", "Benedita  Fontes" };
        String lastchat[]= {"Última conversa", "Última conversa", "Última conversa", "Última conversa", "Última conversa", "Última conversa", "Última conversa", "Última conversa", "Última conversa", "Última conversa" };

        @DrawableRes int img[]= {R.drawable.user1 , R.drawable.user1, R.drawable.user1, R.drawable.user1, R.drawable.user1 , R.drawable.user1 , R.drawable.user1, R.drawable.user1, R.drawable.user1, R.drawable.user1 };
        boolean online[] = {true, false, true, false, true, true, true, false, false, true};

        for (int i = 0; i<10; i++){
            Chat chat = new Chat();
            chat.setmTime("5:04pm");
            chat.setName(name[i]);
            chat.setImage(img[i]);
            chat.setOnline(online[i]);
            chat.setLastChat(lastchat[i]);
            data.add(chat);
        }
        return data;
    }

    @Override
    public void onItemClicked (int position) {
        // passar informação sobre conversa
        Intent intent = new Intent(getActivity(), Conversation.class);
        intent.putExtra("roomName", "ROOM_B");  // pass your values and retrieve them in the other Activity using keyName
        startActivity(intent);
     }

    @Override
    public boolean onItemLongClicked (int position) {
        toggleSelection(position);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection (position);
        if (mAdapter.getSelectedItemCount()>0){
            tv_selection.setVisibility(View.VISIBLE);
        }else
            tv_selection.setVisibility(View.GONE);


        getActivity().runOnUiThread(new Runnable() {
            public void run()
            {
                tv_selection.setText("Delete ("+mAdapter.getSelectedItemCount()+")");
            }
        });

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_edit, menu);
    }
}
