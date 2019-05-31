package com.example.contactlist;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.contactlist.Utils.UniversalImageLoader;
import com.example.contactlist.models.Contact;
import com.nostra13.universalimageloader.core.ImageLoader;




public class MainActivity extends AppCompatActivity implements
        ViewContactsFragment.OnContactSelectedListener,
        ContactFragment.OnEditContactListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;

    /**
     *  Logic that moves from list view to view a contact's parameter in the layout form
     * @param contact the part of the array that contains an individual's parameters
     */
    @Override
    public void OnContactSelected(Contact contact) {
        Log.d(TAG, "OnContactSelected: contact selected from "
                + getString(R.string.view_contacts_fragment)
                + " " + contact.getName());

        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.contact), contact);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(getString(R.string.contact_fragment));
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started. Contact List Application Started!");

        initImageLoader();


        init();

    }

    /**
     * initialize the first fragment (ViewContactsFragment)
     */
    private void init(){
        ViewContactsFragment fragment = new ViewContactsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // reaplce whatever is in the fragment_container view with this fragment,
        // amd add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(MainActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Generalized method to call for permissions of any array
     * @param permissions the action permission is being asked to have access to
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: asking user for permission");
        ActivityCompat.requestPermissions(MainActivity.this,permissions,REQUEST_CODE);
    }

    /**
     * Checks for permissions one at a time in the background
     * @param permissions the action permission is being asked to have access to
     * @return true or false
     */
    public boolean  checkPermission(String[] permissions){
        Log.d(TAG, "checkPermission: checking that permission " + permissions[0] + "was granted");

        int permissionAceccess = ActivityCompat.checkSelfPermission(MainActivity.this,permissions[0]);

        if(permissionAceccess != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermission: "+" permission was denied");
            //TODO TOAST RE-REQUESTING PERMISSION
            return false;
        }else{
            Log.d(TAG, "checkPermission: "+" permission was granted");
            return true;
        }

    }


    /**
     * 3rd permission checking method
     * @param requestCode type of permission
     * @param permissions string array it belongs in
     * @param grantResults result status
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: requestCode = " + requestCode);

        switch(requestCode){
            case REQUEST_CODE:
                for(int i = 0; i < permissions.length; i++){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG, "onRequestPermissionsResult: "+permissions[i]+" granted permission verified");
                    }else{
                        break;
                    }
                }
                break;
        }

    }

    /**
     * Edit contact layout logic that collects the bundle info and moves it
     * @param contact the part of the array that contains an individual's parameters
     */
    @Override
    public void onEditContactSelected(Contact contact) {
        Log.d(TAG, "onEditContactSelected: contact selected from "
                + getString(R.string.edit_contact_fragment)
                + " " + contact.getName());

        EditContactFragment fragment = new EditContactFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.contact), contact);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(getString(R.string.edit_contact_fragment));
        transaction.commit();

    }
}