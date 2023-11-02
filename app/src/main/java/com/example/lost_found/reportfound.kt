package com.example.lost_found

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class reportfound : AppCompatActivity() {
    lateinit var list: ListView;
    var myitem = ArrayList<dataOfItem>();
    var cur_user = FirebaseAuth.getInstance().currentUser?.uid.toString();
    var ref = FirebaseDatabase.getInstance().getReference("Lost/$cur_user");
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportfound)
        list = findViewById(R.id.reportfoundlist);
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                update(snapshot);
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        });
    }
    fun update(snapshot: DataSnapshot) {
        for (i in snapshot.children) {
            var owner = i.child("owner").getValue().toString();
            var about = i.child("discription").getValue().toString();
            var url = i.child("url").getValue().toString();
            var contact = i.child("contact").getValue().toString();
            var x = dataOfItem(owner, contact, about, url, cur_user, i.key.toString());
            myitem.add(x);
        }
        list.adapter = dataAdapter(this, myitem);
        list.setOnItemClickListener{parent, view, position, id->
            var intent = Intent(applicationContext, FullView::class.java);
            intent.putExtra("owner", myitem[position].owner);
            intent.putExtra("about", myitem[position].discription);
            intent.putExtra("url", myitem[position].url);
            intent.putExtra("contact", myitem[position].contact);
            intent.putExtra("uid", myitem[position].uid);
            intent.putExtra("key", myitem[position].key);
            startActivity(intent);
        }
    }
}