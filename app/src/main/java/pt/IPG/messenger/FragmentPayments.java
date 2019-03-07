package pt.IPG.messenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pt.IPG.messenger.recyclerview.Contact;
import pt.IPG.messenger.recyclerview.ContactAdapter;

public class FragmentPayments extends Fragment implements ContactAdapter.ViewHolder.ClickListener{
    private RecyclerView mRecyclerView;
    private MainActivity activityHome;
    private ContactAdapter mAdapter;

    @Override
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_payments, null, false);

        activityHome = ((MainActivity)getActivity());
        activityHome.changeTitle(R.id.toolbar, "Transfer money");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerPayments);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ContactAdapter(getContext(),setData(),this);
        mRecyclerView.setAdapter (mAdapter);

        return view;
    }

    public List<Contact> setData(){
        FragmentContacts fragmentContacts = new FragmentContacts();
        return fragmentContacts.setData();
    }

    @Override
    public void onItemClicked (int position) {
        List<Contact> contacts = mAdapter.getContacts();
        Contact contact = contacts.get(position);
        FragmentPay fragmentPay = new FragmentPay();
        Bundle args = new Bundle();
        args.putSerializable("contact", contact);
        fragmentPay.setArguments(args);
        FragmentTransaction ft = activityHome.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragmentPay).commit();
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_back:
                FragmentContacts fragmentContacts = new FragmentContacts();
                FragmentTransaction ft = activityHome.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frameLayout, fragmentContacts).commit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_back, menu);
    }
}
