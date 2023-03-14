package org.project.bookreadingapp.ui.record

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.project.bookreadingapp.MainActivity
import org.project.bookreadingapp.data.Embed
import org.project.bookreadingapp.data.Wav
import org.project.bookreadingapp.data.api.ApiService
//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.project.bookreadingapp.databinding.FragmentRecordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import android.util.Base64
import java.io.File

class RecordFragment : Fragment(), MediaRecorder.OnInfoListener {

    // Initializing all variables..
    private var startTV: TextView? = null
    private var stopTV: TextView? = null
    private var playTV: TextView? = null
    private var stopplayTV: TextView? = null
    private var statusTV: TextView? = null
    private var playBase64: ImageButton? = null

    // creating a variable for media recorder object class.
    private var mRecorder: MediaRecorder? = null

    // creating a variable for mediaplayer class
    private var mPlayer: MediaPlayer? = null

    // string variable is created for storing a file name
    private var mFileName: String? = null

    // constant for storing audio permission
    val REQUEST_AUDIO_PERMISSION_CODE = 1

    //create wav base64
    private var wavBase64 : String? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //setContentView(org.project.bookreadingapp.R.layout.fragment_record)
//        //มาแก้โด้ยยย หาอะไรใส่แทน setContentView!!!
//
//
//        startTV?.setOnClickListener { // start recording method will
//            // start the recording of audio.
//            startRecording()
//        }
//        stopTV?.setOnClickListener { // pause Recording method will
//            // pause the recording of audio.
//            pauseRecording()
//        }
//        playTV?.setOnClickListener { // play audio method will play
//            // the audio which we have recorded
//            playAudio()
//        }
//        stopplayTV?.setOnClickListener { // pause play method will
//            // pause the play of audio
//            pausePlaying()
//        }
//    }


    private var _binding: FragmentRecordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recordViewModel =
            ViewModelProvider(this).get(RecordViewModel::class.java)

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRecord
        recordViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //อันเก่า
        //val recordButton: ImageButton = binding.recordBtn

        // initialize all variables with their layout items.
//        val statusTV: TextView = binding.idTVstatus
        val startTV: ImageButton = binding.recordBtn
        val stopTV: ImageButton = binding.recordingBtn
        val playTV: ImageButton = binding.playBtn
        val stopplayTV: ImageButton = binding.stopBtn

        playBase64 = binding.playBtn2
//        stopTV!!.setBackgroundColor(808080)
//        playTV!!.setBackgroundColor(808080)
//        stopplayTV!!.setBackgroundColor(808080)
        stopTV.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
        playTV.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
        stopplayTV.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))

//        recordButton.setOnClickListener {
//            recordButton.visibility = View.GONE
////            val intent = Intent(activity, Instructions::class.java)
////            startActivity(intent)
//        }


        startTV?.setOnClickListener { // start recording method will
            // start the recording of audio.
            startTV?.visibility = View.GONE
//            stopTV?.visibility = View.VISIBLE
            playTV?.visibility = View.VISIBLE
            startRecording()
        }
//        stopTV?.setOnClickListener { // pause Recording method will
//            // pause the recording of audio.
//            stopTV?.visibility = View.GONE
//            playTV?.visibility = View.VISIBLE
//            pauseRecording()
//        }
        playTV?.setOnClickListener { // play audio method will play
            // the audio which we have recorded
            playTV?.visibility = View.GONE
            stopplayTV?.visibility = View.VISIBLE
            playAudio()
        }
        stopplayTV?.setOnClickListener { // pause play method will
            // pause the play of audio
            stopplayTV?.visibility = View.GONE
            startTV?.visibility = View.VISIBLE
            pausePlaying()
        }

        playBase64?.setOnClickListener { // play audio method will play
            // the audio which we have recorded
            playBase64?.visibility = View.GONE
            playAudioBase64()
        }


        return root
    }

    private fun playAudioBase64() {
        var wav = Wav()
        wav.wav = wavBase64

        val exSynPath = "/sdcard/MyApp/audio/example.mp3"
        val apiService = ApiService()
        val call = apiService.getEmbed(wav)

        call.enqueue(object: Callback<Embed> {

            override fun onResponse(call: Call<Embed>, response: Response<Embed>) {
                if (response.isSuccessful) {
                    var jsonEmbed = response.body() // Use it or ignore it
                    Log.i("API Get Embed", jsonEmbed?.exSynVoice.toString());
                    val synvoiceBase64 = jsonEmbed!!.exSynVoice
                    Toast.makeText(context, "Successfully.", Toast.LENGTH_SHORT).show()

                    convertBase64ToAudio(synvoiceBase64, exSynPath)

                    playMedia(exSynPath)

                } else {
                    Log.e("API Get Embed" , response.toString())

                }
            }

            override fun onFailure(call: Call<Embed>, t: Throwable) {
                Log.e("API Get Embed" , t.message + " "+ t.cause)
                playBase64?.visibility = View.VISIBLE
            }
        })


    }

    private fun convertBase64ToAudio(base64String: String, filePath: String) {
        val audioData = Base64.decode(base64String, Base64.DEFAULT)
        val file = File(filePath)
        file.parentFile.mkdirs()
        file.writeBytes(audioData)
    }
    private fun convertAudioToBase64(filePath: String): String {
        val file = File(filePath)
        val audioData = file.readBytes()
        return Base64.encodeToString(audioData, Base64.DEFAULT)
    }

    private fun playMedia(filePath: String){
        mPlayer = MediaPlayer()
        try {
            // below method is used to set the
            // data source which will be our file name
            mPlayer?.setDataSource(filePath)

            // below method will prepare our media player
            mPlayer?.prepare()

            // below method will start our media player.
            mPlayer?.start()

        } catch (e: IOException) {
            Log.e("TAG", e.toString())
        }
    }

    private fun startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record and store the audio.
        if (CheckPermissions()) {

            // setbackgroundcolor method will change
            // the background color of text view.


            //เดี๋ยวใช้
            //val context: Context = getApplicationContext()
            //stopTV!!.setBackgroundColor(ContextCompat.getColor(context,
            //    org.project.bookreadingapp.R.color.purple_200))

//            stopTV?.setBackgroundColor(111111)
//            startTV?.setBackgroundColor(808080)
//            playTV?.setBackgroundColor(808080)
//            stopplayTV?.setBackgroundColor(808080)

            val statusTV: TextView = binding.idTVstatus
            stopTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))
            startTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
            playTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
            stopplayTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))

            // we are here initializing our filename variable
            // with the path of the recorded audio file.
            mFileName = Environment.getExternalStorageDirectory().absolutePath
            mFileName += "/AudioRecording.3gp"

            // below method is used to initialize
            // the media recorder class
            mRecorder = MediaRecorder()

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)

            // below method is used to set
            // the output format of the audio.
            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            mRecorder?.setMaxDuration(10000)

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder?.setOutputFile(mFileName)
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder?.prepare()
            } catch (e: IOException) {
                Log.e("TAG", "prepare() failed")
            }
            // start method will start
            // the audio recording.
            mRecorder?.start()
            statusTV?.text = "Recording Started"

            mRecorder?.setOnInfoListener(this)


