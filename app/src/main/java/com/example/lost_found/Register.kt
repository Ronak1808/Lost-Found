package com.example.lost_found
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.math.sign

@Suppress("DEPRECATION")
class Register : AppCompatActivity() {
    //TODO : Register Activity and XML file name differ so check at last if casue error
    lateinit var name: EditText;
    lateinit var roll:EditText;
    lateinit var email_in: EditText;
    lateinit var password: EditText;
    lateinit var signup: Button;
    lateinit var confirmpass:EditText;
    lateinit var whatsapp:EditText;
    var GALLERY_REQUEST_CODE = 100;
    lateinit var insert:Button;
    lateinit var image_URL:String;
    lateinit var file_uri:Uri;
    var mAuth = FirebaseAuth.getInstance();
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        email_in = findViewById(R.id.email_in);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        confirmpass = findViewById(R.id.confirm);
        whatsapp = findViewById(R.id.whatsapp);
        roll = findViewById(R.id.roll);
        name = findViewById(R.id.name);
    }
    fun valid():Boolean{
        if(name.text.toString().length == 0){
            Toast.makeText(applicationContext, "ENTER NAME", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(roll.text.toString().length!=8){
            Toast.makeText(applicationContext, "INVALID ROLL", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(whatsapp.text.toString().length < 10){
            Toast.makeText(applicationContext, "ENTER VALID WHATSAPP NUMBER", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_in.text.toString()).matches()) {
            Toast.makeText(applicationContext, "INVALID EMAIL", Toast.LENGTH_SHORT).show();
            return false;
        }
        var domain = "iitp.ac.in";
        var given = email_in.text.toString().substring(email_in.text.toString().indexOf("@")+1);
        if(given!=domain){
            Toast.makeText(applicationContext, "ONLY IITP STUDENTS ALLOWED", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.text.toString() != confirmpass.text.toString()){
            Toast.makeText(applicationContext, "PASS AND CONFIRM PASS DID NOT MATCH", Toast.LENGTH_SHORT).show();
            password.text.clear();
            confirmpass.text.clear();
            return false;
        }
        if(password.text.toString().length <8){
            Toast.makeText(applicationContext, "PASSWORD SHOULD BE ATLEAST 8 CHARACTER LONG", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.text.toString() != confirmpass.text.toString()){
            Toast.makeText(applicationContext, "PASSWORD DID NOT MATCH CONFIRM PASSWORD", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    fun createUser(view: View) {
        uploageImage(file_uri);
    }
   fun insert(view:View){
            val intent = Intent();
            intent.type = "image/*";
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Please select..."), GALLERY_REQUEST_CODE);
   }
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!= null && data.data != null){
            //Getting the Uri
            file_uri = data.data!!;
        }
    }
    private fun uploageImage(fileUri: Uri?) {
          if (fileUri != null) {
              val fileName = UUID.randomUUID().toString() + ".jpg"
              val refStorage = FirebaseStorage.getInstance().reference.child("Users/$fileName");
              val progressDialog = ProgressDialog(this@Register);
              progressDialog.setTitle("Please Wait...")
              progressDialog.setMessage("Signing Up")
              progressDialog.show();
              refStorage.putFile(fileUri).addOnFailureListener {
                  Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT)
                      .show();
                  progressDialog.cancel();
              }.addOnSuccessListener() { taskSnapshot ->
                  refStorage.downloadUrl.addOnCompleteListener() { taskSnapshot ->
                      image_URL = taskSnapshot.result.toString();
                  }
              }.addOnCompleteListener(){
                  var email = email_in.text.toString();
                  var pass = password.text.toString();
                  var base = FirebaseDatabase.getInstance().getReference("Users");
                  if(valid()){
                      mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener() {
                          if (it.isSuccessful) {
                              Toast.makeText(applicationContext, "REGISTERATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                              var fuser = mAuth.currentUser;
                              if (fuser != null) {
                                  fuser.sendEmailVerification().addOnSuccessListener {
                                      Toast.makeText(applicationContext, "VERIFICATION EMAIL SENT", Toast.LENGTH_SHORT).show();
                                      var ref = FirebaseDatabase.getInstance().getReference("Users");
                                      var user = AddUser(image_URL, name.text.toString(),  whatsapp.text.toString(), roll.text.toString(), mAuth.currentUser?.uid.toString());
                                      ref.child(mAuth.currentUser?.uid.toString()).setValue(user).addOnCompleteListener(){
                                          progressDialog.cancel();
                                          startActivity(Intent(applicationContext, MainActivity::class.java));
                                      }
                                  }.addOnFailureListener(){
                                      Toast.makeText(applicationContext, it.message.toString() + "This ", Toast.LENGTH_SHORT).show();
                                      progressDialog.cancel();
                                  }
                              }
                          }
                          else{
                              progressDialog.cancel();
                              Toast.makeText(applicationContext, "SOMETHING WENT WRONG" + it.exception.toString(), Toast.LENGTH_SHORT).show();
                              startActivity(Intent(applicationContext, MainActivity::class.java));
                          }
                      }
                  }
              }
          }
      }
}

