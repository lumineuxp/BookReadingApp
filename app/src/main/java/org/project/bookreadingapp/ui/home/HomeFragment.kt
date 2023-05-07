package org.project.bookreadingapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import org.project.bookreadingapp.Contact
import org.project.bookreadingapp.Help
import org.project.bookreadingapp.Instructions
import org.project.bookreadingapp.databinding.FragmentHomeBinding
import java.io.IOException
import kotlin.system.exitProcess

const val REQUEST_CODE = 200

class HomeFragment : Fragment() {

    // constant for storing audio permission
    val REQUEST_AUDIO_PERMISSION_CODE = 1

    //private var permissionGranted = false

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        val InstructButton: Button = binding.instructBtn
        //val BackButton: Button = binding.backBtn
        val HelpButton: Button = binding.helpBtn
        val ContactusButton: Button = binding.contactBtn

        InstructButton.setOnClickListener {
            val intent = Intent(activity, Instructions::class.java)
            startActivity(intent)
        }

        HelpButton.setOnClickListener {
            val intent = Intent(activity, Help::class.java)
            startActivity(intent)
        }

        ContactusButton.setOnClickListener {
            val intent = Intent(activity, Contact::class.java)
            startActivity(intent)
        }



//        BackButton.setOnClickListener {
//            InstructionFragment.visibility = View.GONE
//        }

        // check permission method is used to check
        // that the user has granted permission
        // to store the audio.
        if (CheckPermissions()) {
            return root
        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions()
        }

        return root
    }

//    internal fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String?>?,
//        grantResults: IntArray
//    ) {
//        // this method is called when user will
//        // grant the permission for audio recording.
//        when (requestCode) {
//            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
////                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
//                val permissionToStore = grantResults[0] == PackageManager.PERMISSION_GRANTED
//                if (permissionToStore) {
//                    Log.i("home permission granted","Permission Granted")
////                    Toast.makeText(
////                        requireContext(),
////                        "Permission Granted",
////                        Toast.LENGTH_LONG
////                    ).show()
//                } else {
//                    Log.i("home permission denied","Permission Denied")
////                    Toast.makeText(
////                        requireContext(),
////                        "Permission Denied",
////                        Toast.LENGTH_LONG
////                    ).show()
//                }
//            }
//        }
//    }

//        override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE)
//            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
//            Toast.makeText(
//                        requireContext(),
//                        "Permission Granted",
//                        Toast.LENGTH_LONG
//                    ).show()
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG).show()
//                permissionGranted = true
//            } else {
//                Toast.makeText(requireContext(), "You must press allow first.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE) {
//            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
//            Toast.makeText(
//                        requireContext(),
//                        "Permission Granted",
//                        Toast.LENGTH_LONG
//                    ).show()
//        }
        val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (permissionGranted) {
            Toast.makeText(
                requireContext(),
                "Permission Granted",
                Toast.LENGTH_LONG
            ).show()
        }
        else {
            val fragmentManager = childFragmentManager // Replace with your FragmentManager instance
            val dialogHome = DialogHome()
            dialogHome.show(fragmentManager,"MyCustomFragment")
        }
    }

    fun CheckPermissions(): Boolean {

        val result = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
//        val result1 = ContextCompat.checkSelfPermission(
//            requireContext(), Manifest.permission.RECORD_AUDIO
//        )

//        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        return result == PackageManager.PERMISSION_GRANTED
    }

     fun RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        //อันเก่า
//        requestPermissions(arrayOf(
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ),
//            REQUEST_AUDIO_PERMISSION_CODE)
        //อันใหม่
        requestPermissions(arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),
        REQUEST_AUDIO_PERMISSION_CODE)
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //setContentView(R.layout.activity_main)
//
//        val InstructButton: Button = binding.instructBtn
//
//        InstructButton.setOnClickListener {
//            InstructButton.visibility = View.INVISIBLE
//        }
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}