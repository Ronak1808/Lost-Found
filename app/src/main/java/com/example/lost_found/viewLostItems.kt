package com.example.lost_found

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class viewLostItems : AppCompatActivity() {
    lateinit var list:ListView;
    lateinit var ref:DatabaseReference;
    public var lost_item = ArrayList<dataOfItem>();
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_lost_items)
        list = findViewById(R.id.list_lost);
        list.adapter = dataAdapter(this, lost_item);
        read();
    }
    fun update(snapshot: DataSnapshot){
        for(item in snapshot.children){
            for(i in item.children)
            {   var uid = i.child("uid").getValue().toString();
                var owner = i.child("owner").getValue().toString();
                var contact = i.child("contact").getValue().toString();
                var url = i.child("url").getValue().toString();
                var discription = i.child("discription").getValue().toString();
                lost_item.add(dataOfItem(owner, contact, discription, url, uid, i.key.toString()));
            }
        }
        list.adapter = dataAdapter(applicationContext, lost_item);
        list.setOnItemClickListener{parent, view, position, id->
            var intent = Intent(applicationContext, FullViewLost::class.java);
            intent.putExtra("owner", lost_item[position].owner);
            intent.putExtra("about", lost_item[position].discription);
            intent.putExtra("url", lost_item[position].url);
            intent.putExtra("contact", lost_item[position].contact);
            intent.putExtra("uid", lost_item[position].uid);
            intent.putExtra("key", lost_item[position].key);
            startActivity(intent);
        }
    }
    fun read(){
        var ref = FirebaseDatabase.getInstance().getReference("Lost");
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                update(snapshot);
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        });
    }

}