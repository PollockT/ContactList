package com.example.contactlist;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactlist.Utils.ChangePhotoDialog;
import com.example.contactlist.Utils.DatabaseHelper;
import com.example.contactlist.Utils.Init;
import com.example.contactlist.Utils.UniversalImageLoader;
import com.example.contactlist.models.Contact;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditContactFragment extends Fragment implements ChangePhotoDialog.OnPhotoReceivedListener {
    private static final String TAG = "EditContactFragment";

    private Contact mContact;
    private EditText mName,mPhoneNumber,mEmail;
    private CircleImageView mContactImage;
    private Spinner mSelectDevice;
    private Toolbar toolbar;
    private String mSelectedImagePath;
    private int mPreviousKeyStroke;

    //This will skip the nullpointer exception when adding to a new bundle from MainActivity
    public EditContactFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editcontact, container, false);
        Log.d(TAG, "onCreateView: started.");
        mName = (EditText) view.findViewById(R.id.et_ContactName);
        mPhoneNumber = (EditText) view.findViewById(R.id.et_PhoneNumber);
        mEmail = (EditText) view.findViewById(R.id.et_EmailAddress);
        mSelectDevice = (Spinner) view.findViewById(R.id.selectDevice);
        mContactImage = (CircleImageView) view.findViewById(R.id.contactImageEdit);
        toolbar = (Toolbar) view.findViewById(R.id.contactToolbar);

        mSelectedImagePath = null;

        //fragment title bar text set
        TextView heading = (TextView) view.findViewById(R.id.tvTitleActionBar);
        heading.setText(getString(R.string.editing_contact));

        //required for setting up the toolbar for delete and further options
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        //get contact properties from bundles
        mContact = getContactFromBundle();

        if(mContact != null){
            init();
        }


        //navigation for the backarrow
        ImageView ivBackArrow = (ImageView) view.findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");
                //remove previous fragment from the BackStack (therefore navigating back)
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        //Save changes
        ImageView ivCheckMark = (ImageView) view.findViewById(R.id.ivCheckMark);
        ivCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: saving the contact info");

                if(checkStringIfNull(mName.getText().toString())){
                    Log.d(TAG, "onClick: saving changes to contact " + mName.getText().toString());

                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                    Cursor cursor = databaseHelper.getContactID(mContact);

                    int contactID = -1;
                    while(cursor.moveToNext()){
                        contactID = cursor.getInt(0);
                    }
                    if(contactID > -1){
                        if(mSelectedImagePath != null){
                            mContact.setProfileImage(mSelectedImagePath);
                        }
                        mContact.setName(mName.getText().toString());
                        mContact.setPhoneNumber(mPhoneNumber.getText().toString());
                        mContact.setDevice(mSelectDevice.getSelectedItem().toString());
                        mContact.setEmail(mEmail.getText().toString());

                        databaseHelper.updateContact(mContact,contactID);
                        Toast.makeText(getActivity(),"Contact Updated",Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onClick: CONTACT NOT UPDATED");
                        Toast.makeText(getActivity(),"Something went wrong, Contact not updated",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick:  CONTACT NOT UPDATED" + mName.getText().toString()+ "\n");
                        Log.d(TAG, "onClick:  CONTACT NOT UPDATED" + mPhoneNumber.getText().toString()+ "\n");
                        Log.d(TAG, "onClick:  CONTACT NOT UPDATED" + mSelectDevice.getSelectedItem().toString()+ "\n");
                        Log.d(TAG, "onClick:  CONTACT NOT UPDATED" + mEmail.getText().toString()+ "\n");
                        Log.d(TAG, "onClick:  CONTACT NOT UPDATED" + mSelectedImagePath.toString()+ "\n");
                    }
                }
            }
        });

        //choose for new image
        ImageView ivCamera = (ImageView) view.findViewById(R.id.ivCameraEdit);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: OPENING CAMERA ICON MULITPLE TIMES
                for( int i = 0; i < Init.PERMISSIONS.length; i++){
                    String[] permission = {Init.PERMISSIONS[i]};
                    if(((MainActivity)getActivity()).checkPermission(permission)){
                        if(i == Init.PERMISSIONS.length - 1){
                            Log.d(TAG, "onClick: opening the 'image selection dialog box'.");
                            ChangePhotoDialog dialog = new ChangePhotoDialog();
                            dialog.show(getFragmentManager(), getString(R.string.change_photo_dialog));
                            dialog.setTargetFragment(EditContactFragment.this, 0);
                        }
                    }else{
                        ((MainActivity)getActivity()).verifyPermissions(permission);
                    }
                }


            }
        });

        return view;
    }

    private boolean checkStringIfNull(String string){
        if(string.equals("")){
            return false;
        }else {
            return true;
        }
    }

    private void init() {
        mName.setText(mContact.getName());
        mPhoneNumber.setText(mContact.getPhoneNumber());
        mEmail.setText(mContact.getEmail());

        //connecting the ImageLoader
        UniversalImageLoader.setImage(mContact.getProfileImage(),mContactImage,null,"https://");

        //Connecting and setting the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.device_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectDevice.setAdapter(adapter);
        int position = adapter.getPosition(mContact.getDevice());
        mSelectDevice.setSelection(position);


    }

    private Contact getContactFromBundle(){
        Log.d(TAG, "getContactFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            return bundle.getParcelable(getString(R.string.contact));
        }else{
            return null;
        }
    }
    //delete menu part 1
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //delete menu part 2
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuitem_delete:
                Log.d(TAG, "onOptionsItemSelected: deleting contact.");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Grabs the photo from ChangePhotoDialog fragment
     * @param bitmap image
     */
    @Override
    public void getBitmapImage(Bitmap bitmap) {
        Log.d(TAG, "getBitmapImage: got the bitmap: " + bitmap);
        //get the bitmap from 'ChangePhotoDialog'
        if(bitmap != null) {
            //compress the image (if you like)
            ((MainActivity)getActivity()).compressBitmap(bitmap, 70);
            mContactImage.setImageBitmap(bitmap);
        }

    }

    @Override
    public void getImagePath(String imagePath) {
        Log.d(TAG, "getImagePath: " + imagePath);

        if(!imagePath.equals("")){
            imagePath = imagePath.replace(":/", "://");
            mSelectedImagePath = imagePath;
            UniversalImageLoader.setImage(imagePath,mContactImage,null,"");
        }
    }
    /**
     * phone number formatter
     */
    private void initTextOnChangeListener(){

        mPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mPreviousKeyStroke = keyCode;
                return false;
            }
        });

        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String number = s.toString();
                Log.d(TAG, "afterTextChanged:  " + number);

                if(number.length() == 3 && mPreviousKeyStroke != KeyEvent.KEYCODE_DEL
                        && !number.contains("(")){
                    number = String.format("(%s", s.toString().substring(0,3));
                    mPhoneNumber.setText(number);
                    mPhoneNumber.setSelection(number.length());
                }
                else if(number.length() == 5 && mPreviousKeyStroke != KeyEvent.KEYCODE_DEL
                        && !number.contains(")")){
                    number = String.format("(%s) %s",
                            s.toString().substring(1,4),
                            s.toString().substring(4,5));
                    mPhoneNumber.setText(number);
                    mPhoneNumber.setSelection(number.length());
                }
                else if(number.length() ==10 && mPreviousKeyStroke != KeyEvent.KEYCODE_DEL
                        && !number.contains("-")){
                    number = String.format("(%s) %s-%s",
                            s.toString().substring(1,4),
                            s.toString().substring(6,9),
                            s.toString().substring(9,10));
                    mPhoneNumber.setText(number);
                    mPhoneNumber.setSelection(number.length());

                }
            }
        });
    }
}