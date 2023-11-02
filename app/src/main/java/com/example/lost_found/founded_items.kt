package com.example.lost_found

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class founded_items : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_founded_items);
    }
    var founded = ArrayList<dataOfItem>();
    lateinit var list:ListView;
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_founded_items)
        list = findViewById(R.id.listfoundeditems);
        var ref = FirebaseDatabase.getInstance().getReference("Found");
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                update(snapshot);
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        });
    }
    fun update(snapshot: DataSnapshot){
        for(item in snapshot.children){
            var owner = item.child("owner").getValue().toString();
            var about = item.child("discription").getValue().toString();
            var url = item.child("url").getValue().toString();
            var contact = item.child("contact").getValue().toString();
            var x = dataOfItem(owner, contact, about, url, "kjasjf", "kfhakj");
            founded.add(x);
        }
        list.adapter = dataAdapter(this, founded);
    }
}