package com.example.contactlist.Utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.sax.TextElementListener;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.contactlist.MainActivity;
import com.example.contactlist.R;
import com.example.contactlist.models.Contact;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class ContactListProperty extends ArrayAdapter<String> {
    private static final String TAG = "ContactListProperty";

    private LayoutInflater mInflater;
    private List<String> mProperties = null;
    private int layoutResource;
    private Context mContext;
    private String mAppend;

    public ContactListProperty(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> properties) {
        super(context, resource, properties);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContext = context;
        this.mProperties = properties;


    }
    //---------------------------Stuff to change--------------------------------------------
    private static class ViewHolder{
        TextView textProperty;
        ImageView rightIcon;
        ImageView leftIcon;
    }
    //--------------------------------------------------------------------------------------
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
         ************ ViewHolder Build Pattern Start ************
         */
        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            //---------------------------Stuff to change--------------------------------------------
            holder.textProperty = (TextView) convertView.findViewById(R.id.tvMiddleCardView);
            holder.rightIcon = (ImageView) convertView.findViewById(R.id.iconRightCardView);
            holder.leftIcon = (ImageView) convertView.findViewById(R.id.iconLeftCardView);
            //--------------------------------------------------------------------------------------


            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        //---------------------------Stuff to change--------------------------------------------
        final String textProperty = getItem(position);
        holder.textProperty.setText(textProperty);

        //--------------------------logic-------------------------------------------------------

        //decide if email or phone number
        //check if email
        if(textProperty.contains("@")){
            holder.leftIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_email",null,mContext.getPackageName()));
            holder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Email Icon clicked");
                    Intent email = new Intent(Intent.ACTION_SEND);

                    email.setType("plain/text");

                    email.putExtra(Intent.EXTRA_EMAIL,new String[]{textProperty});

                }
            });
        }
        //falls to phone by default if not email
        else if((textProperty.length() != 0)){
            holder.leftIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_phone",null,mContext.getPackageName()));
            holder.rightIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_message",null,mContext.getPackageName()));

            holder.rightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: TextMessage icon clicked");
                    Intent textMessage = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",textProperty,null));
                    mContext.startActivity(textMessage);
                }
            });

            holder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: phone icon clicked");
                    if(((MainActivity)mContext).checkPermission(Init.PHONE_PERMISSIONS)){
                        Log.d(TAG, "onClick: starting phone call");
                        Intent call = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", textProperty , null));
                        mContext.startActivity(call);
                    }else{
                        ((MainActivity)mContext).verifyPermissions(Init.PHONE_PERMISSIONS);
                        Log.d(TAG, "onClick: phone call intent failed");
                    }

                }
            });
        }

        //--------------------------------------------------------------------------------------

        return convertView;
    }
}


