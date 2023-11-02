package com.example.lost_found

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import com.squareup.picasso.Picasso
@Suppress("DEPRECATION")
class FullView : AppCompatActivity() {
    lateinit var img: ImageView;
    lateinit var owner: TextView;
    lateinit var contact:TextView;
    lateinit var about:TextView;
    lateinit var submit: Button;
    var urls = ArrayList<String>();
    var indices = ArrayList<Int>();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_view)
        var url = intent.getStringExtra("url").toString();
        indices.add(-1);
        for(i in 0..url.length-1){
            if(url[i]==','){
                indices.add(i);
            }
        }
        for(i in 0..indices.size-2){
            var temp = url.substring(indices[i]+1, indices[i+1]-1);
            urls.add(temp);
        }
        img = findViewById(R.id.largeImagelost);
        owner = findViewById(R.id.ownerbiglost);
        contact = findViewById(R.id.contactbiglost);
        about = findViewById(R.id.aboutbiglost);
        submit = findViewById(R.id.found);
        owner.text = intent.getStringExtra("owner").toString();
        contact.text = intent.getStringExtra("contact").toString();
        about.text = intent.getStringExtra("about").toString();
        Picasso.with(this).load(intent.getStringExtra("url").toString()).resize(359, 314).onlyScaleDown().into(img);
    }
    var cur = 0;
    fun back(view:View){
        if(cur != 0){
            Picasso.with(this).load(urls[cur-1]).resize(359, 314).onlyScaleDown().into(img);
            cur--;
        }
        else{
            cur = urls.size-1;
            Picasso.with(this).load(urls[cur]).resize(359, 314).onlyScaleDown().into(img);
        }
    }
    fun front(view:View){
        if(cur <= urls.size-2){
            Picasso.with(this).load(urls[cur+1]).resize(359, 314).onlyScaleDown().into(img);
            cur++;
        }
        else{
            Picasso.with(this).load(urls[0]).resize(359, 314).onlyScaleDown().into(img);
            cur = 0;
        }
    }
    fun found(view: View){
        var dialog = ProgressDialog(this@FullView);
        dialog.setTitle("Please Wait...");
        dialog.setMessage("Submitting Report...");
        dialog.show();
        var ref = FirebaseDatabase.getInstance().getReference("Lost");
        var uid = intent.getStringExtra("uid").toString();
        var key = intent.getStringExtra("key").toString();
        var obj = dataOfItem(owner.text.toString(), contact.text.toString(), about.text.toString(), intent.getStringExtra("url").toString(), "dkjjhadfkj", "dkfhkjfjh");
        var update_found = FirebaseDatabase.getInstance().getReference("Found");
        var a = update_found.push().key.toString();
        update_found.child(key).setValue(obj).addOnCompleteListener(){
            ref.child(uid).child(key).removeValue().addOnCompleteListener(){
                if(it.isSuccessful){
                    Toast.makeText(applicationContext, "FOUND REPORTED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    startActivity(Intent(applicationContext, home::class.java));
                }
                else{
                    dialog.cancel();
                    Toast.makeText(applicationContext, it.exception.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}