package com.example.lost_found

import android.hardware.Camera.PictureCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
class FullViewLost : AppCompatActivity() {
    lateinit var img: ImageView;
    lateinit var owner: TextView;
    lateinit var contact:TextView;
    lateinit var about:TextView;
    lateinit var next: ImageButton;
    lateinit var prev:ImageButton;
    var indices = ArrayList<Int>();
    var urls = ArrayList<String>();
    var cur = -1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_view_lost);
        indices.add(-1);
        var url = intent.getStringExtra("url").toString();
        for(i in 0..url.length-1){
            if(url[i] == ','){
                indices.add(i);
            }
        }
        for(i in 0..indices.size-2){
            var temp = url.substring(indices[i]+1, indices[i+1]-1);
            urls.add(temp);
        }
        cur = 0;
        img = findViewById(R.id.largeImagelost);
        owner = findViewById(R.id.ownerbiglost);
        contact = findViewById(R.id.contactbiglost);
        about = findViewById(R.id.aboutbiglost);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        owner.text = intent.getStringExtra("owner").toString();
        contact.text = intent.getStringExtra("contact").toString();
        about.text = intent.getStringExtra("about").toString();
        Picasso.with(this).load(urls[0]).resize(376, 328).onlyScaleDown().into(img);
    }
    fun change_next(view: View){
        if(cur <= urls.size-2){
            Picasso.with(this).load(urls[cur+1]).resize(376, 328).onlyScaleDown().into(img);
            cur++;
        }
        else{
            Picasso.with(this).load(urls[0]).resize(376, 328).onlyScaleDown().into(img);
            cur = 0;
        }
    }
    fun change_prev(view:View){
        if(cur>0){
            Picasso.with(this).load(urls[cur-1]).resize(376, 328).onlyScaleDown().into(img);
            cur--;
        }
        else{
            Picasso.with(this).load(urls[urls.size-1]).resize(376, 328).onlyScaleDown().into(img);
            cur = urls.size-1;
        }
    }
}