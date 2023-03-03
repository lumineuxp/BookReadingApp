package org.project.bookreadingapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.project.bookreadingapp.databinding.ActivityContactBinding

class Contact : AppCompatActivity() {

//    private var _binding: ActivityContactBinding? = null
    lateinit var  binding: ActivityContactBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val etSubject: EditText = binding.txtSubject
//        val etMessage: EditText = binding.txtSubject
//        val sendButton: Button = binding.btnSend

        binding.btnSend.setOnClickListener {
            val subject = binding.txtSubject.text.toString().trim()
            val message = binding.txtMessage.text.toString().trim()
            //val email = binding.txtMail.text.toString().trim()

            val mail = "klittiya.suwanmalai@g.swu.ac.th"


            if (subject.isEmpty()) {
                Toast.makeText(this, "Please add Subject", Toast.LENGTH_SHORT).show()
            }
            else if (message.isEmpty()) {
                Toast.makeText(this, "Please add Message", Toast.LENGTH_SHORT).show()
            }
            else {
                //val mail:String = "mailto:" + email + "?&subject=" + Uri.encode(subject) + "&body=" + Uri.encode(message)
//                    String mail = "mailto:" + email + "?&subject=" + Uri.encode(subject) + "&body=" + Uri.encode(message);

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:"+mail)
                    putExtra(Intent.EXTRA_EMAIL, mail)
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, message)
                }
//                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                //intent.setData(Uri.parse(mail))

                try {
                    startActivity(Intent.createChooser(intent,"Send Email"))
                }
                catch (e:Exception) {
                    Toast.makeText(this@Contact, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }

    }





}