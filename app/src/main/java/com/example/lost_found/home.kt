package com.example.lost_found

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
class home : AppCompatActivity() {
    lateinit var welcome:TextView;
    lateinit var userimage:ImageView;
    var mAuth = FirebaseAuth.getInstance();
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        welcome = findViewById(R.id.welcome);
        userimage = findViewById(R.id.userimage);
        var ref = FirebaseDatabase.getInstance().getReference("Users");
        lateinit var name:String;
        var imageurl:String = "";
        var uid = mAuth.currentUser?.uid.toString();
        ref.child(uid).child("name").get().addOnCompleteListener(){
            if(it.isSuccessful){
                welcome.text = "Welcome  ${it.result.value.toString()}";
            }
        }
        ref.child(uid).child("email").get().addOnCompleteListener(){
            if(it.isSuccessful){
                imageurl = it.result.value.toString();
                Picasso.with(this).load(imageurl).fit().centerCrop().into(userimage);

            }
            else{
                imageurl = "http://graph.facebook.com/{user-id}/picture?type=large";
                Picasso.with(this).load(imageurl).fit().centerCrop().into(userimage);
            }
        }
    }
    fun logout(view: View){
        mAuth.signOut();
        startActivity(Intent(this, MainActivity::class.java));
    }
    fun reportlost(view:View){
        startActivity(Intent(this, reportlost::class.java));
    }
    fun reportfound(view:View){
        startActivity(Intent(this, reportfound::class.java));
    }
    fun viewlost(view:View){
        startActivity(Intent(this, viewLostItems::class.java));
    }
    fun viewfound(view:View){
        startActivity(Intent(this, founded_items::class.java));
    }
    fun update(view:View){
        mAuth.sendPasswordResetEmail(mAuth.currentUser?.email.toString())
        Toast.makeText(applicationContext, "Password Reset Link Sent", Toast.LENGTH_SHORT).show();
    }
}