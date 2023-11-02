package com.example.lost_found
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.w3c.dom.Text

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
  /*  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }*/
    lateinit var email:EditText;
    lateinit var pass:EditText;
    lateinit var signin:Button;
    lateinit var forgot:TextView;
    var mAuth = FirebaseAuth.getInstance();
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        email = findViewById(R.id.email_in);
        pass = findViewById(R.id.pass_in);
        signin = findViewById(R.id.signin);
        forgot = findViewById(R.id.forgot);
        var user = FirebaseAuth.getInstance().currentUser
        if(user!=null && user.isEmailVerified){
            startActivity(Intent(this, home::class.java));
        }
    }
    fun signup(view: View){
        startActivity(Intent(applicationContext, Register::class.java));
    }
    fun forgot(view:View){
        var email = email.text.toString();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(){
            if(it.isSuccessful){
                Toast.makeText(applicationContext, "PASSWORD RESET LINK SENT", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(applicationContext, it.exception.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    fun signin(view:View){
        var id:String = email.text.toString();
        var password:String = pass.text.toString();
        var domain:String = id.substring(id.indexOf("@") + 1);
        var iitp:String = "iitp.ac.in";
        email.text.clear();
        pass.text.clear();
        if(domain!=iitp){
            Toast.makeText(applicationContext, "ONLY IITP ALLOWED", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(id).matches()){
            Toast.makeText(applicationContext, "ENTER A VALID EMAIL ", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            val progressDialog = ProgressDialog(this@MainActivity);
            progressDialog.setTitle("Please Wait..");
            progressDialog.setMessage("Signing In...");
            progressDialog.show()
            mAuth.signInWithEmailAndPassword(id, password).addOnCompleteListener{
                if(it.isSuccessful){
                    var fuser = mAuth.currentUser;
                    if(fuser!!.isEmailVerified){
                        progressDialog.cancel();
                        startActivity(Intent(applicationContext, home::class.java));
                    }else{
                        Toast.makeText(applicationContext, "PLEASE VERIFY EMAIL", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        mAuth.signOut();
                    }
                }
                else{
                    progressDialog.cancel();
                    Toast.makeText(applicationContext, it.exception?.message.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}