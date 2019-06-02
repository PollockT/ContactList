package com.example.contactlist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.contactlist.Utils.ContactListAdapter;
import com.example.contactlist.Utils.DatabaseHelper;
import com.example.contactlist.models.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ViewContactsFragment extends Fragment {

    private static final String TAG = "ViewContactsFragment";

    private String imageURL0 = "photos2.fotosearch.com/bthumb/CSP/CSP881/ninja-mascot-clip-art__k25046218.jpg";
    private String imageURL1 = "lh3.googleusercontent.com/-5s6sZRQzcxc/UcztTeDJq8I/AAAAAAAAAHE/v-PgdSa5O3Y/s396/Photo%252520Jun%25252022%25252C%2525202013%25252C%2525202%25253A40%252520AM.jpg";
    private String imageURL2 = "lumiere-a.akamaihd.net/v1/images/luke-skywalker-i-am-a-jedi_fce1d84d.jpeg?region=204%2C0%2C414%2C413";
    private String imageURL3 = "na.rdcpix.com/766640868/8ecb8b5c10a19ccbe1daba7bc38ec77cw-c320763xd-w685_h860_q80.jpg";
    private String imageURL4 = "66.media.tumblr.com/baea33b98e5aa66abd0e5da888d06c44/tumblr_pkw4y0EZTh1uk7v3v_540.jpg";
    private String imageURL5 = "us.123rf.com/450wm/baggira/baggira1703/baggira170300027/75539553-a-ferocious-evil-cat-on-the-windowsill-on-the-street-angry-mistrustful-cussing-cat-the-cat-looks-mal.jpg?ver=6";
    private String imageURL6 = "bookmole1.files.wordpress.com/2010/10/picard.jpg";

    private EditText  mSearchContacts;

    public interface  OnAddContactListener{
        public void onAddContact();
    }
    OnAddContactListener mOnAddContact;

    public interface OnContactSelectedListener{
        public void OnContactSelected(Contact con);
    }
    OnContactSelectedListener mContactListener;


    //variables and widgets
    private static final int STANDARD = 0;
    private static final int SEARCH = 1;
    private int mAppBarState;


    private AppBarLayout viewContactsBar, searchBar;
    private ContactListAdapter adapter;
    private ListView contactsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewcontacts, container, false);
        viewContactsBar = (AppBarLayout) view.findViewById(R.id.viewContactsToolbar);
        searchBar = (AppBarLayout) view.findViewById(R.id.searchToolbar);
        contactsList = (ListView) view.findViewById(R.id.contactsList);
        mSearchContacts = (EditText) view.findViewById(R.id.etSearchContacts);
        Log.d(TAG, "onCreateView: started.");


        setAppBarState(STANDARD);

        setupContactsList();

        // navigate to add contacts fragment
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked fab.");
                mOnAddContact.onAddContact();
            }
        });

        ImageView ivSearchContact = (ImageView) view.findViewById(R.id.ivSearchIcon);
        ivSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked search icon.");
                toggleToolBarState();
            }
        });

        ImageView ivBackArrow = (ImageView) view.findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");
                toggleToolBarState();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mContactListener = (OnContactSelectedListener) getActivity();
            mOnAddContact = (OnAddContactListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    //
    private void setupContactsList(){
        final ArrayList<Contact> contacts = new ArrayList<>();
        Log.d(TAG, "setupContactsList: contact list populating");

  //      contacts.add(new Contact("Theodore Pollock","(555)555-5555","mobile","pollock@gmail.com",imageURL0));
  //      contacts.add(new Contact("Katherine Janeway","(555)555-4326","mobile","janeway@gmail.com",imageURL1));//
  //      contacts.add(new Contact("Luke Skywalker","(555)555-2654","mobile","Skywalker@gmail.com",imageURL2));//
  //      contacts.add(new Contact("Zach Baggins","(555)555-3543","mobile","Baggins@gmail.com",imageURL3));//
  //      contacts.add(new Contact("Rangy Cat","(614)205-0940","land","cat@gmail.com",imageURL4));//
  //      contacts.add(new Contact("Rusty Cat","(614)296-1153","land","cat2@gmail.com",imageURL5));//
  //      contacts.add(new Contact("Jean-Luk Picard","(555)555-1642","mobile","Picard@gmail.com",imageURL6));//

        DatabaseHelper databaseHelper= new DatabaseHelper(getActivity());
        Cursor cursor = databaseHelper.getAllContacts();

        if(!cursor.moveToNext()){
            Toast.makeText(getActivity(),"There are no contacts to show", Toast.LENGTH_SHORT).show();
        }

        while(cursor.moveToNext()){
            contacts.add(new Contact(
                    cursor.getString(1),//mName
                    cursor.getString(2),//mPhoneNumber
                    cursor.getString(3),//device
                    cursor.getString(4),//email
                    cursor.getString(5)//avatar
                    ));
        }

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        adapter = new ContactListAdapter(getActivity(), R.layout.layout_contactslistitem, contacts, "");
        //search function added before adapter set
        //TODO: DON'T ABSTRACTLY GRAB LETTERS FROM ANY CONTACT
        mSearchContacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contactsList.setAdapter(adapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onClick: navigating to " + getString(R.string.contact_fragment));

                //pass the contact to the interface and send it to MainActivity
                mContactListener.OnContactSelected(contacts.get(position));
            }
        });


    }

    /**
     * Initiates the appbar state toggle
     */
    private void toggleToolBarState() {
        Log.d(TAG, "toggleToolBarState: toggling AppBarState.");
        if(mAppBarState == STANDARD){
            setAppBarState(SEARCH);
        }else{
            setAppBarState(STANDARD);
        }
    }

    /**
     * Sets the appbar state for either the search 'mode' or 'standard' mode
     * @param state
     */
    private void setAppBarState(int state) {
        Log.d(TAG, "setAppBarState: changing app bar state to: " + state);

        mAppBarState = state;

        if(mAppBarState == STANDARD){
            Log.d(TAG, "setAppBarState: appbar state set to 0");
            searchBar.setVisibility(View.GONE);
            viewContactsBar.setVisibility(View.VISIBLE);

            //hide the keyboard
            View view = getView();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            try{
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (NullPointerException e){
                Log.d(TAG, "setAppBarState: NullPointerException: " + e.getMessage());
            }
        }

        else if(mAppBarState == SEARCH){
            Log.d(TAG, "setAppBarState: appbar state set to 1");
            viewContactsBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);

            //open the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setAppBarState(STANDARD);
    }
}