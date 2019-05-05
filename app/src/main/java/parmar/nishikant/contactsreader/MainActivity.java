package parmar.nishikant.contactsreader;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {


                //get contacts "once" details
                fetchContacts();


                //syncing
                getApplication().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,new MyContentObserver(new Handler(),getApplicationContext()));

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 0x1);

            }
        } catch (Exception e) {
        }
    }
    public void fetchContacts(){
        List<ContactDetails> contactDetailsList;
        contactDetailsList = new ArrayList<>();
        ListView contactlistview = findViewById(R.id.contactlistview);
        TextView title = findViewById(R.id.ContactsReader);
        ContentResolver cr = getApplicationContext().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor!=null && cursor.getCount()>0){
            int count = cursor.getCount();
            cursor.moveToFirst();
            int counter = 1;
            while(counter<=count){
                ContactDetails contactDetails = new ContactDetails();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String photouri =  cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Cursor cellNocursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"= ? ",new  String[]{id},null);
                ArrayList<String> cellNolist = new ArrayList<>();
                while(cellNocursor.moveToNext()){
                    String cellNo = cellNocursor.getString(cellNocursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    cellNolist.add(cellNo);
                }

                ArrayList<String> emaillist = new ArrayList<>();
                Cursor emailcursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID+"= ? ",new  String[]{id},null);
                while(emailcursor.moveToNext()){
                    String email = emailcursor.getString(emailcursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    emaillist.add(email);
                }

                contactDetails.fullname=name;
                contactDetails.photouri=photouri;
                contactDetails.cellnolist=cellNolist;
                contactDetails.emaillist=emaillist;
                contactDetailsList.add(contactDetails);


                if(count==counter){
                    adapter madapter = new adapter();
                    madapter.setContactDetailsList(contactDetailsList);
                    madapter.contactDetailsList=contactDetailsList;
                    contactlistview.setAdapter(madapter);
                    title.setText(Integer.toString(contactDetailsList.size())+" Contacts Found");
                    Log.i("Set","Adapter set");


                }
                counter++;
                cursor.moveToNext();

            }
        }



    }


    public class MyContentObserver extends ContentObserver {
        private Context context;
        public MyContentObserver(Handler handler) {
            super(handler);
        }
        public MyContentObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            try {
                Log.i("name","Change");
                fetchContacts();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class adapter extends BaseAdapter {
        private List<ContactDetails> contactDetailsList;
        public void setContactDetailsList(List<ContactDetails> list){
            contactDetailsList = list;
        }
        @Override
        public int getCount() {
            return contactDetailsList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.onecontactitem,null);
            ImageView photo = convertView.findViewById(R.id.photo);
            TextView name = convertView.findViewById(R.id.name);
            TextView cellno = convertView.findViewById(R.id.cellno);
            final ContactDetails contactDetails = contactDetailsList.get(position);
            name.setText(contactDetails.fullname);
            try {
                photo.setImageURI(Uri.parse(contactDetails.photouri));
            }
            catch (Exception e){

                //Image source
                //https://pixabay.com/vectors/user-icon-person-personal-about-me-2517433/
                photo.setImageResource(R.drawable.user);
            }

            if(contactDetails.cellnolist.size()==0){
                cellno.setText("No number found");
            }
            else {
                //Display first cell no.
                cellno.setText(contactDetails.cellnolist.get(0));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DisplayContact.class);
                    intent.putStringArrayListExtra("cellnolist",contactDetails.cellnolist);
                    intent.putStringArrayListExtra("emaillist",contactDetails.emaillist);
                    intent.putExtra("fullname",contactDetails.fullname);
                    try{
                        intent.putExtra("photouri",contactDetails.photouri);
                    }catch (Exception e){
                        intent.putExtra("photouri","not_found");
                    }

                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    private class ContactDetails {
        String fullname;
        String photouri;
        ArrayList<String> cellnolist;
        ArrayList<String> emaillist;
    }

}
