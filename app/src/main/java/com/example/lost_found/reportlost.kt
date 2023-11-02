package com.example.lost_found

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class reportlost : AppCompatActivity() {
    var uris = ArrayList<Uri>();
    private var PICK_IMAGE_REQUEST = 71;
    private var filePath: Uri? = null;
    private var firebaseStore:FirebaseStorage? = null;
    private var storageReference:StorageReference? = null;
    lateinit var owner:EditText;
    lateinit var file_uri:Uri;
    var  image_URL = "";
    lateinit var contact:EditText;
    lateinit var discription:EditText;
    lateinit var submit: Button;
    lateinit var insert:Button;
    lateinit var owner_text:String;
    lateinit var contact_text:String;
    lateinit var discription_text:String;
    var GALLERY_REQUEST_CODE = 71;
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reportlost)
        owner = findViewById(R.id.owner);
        contact = findViewById(R.id.contact);
        discription = findViewById(R.id.discription);
        submit = findViewById(R.id.submit);
        insert = findViewById(R.id.insert);
    }
    fun check():Boolean{
        owner_text= owner.text.toString();
        contact_text = contact.text.toString();
        discription_text= discription.text.toString();
        if(owner_text.length == 0){
            Toast.makeText(applicationContext, "ENTER OWNER NAME", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(discription_text.length == 0){
            Toast.makeText(applicationContext, "ENTER SOMETHING ABOUT LOST ITEM", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }
    fun insert(view:View){
        if(Build.VERSION.SDK_INT<19){
            var intent = Intent();
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_REQUEST_CODE);
        }
        else{
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }
    var i= 0;
    fun uploadImagePieceWise(){
        val dialog = ProgressDialog(this@reportlost);
        dialog.setTitle("Please Wait...");
        dialog.setMessage("Submitting Report...");
        if(i==0){
            dialog.show();
        }
        if(i==uris.size){
            i = 0;
            dialog.cancel();
            var ref = FirebaseDatabase.getInstance().getReference("Lost");
            var key1 = ref.push().key.toString();
            var lost = dataOfItem(owner_text, contact_text, discription_text, image_URL, FirebaseAuth.getInstance().currentUser?.uid.toString(), key1);
            var user = FirebaseAuth.getInstance().currentUser?.uid.toString();
            ref.child(user).child(key1).setValue(lost).addOnCompleteListener(){
                if(it.isSuccessful){
                    Toast.makeText(applicationContext, "LOST REPORTED", Toast.LENGTH_SHORT).show();
                    startActivity(Intent(applicationContext, home::class.java));
                }
                else{
                    Toast.makeText(applicationContext, "SOMETHING WENT WRONG ${it.exception?.message.toString()}", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
        var cur_uri = uris[i];
        if(cur_uri != null){
            var filename = UUID.randomUUID().toString() + ".jpg"
            var refStorage = FirebaseStorage.getInstance().reference.child("Lost/$filename");
            refStorage.putFile(cur_uri).addOnFailureListener{
                Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show();
            }.addOnSuccessListener{taskSnapshot ->
                refStorage.downloadUrl.addOnCompleteListener(){taskSnapshot->
                    image_URL += taskSnapshot.result.toString();
                    image_URL += ',';
                    i++;
                    uploadImagePieceWise()
                }
            }
        }
    }
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //Getting the Uri
            if(data?.getClipData() != null){
                var count = data.clipData?.itemCount
                for(i in 0..count!!-1){
                    uris.add(data.clipData?.getItemAt(i)!!.uri);
                }
            }
            else if(data?.getData()!=null){
                uris.add(data.data!!)
            }

        }
    }

    fun submit(view:View){
        if(check()){
            uploadImagePieceWise();
        }
    }
}