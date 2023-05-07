package org.project.bookreadingapp.ui.home

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.NonCancellable.start
import org.project.bookreadingapp.R
import kotlin.system.exitProcess
import android.provider.Settings

class DialogHome: DialogFragment() {

    val REQUEST_AUDIO_PERMISSION_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner)

        getDialog()!!.setCanceledOnTouchOutside(false)

        return inflater.inflate(R.layout.custom_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog, null)
        val closeAppButton = view.findViewById<Button>(R.id.btn_closeApp)
        val acceptButton = view.findViewById<Button>(R.id.btn_acceptPerm)

        closeAppButton.setOnClickListener {
            // Do something when the button is clicked
            exitProcess(-1)
        }

        acceptButton.setOnClickListener {

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", "org.project.bookreadingapp", null)
            intent.data = uri
            startActivity(intent)
            dismiss() // dismiss the dialog when done
            exitProcess(-1)
//            if (CheckPermissions()) {
//                return@setOnClickListener
//            } else {
//                // if audio recording permissions are
//                // not granted by user below method will
//                // ask for runtime permission for mic and storage.
//                RequestPermissions()
//            }



//            // Get a reference to an instance of HomeFragment
//            val homeFragment = requireActivity().supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment?
//            // Call the method in HomeFragment
//            val checkPerm = homeFragment?.CheckPermissions()
//            if (checkPerm == true) {
//                return@setOnClickListener
//            } else {
//                // if audio recording permissions are
//                // not granted by user below method will
//                // ask for runtime permission for mic and storage.
//                homeFragment?.RequestPermissions()
//            }

        }

        builder.setView(view)
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
//        if (permissionGranted) {
//            Toast.makeText(
//                requireContext(),
//                "Permission Granted",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//        else {
////            Toast.makeText(
////                requireContext(),
////                "Please Accept Permission To Use The App",
////                Toast.LENGTH_LONG
////            ).show()
//            val fragmentManager = childFragmentManager // Replace with your FragmentManager instance
//            val dialogHome = DialogHome()
//            dialogHome.show(fragmentManager,"MyCustomFragment")
//        }
//    }
//
//    fun CheckPermissions(): Boolean {
//
//        val result = ContextCompat.checkSelfPermission(
//            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
////        val result1 = ContextCompat.checkSelfPermission(
////            requireContext(), Manifest.permission.RECORD_AUDIO
////        )
//
////        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
//        return result == PackageManager.PERMISSION_GRANTED
//    }
//
//    fun RequestPermissions() {
//        // this method is used to request the
//        // permission for audio recording and storage.
//        //อันเก่า
////        requestPermissions(arrayOf(
////            Manifest.permission.RECORD_AUDIO,
////            Manifest.permission.WRITE_EXTERNAL_STORAGE
////        ),
////            REQUEST_AUDIO_PERMISSION_CODE)
//        //อันใหม่
//        requestPermissions(arrayOf(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ),
//            REQUEST_AUDIO_PERMISSION_CODE)
//    }


}