//            mRecorder?.release()
//            mRecorder = null
//            statusTV?.text = "Recording Stopped"

        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions()
        }

    }

    override fun onInfo(mRecorder: MediaRecorder, what: Int, p2: Int) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            mRecorder?.stop()
            mRecorder?.release()
            statusTV?.text = "Recording Stopped"
        }
    }

    internal fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>?,
        grantResults: IntArray
    ) {
        // this method is called when user will
        // grant the permission for audio recording.
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.size > 0) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(
                        requireContext(),
                        "Permission Granted",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permission Denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun CheckPermissions(): Boolean {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(
//                requireActivity(), arrayOf(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.RECORD_AUDIO
//                ),
//                REQUEST_AUDIO_PERMISSION_CODE
//            )
//        } else {
//            Log.e("DB", "PERMISSION GRANTED")
//        }

        // this method is used to check permission
//        val result = ContextCompat.checkSelfPermission(
//            ApplicationProvider.getApplicationContext<Context>(),
//            WRITE_EXTERNAL_STORAGE
//        )
//        val result1 = ContextCompat.checkSelfPermission(
//            ApplicationProvider.getApplicationContext<Context>(),
//            RECORD_AUDIO
//        )

        val result = ContextCompat.checkSelfPermission(
            requireContext(),WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(
            requireContext(),RECORD_AUDIO
        )

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        requestPermissions(arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE),
        REQUEST_AUDIO_PERMISSION_CODE)

//        ActivityCompat.requestPermissions(
//            this@MainActivity,
//            arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE),
//            REQUEST_AUDIO_PERMISSION_CODE
//        )
    }

    fun playAudio() {
        stopTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
        startTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))
        playTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
        stopplayTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))

        // for playing our recorded audio
        // we are using media player class.
        Log.i("file name", mFileName.toString())
        playMedia(mFileName.toString())

        wavBase64 = convertAudioToBase64(mFileName.toString())
        Log.i("base64", wavBase64!!)
//        try {
//            // below method is used to set the
//            // data source which will be our file name
//            mPlayer?.setDataSource(mFileName)
//
//            // below method will prepare our media player
//            mPlayer?.prepare()
//
//            // below method will start our media player.
//            mPlayer?.start()
//            statusTV?.text = "Recording Started Playing"
//        } catch (e: IOException) {
//            Log.e("TAG", "prepare() failed")
//        }
    }

//    fun pauseRecording() {
//        stopTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
//        startTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))
//        playTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))
//        stopplayTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))
//
//        // below method will stop
//        // the audio recording.
//        mRecorder?.stop()
//
//        // below method will release
//        // the media recorder class.
//        mRecorder?.release()
//        mRecorder = null
//        statusTV?.text = "Recording Stopped"
//    }

    fun pausePlaying() {
        // this method will release the media player
        // class and pause the playing of our recorded audio.
        mPlayer?.release()
        mPlayer = null
        stopTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
        startTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))
        playTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.purple_200))
        stopplayTV?.setBackgroundColor(resources.getColor(org.project.bookreadingapp.R.color.gray))
        statusTV?.text = "Recording Play Stopped"
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}