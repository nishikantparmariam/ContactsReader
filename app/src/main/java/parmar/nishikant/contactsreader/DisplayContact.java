package parmar.nishikant.contactsreader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayContact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        Intent intent = getIntent();
        String photouri = intent.getExtras().getString("photouri");
        ArrayList<String> cellnolists = intent.getExtras().getStringArrayList("cellnolist");
        ArrayList<String> emaillist = intent.getExtras().getStringArrayList("emaillist");

        ImageView photo = findViewById(R.id.photo);
        if(photouri=="not_found"||photouri==null||photouri==""){
            //Image source
            //https://pixabay.com/vectors/user-icon-person-personal-about-me-2517433/
            photo.setImageResource(R.drawable.user);
        } else {
            photo.setImageURI(Uri.parse(photouri));
        }

        TextView fullname = findViewById(R.id.name);
        fullname.setText(intent.getExtras().getString("fullname"));





        TextView cellno = findViewById(R.id.cellno);
        if(cellnolists.size()==0){
            findViewById(R.id.cellnodiv).setVisibility(View.GONE);
        }
        else {
            String cellnoText = "";
            for(String c:cellnolists){
                cellnoText=cellnoText+"\n"+c;
            }
            cellno.setText(cellnoText.trim());
        }





        TextView email = findViewById(R.id.email);
        if(emaillist.size()==0){
            findViewById(R.id.emaildiv).setVisibility(View.GONE);
        }
        else {
            String emailText = "";
            for(String e:emaillist){
                emailText=emailText+"\n"+e;
            }
            email.setText(emailText.trim());
        }


    }
}
