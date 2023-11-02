package com.example.lost_found
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
class dataAdapter(val cont:Context, val data:ArrayList<dataOfItem>):ArrayAdapter<dataOfItem>(cont, R.layout.itemview , data) {
    @SuppressLint("MissingInflatedId")
    override fun getView(position:Int, convertView: View?, parent:ViewGroup):View{
        var inflater = LayoutInflater.from(cont);
        var view = inflater.inflate(R.layout.itemview, null);
        /*var uri:TextView = view.findViewById(R.id.url);*/
        var img:ImageView = view.findViewById(R.id.image);
        var name:TextView = view.findViewById(R.id.Name);
        var contact:TextView = view.findViewById(R.id.number);
        var about:TextView = view.findViewById(R.id.about);
        name.text = data[position].owner;
        contact.text = data[position].contact;
        /*img.setImageResource(R.drawable.one);*/
        if(data[position].discription.length <=30){
            about.text = data[position].discription;
        }
        else{
            about.text = (data[position].discription.substring(0, 28) + "...");
        }
        Picasso.with(cont).load(data[position].url).fit().into(img);
        return view;
    }
}