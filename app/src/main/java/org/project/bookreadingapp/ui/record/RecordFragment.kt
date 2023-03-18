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
        var emb =  "SUQzBAAAAAAAI1RTU0UAAAAPAAADTGF2ZjU4LjQ1LjEwMAAAAAAAAAAAAAAA//NYwAAAAAAAAAAAAEluZm8AAAAPAAAAjQAAPDAABggLDRETFBgaHR8hJSYqLC4xMzc4PD5AQ0VISkxQUVVXWVxeYmNlaWtucHR1d3t9gIKEh4mNjpCUlpmbn6Cipqirra+ytLi6u7/BxMbIy83R09bY2t3f4+Xm6uzv8fP3+Pz+AAAAAExhdmM1OC45MQAAAAAAAAAAAAAAACQC0AAAAAAAADww3c4gUwAAAAAAAAAAAAAA//M4xAAUEl5gCUEQAEDcgAAAAxjGMY4BjGN5jIQhCaEOc5zn///7E85zgBJwMDFuhznOd/yEahDnOehCEOf+QnkIQ53/OfznchAgAAABHAgCAIBjhiGIPh/8EMoGFQ/YGRgEr7kVx2yccd9p//M4xA4Xem6UAYgwAI6/n+47PkzTN//9nu/37/5v93lHvO8w8AGYVmmqN/xqNTrd3xdX7dBt3U8yrzXvrlmMQhNt+JXMZ+/fTkIrYPMxt9nmqboZmXV7BmlR5YhSkkkkksbbScA3paTKV7Dz//M4xA8X2Ua+X88wAjSONWSPApd5Zghv4qHQ4NzQPUPi2y26NnujZsPOWzxBd59f93yfuPt1BpRAe6DAGsaHlGlsC5UGAgIkElGS/RbT7giPJNo+gwaMx3ypF1pVFVEI6vkQwzVw/Cg1FEKK//M4xA4YQlaOMnpGGGpRPn7lLsBaDKopYNawql4RhWDqBqRElWhipQTPsY1e2UJlnoaDmhlZG0CyDmZndDh70T/mr/9wpTLIiIjNo5c/hoDVUARCEQ8a+wrY/1Cz3NQVkvW7IToF3bVhKOcG//M4xAwYAsKFjnjE2ZI5scTDTib0xRsu5Ag7VSHMjQgKISCI+epb8LZaUNjtznsfFIiASZc7Lv3OvrVnWjESj6u1Hltt3a036CjhlGMtSGJ/+XUqlBCvy5P/ZMVx6qkW+gqaRGAGohcQyWRF//M4xAsW0UJsVHpGOA0dG5hV3urrbVWemZlscWRLu3wVWpgR2Gc7yiapZsGZTk6TLs1USCDqCoqAmHQaQssNDQKgrEolkrERCHhQUe18IB0AHBc2l/8z1faqus+IFQAgWzMAYJFeEvr4vPHL//M4xA4YMcKJjEsGGK9evMye/q8lqgkER0zPyoGLc0RNABNwN8OBmiJ7p7n6I5hAAqHc67hAAQHA33d/QtE67vxxZhEQOLw4GLgY1n3qkKy5NgIThcccGf8IvRVn6yhdhHggsLC36tCjVi9h//M4xAwWubaMy09AAGWL62hK5hZZC/EuSY4XiuFTdimaySjnFRVdm4ZrlVhVr2ZrZmkk2oZVKBUHyiqlKuzNyraqvDMsCx0hybAsaIj2VO35VxH50s8r/1/1KDrXf2jkg/iCYAuwNv7iziun//M4xBAYmnK0AY+YAMcWMQkGQDFgDaw83k+7GhACJFklf5utMwK5NGJQ/qZEv5sZj5FmDN/zxmnumRUZArmyJxX/0FGhpdBzE6OgdZFUKX/7X/WnXZJtJSJgr//uUP0VAM8L/3IbYgrtlpUg//M4xAwVKVqsRdkwADkHOJMyQgaEGBtYYs9SQlm5TTdC5L6w7M/5aUkJTLSqmSi/k/////tdtUrsmMhH29G4jJ+fXeIxbAOUZqMCwCLPo///vVZ320qABOy0BMWgXu63l2lXOeUgrjHr6uoZ//M4xBYVOZrBvtPOfAZYMhXtz1zYKIKJp/A7/AznObV4mY6SiP53+4FG9zeV3r5oQX1cImcz7p+fzuPtbe5a0UJnXf/+mnYFdvu/15bbSwrbR////i+ot1lMgnuf9VpDgp/r8ViVhaFA7gjl//M4xCAdCm7UfsPK8IDwJ4aDXR5KBNiBInUqH08cfbA7ibnl0HAwxPJNuNAcN/P1qqLXTaPQXO4UtXOjsT//udkqqERv8rGtcif/8ioQ6nRqCahUPsWBDIEBgzM+BHmZxKoDDp8A/D+kDKSB//M4xAoXUZLVlI5OmDRq/i0q5rKk1RwwgwcBKNNNLufKPdTWtau6qF1GnZfhn3S5J/vO2c+2CfSsIx0s6lthKJPTRf0PU31HwKH3AMGn/kF5B+gO4Ijw0oc6l4sDdVf7atXtI5JG25IBvWPu//M4xAsU8aMKXnsG6pyrNYdr2u3lBvnC6kgOENJjXOFZq+x44AfEksz3c6LlsT2/zJ0zCn6DSMf5ETMfqZGzfK+f/3h/n9aOMYONFWf96P4inniPp6bM9EiRWuzW4Xk7tANiATEIKwyhIBRr//M4xBYUsa7iXkmEdM4WRAKP2Zk5P1VZTkWM/UrGMjlBla2yCWUv9soIU/ZjOyaslc2ZwoCXZ3mJFBWX70O6Xayyj4UeEPxOc65ttyONyCDvHuaYaEVQz2KMZ5asHlZ1rQ3S7UdKU29//0////M4xCIUQz7yXCiNP/ZUmP////8lP+31dCLPOdGtRv6vQIIn7AIomUQzYowgh3bDNpjyZO3oHJ9obD7ntvt012AjqgQUq1cEFhUeruoi1yE22VyM5jpuVnHdHUziiO90dXf/p9MjORu+v+vf//M4xDAUaxL+XCgFZ7bT7bOshCKwix9jWQzkIJI53nZjrRpzVUOYaVFFtv8MUGXafaGZm+skgTv5WNzeTIFNAkav2ASDP4DZ3F4+/8vncu4WMa4AVSK32TgFpP/2lK6l395YT7z334XEu4Ji//M4xD0Tia8KfMBG7nzCDmBkPCgFT6WUYNB550UNKiIKAqVVXQBZKRLVX0QsndGL1BDOsvyAhlI00wzU1q/eqPRXUW/z4H+vdpkGNbOZ5TL/E5+OOE0zLHrAIa5YqqvERXp/DXCrio09CpZv//M4xE0U+NLDEB5WNKniL/llnSNsRLX2RPaQAG3pyeDuB9HYjqesaFZmiqJs2soYEEpf3c1KN0CVIX590VmZOP9RPCLl4vNZCYq/pP//0WRpOYmYOgE2BhU6zLIYQFGf5E8hf/0qgIAOdBRc//M4xFgUWWrqXm4ajv/DOYGYqCnqcZ8uxpMU9hZjsSaA/Nu2qgLBcO73B8+lBCI043k1R2hgHy8akGu3ikcPjH8JKbt8Y15q/wv+ij/9sVcWk96etVReQgCABQSAAS9KbHbUcZ9s2HdcISgO//M4xGUT8SqoXNHe5MwhqRVvoHYlP761YtiW3/8QQq1bNTGpESTYmrgy7iVUpY0bNnHu2F0rrONfEodxNxCn//T0dE1a3b+AkZMGkJUjgACzPulOOLb9KtgMY382FztB+zWv7mkM4T3jPgqk//M4xHQUOSK1nhZeLLqOgn4xF3VTDbC+jJQNpqiJqi11KLgcgYFkn5w253QDVM9tOpq/zEUU+KQrFP9blUMAYkCLtgBWx1/cbRk9ASfHyrs4IyGIud13NU9Cm3rddUORLRc41tFgiTxiWtBo//M4xIIUkSLFnkYaNiyo9fRLIBo8i8cB2NiKraWOfh14s8D1LMtJlgw7S4gF+M35tCVDCUAn8AApeJfQN6GzED0aq2tPCVEzMIgWPLoz7Em5Knk/POiI0KIf9yIHQox7jMqYDbe5JNEXDCOv//M4xI4U8Rq1vsPOXFaRCWdmXiFUDxUGGLuCk1xWTzFnIJz+DBxszE6gqXIQIfYTDHW+hXu1qoxEM4205LvhF38fGV0S9DRrPcazotuDXL0ZbR9syb1jOs5wraHVQpOznEim9TfPd27Sl/5j//M4xJkYyVatlDZSNDlIHKJAinf39bf/M5klIFUxhmMYr5NGFmbJojy8eE/0ybgRPuDvMzx5psQg60Yc5kGgrC+DcMRXx4asZD8JYkSdpclAuBL3InhYC7i5lUADABAONVwHlMPGSIAVowTr//M4xJQkGz7qXnifH223I5Q5b3vGl2IsOIvK9F16MS1l+42zd6+vWfwbR9fXxeuY6moXnVMmXMyNYK/C/P8jpZtlNSSUzGh/czLC6nbyl+VrfJ6KmjMXaRw/bMjPv0i5bylSoWmHRJPSyYmN//M4xGIfs0LRvnjZPm67N68DWrWjomlMpCMfAqsZPwRRCWdCeaknTJallDAKIMlNZWEIFMXeMZ2grEqclM/qcnnESyt7KALnxMwzJZXNzBn6SBKiIwYg+fCygg48J3oMkHH2NEKkiw0m8mKA//M4xEIU6KLGfnsGjI2sf9iyVaP/1qGk2E//0y5vWjIJBGuEbR2mBZ3//nZRDZIXYGCGE6+uZVBjir9U1eaHXNMOmEzG7zna/CDGn3E4m/6fpikQry53GvLloAoESnPrO3KQTjoKLorHeeQz//M4xE0eiabLHM8QtHbdJWxU9PW+XBwTwvCZd9/4yv+bGn2kwYSJzDJGCxAqDgoZCxDk//l3qFi/9NVAQB6lDBQFvuv7hTiIRcll7c7f7gMIDmXW5XMxpSs1HtNh/jsWYwsKLgP8nul+YKdp//M4xDEc4Z6+VN4RBIS/bHKRuYNCGrJiuxRT+09xdJ7EqY4fuHYWADC1/Lg0CJV+2Nv/xQ9fiEXv+kdZXuEHmm2Ab9TlQI0F3QMBgZe82WVQ0OYHAS0VVhPEzMQ0izw74sO9TUursQD6RGgc//M4xBwW8hLB9JYKmCfXvWUJNW9X5NSvSIlfLI26vIZJCuxP0sVy7g0Clv48+Ey5WkBW3xMumhfyF0ziyUegxt+/6cvHvEc5csqVSBGVRuZ5Ym7gNbzfGcCc/DTf5E5Fb9X8qmEzte+L8U5a//M4xB8VqlruPnrFRvf/yDF1Vcs7/lIRMalW4VAMxET2Gp869u8RGxPifwT/m/XvdAv6P+X9Hu/f9aotUDmRf+NR/3SSkGIAgBcal4Hp/d6f4hzt+oWKqvViFbrur86ADJVva1jOKiYpdfFk//M4xCcT8YrOXnqFJFHFvB1lAeBp7KLiZ/IBP6hjfmf6P9S+zhWUeUHfy3ESIa+HaUJ/lwmmqApQYd01fOxJQmkIkmmeodKqzA2o4ZIn3az4Os9OrBAGmZSl2M3G/+BRIJAw16yik6+/CFU4//M4xDYVKPKoVHsGcP5cPN3VHNHDAPn3dEo5MYhBc0XEBeFwfIA+NLn44IU5CNQ4pABNOflGP5rPtb/H/MDfzt//+paf1+ub1T6qiB4aQwiLErZK6tvl1rYSaMlCGF63/eVCGN9cwRAEI5RI//M4xEAb0zqsAFlS/REmimgteCgnIDurpQJEdiAWEAoFB54rJQuKwubLoiAAAmAhGuANEvOZIXNw66Pj1Ygm5d7hRD8LbWhDndToQ5yEIhFc9iBRniOEQ85G6vReit//8t1d////7q+W//V7//M4xC8Vyyr6XCgT4xzNrPWeNS8fuxRLGyR8IbTU0PzVkPWdM6hwlLHSZC0JToibbPUvQ4uqqlm1+utsKiepsv/VkyThoVlTdL1zpUAOCFNeDplVVzYYv88ZOAZF4V+qIxVndAh7OjGGZ/kW//M4xDYUOR7yXMpEroaVRwhsFGrb8v3ZMYbJj////yoKr/1jGVAUN7hpWaWbTWxhj41eLV+LjVmVT2tBXhCk3FbjMLNa/ZxlVGRiVMRsWi0Z3Qn/0d3GEDHRkZlMc79T+Rv+s1UEl////p/a//M4xEQUoyLWfnmEqI32fTv27P//58/vKLE+xWRYV2mXb/bVhCrdxKwFl1yQ/fo66PypPEWoljVxPbtGrlDgPf2gZDjaSA9ylChdzQoZE542SIwM6DgbMIcCQgFCIx0k9FfppXJkq3R3/1mu//M4xFAUuNLbHlpGUMbFWiZIOb9vra9AnVqxnEHcPG2cyky7nkhgxQBUSTpNHBEACVDRneKKIhCwt6v7u1cwR0kJPtVy3qFOnUDaCQSnudZMEU2kd1wvu+0N6/v5UyIXHTqnVVeLV3aWb761//M4xFwUyXbG/EjEtIQen4Z1w8oYOmc7Ei0cKXq02lzQiLEELW1puXoNH44knf+xm7/MKZRGejJuJrcxz0OALWsGjopjWNjowE0USn2WiwWV/7aURq8+kVJVu322qBHZTMZJZ8TArJ7VN9Cc//M4xGcUeTrPHkmGcAE5gZOIFknKOIKmHo3SITJIwWMcuppGOU5HJgkH0W3aP8ru20+z+7PHxv7vyibAMav9lN7wFV/+VCZn//FqqhWhoxiz2rZxVtLDM545TVYZS6JNbCpMdLKHwyw2vEW5//M4xHQU0crOXmDMnLIg1traaSCn+o441roAmrkBo/mbNdN4kILMRFF+ldy5cQhQSm3f93+hZEWTkyJ0RAy3/+RqACDbXYQummam4gQQImFiQFIXLaYxJAiPF7hQG5M1Fm6s0Ej43ZECIqn2//M4xH8VESalctLExCInVuVh6vtduhiMkoqoEapSx7s12IBzMSURUfrnbdmv9FCtESzHr1UEARFhXyUD1AifQIlrUWicRkEI23ddjcYW+k9GM1qtMidukvNcV0/ceoMUkAxLJvl4fIZW/Zfz//M4xIkUaSKRjNpKxObo3808sTEAWDe9zoECKDx+8Z//pWO/9bdBNj/+lXz9IdVcq0dCzUDvUtl2PM7j0rcEI1NhjTX1Y3LEJ8BY31txKGJSwgSAct7m2jIADHLLKwiPant28whX/tMW8xtW//M4xJYVIQqufh5WOBcVi6ck4puGxfSGDET5wIAeIZPosrlljlu9u337bL46rDM7TCARA7GB/ixZud9NWlkpGkBxEStZEWiEu4oQg5+zv+07MZT9b/63UXU4upDelc/13GhFSRY2KyC3apE9//M4xKAkUw6cKtMLGRm0exht0Y2CbUSExIu1+PXx/pV7WldM7p9jOIbY7jUtpslrFzvVYWN/VpZLxNR5Y00tI7yypQtJIw/WluYXcB+7cJ3rgxx4Hpuje6T8mUMPUcZ2GIsTsjG5RL41eAM8//M4xG0jiwKwAMvG/b2GivYjmaecI0jgiDhhBmlNP//94Qun6Uy/8j8oQs76kirAVoX4QQmqFVzvJOySsdAq4RkjuPlV5kewZOhWYnzlRWy+xsg+YDxrykDhwGPmvz89/hr5ahscSJ1Z4FTs//M4xD0Z6yLSPmDE+bDKqbFMooBcYcU7l+lLPdvZNfzFUwmQUJVh5H/2NcprP9P+73LQzTiZFu2psgz66JCXgfs1GkB5G2NRKSnkT0EsDgoTKIKhaJSQbEYGEsRNCNMPX1JhI19JlDCxp2Ma//M4xDQUcTbWXjBHDOatIVEZkJfkaKAqRAXYQNBURFiKfqJFgKtJXQuVqQZzNwBrFhIKEYSFgS0x5ZYcA0cURsFqo4k0UPKbnYRDq8feQoRl0zI0dLKpkaOlk2sUUh9YqKhwEwRY1RJICEZN//M4xEEVGUKgThsGFN0eSHPF+DNwUBlzO99scPJNu21qQDYipABL6OGGvI5L5NThExREGhwAXhQGp1xMMh4IBVCJjKZOJW7epRWop4SpJNSDM3wiHsZiBoaHU8kO5lkakeYfDn3/Xh//czQ0//M4xEsVOa6Q3spGkHGgeEreZ66U5ZcQMAG5xS6UWPyoLcMCqhdl2JxnCotFEdhXKnhosWNX21q8rI8tjHk1NLWXZGzST7jZHJQ/haAv0s8ixzfJ2dbvN36vZ4l/r0oTS9/T9vOd3nCAxSKh//M4xFUb2malvsPEnAhL/t9FrsdG+rkItwjHMUwiAzjWXgnJtd/9amgUjckwwv/+bdzDEXrF7Mqo6NaS1nrQOnwMQHJrJsuPwe/256a+zu1d2Zm2HR9rUrtOVOmLpZL4iDyTI6Y1ofyunZye//M4xEQdKwKoXsMEnUB0K5JkAkZamman/96MlkKVJvqUKBAyocQZFVHb2Zj2MLGFPdSNfP9lCju8PnesWL0gRSJJZ2OEbda0KMCdWEhGW3F0rduOoszBEenAAIQNs3wGzc3qrGhtr1UgAJAR//M4xC4Uix6uVHhE3ADma/yTL5afV/q/////3+TPa3/qhPVyMraM6oAHCBUqGKKO9SosI1+JHhBtzKdi6LoEoL9SrGAoAUNTCkcItxzF3KOydTzZYTLU3msZM7Wrv2+Nk+Ge271C354MbqM1//M4xDoUkWaiMsmE1Cbb+rBnu6HxETVW77r8j+JT39v+2+sNhqoHhTkcgBlUAERhVhkQ4zpyUSqeLEenbor9kSsBpa57nwQs3hAicIrSEn5Wy7GI3QhAcGseEzN7QmOfaDqTaLREHXe1tmT3//M4xEYUySqo3jPGMoRdQVj3JUvBXtqnAxTVACIKoIY81lrsMDWLeU1f7BavigC13ApJi8jwXYTApYxUTgHR1m20LhHWNuy6PtVRra46T1uhbWgABL1WvhK6d90v2Dxu1X7HEvHMi/MtShrD//M4xFEXYUa1ksMw5AfLf55yufqPkP//+cVD4b7/6myAcGFYEqT1uvXGnK4L/KSGnHBOw2IZQVRCSOw3ImSrOxv58nQqBZdjXu5R5kKhVHY3nK0pIFkkvluGMNp4NggKVWY2sUBOy2rjy+VQ//M4xFIgSgqkKtPLhOJoD+UDdaWqSJiLEWNXQbsZUIbPrbx4p9v39/moHA7rINMRux19C90OMcFWEn8QKpEzaQtyYW//quhj71eN7kLOOMkrSG4CMibjlJ6ij+gCPLWf6WHKec+90kABk9FG//M4xC8fgzrNlnoPMwRQkRCKnzOAiIkKlWS9/+3/IwBY62RrB0Ie5ZgWgFEB1Gik+3VKe6L57d6/mg+I3EVGAwLkTTp5zaVctVnQ8y2p9v7K9zGWPj5UoSLkiBBDBTgHKx27yQY99aa0mIF5//M4xBAU4Z7O/nnE1Fl9g/RvDLVEH1SRLgSzWIgXOOXlSXaeJLfGpraIVbugGiSLTWijGUvMhv0fmmNWY4CDAItO/1SX/ar/1X+oPiBRQM6qdEcc3ufu9JG8VpLdOjHjnadT7QJj+x2a0gg7//M4xBsVAabGXnsKTMsXBCK9iyCBXOUuyKxRhBzv+qmccStlQlnZX7fton1ZhzE3I9sUWYAJpct7gmf05MLN0/3IWFCLqSAIB1ZbZdjAerWWAMR18FyZHhVR3POUEjjNO7r3EcHHn9/EOS3+//M4xCYYGbK+/I6QUHB+bE14gh2/64hj//Ym39RkgKD6gbDEgvd5+KunqaYyofukgs84T4ciMOdfZ//qPOcR9H/1JDQCioK1giAuucr9sAc7WAwXBjqloVTnRdhwhIVl66AwQEKXAgZUfREK//M4xCQUWaq5vhPELCfQGBO2hQom1qGfVnKMjvVgwEcv0ROpW+ZpjGDCv0KrTwnszwiiH0OlaP1P/eoAAQxAXGMEs3cmcoBFYx1y7IOsPd7bSBZQkZA8bw+la4QPTPTPGFR/uHJC967onu7u//M4xDEUcVaSPNGGxOnnu7ni671zsttsffxZ4QGRMGAwOc5Quf///9Sv40uLrpA3r9bbdgMBEvWu5bGt7wbddELCYOmNPpVixgQKJCVHRLBmi+C1jgJGvtYxvMYzi3ucy6sHwZuRi+QUK/IQ//M4xD4TgXLmXnsKyq7pFCDegml8yy4////+ho7o+b1+qrdAAAHagOnpbK8FAF7VX8xkywQRBUchlVI7D8RTPHKvR0k9jv6fJNEhl5kfG+IxuO9ycv0ewbTfX3aOTYGhTERSW3E5m/W1lFgQ//M4xE8geraoKsBZKBIOTc4Lo2EfzjWDgwrNLzr80m7b/9N6TSZn9v85U7P5/dXrIqX62zM/M2czaSw5gwbs3VBp8frqMgpok2UePCE4qKaBgYCewJhHk2sw62QSrKJ5n1nvrf5r9n9aT+j8//M4xCweYrqwypvTpO/Wh6fUxQqqSjYwVL1V+8YJxLJSA9dp4EbFohOEOxuHLV5qDAh8/Nwyi2H7BLlfMT/hnEYWepUEATM1VVDf/5oo16TRuICgJvpdB90olUcVGFeIn0ADBbGH0E0xqtPf//M4xBEUSarLHHpE6G5WErNqpgMhKiQnBN8yIk6DJhRohACLTv4WNO+6rIezYMBN9PUuzf//muZ2QyiS3MpQpSpIFTCrv/xEalTv+TQqbFSbttgSScDvviQCr696082V5JXfeVGImFK3pVW6//M4xB4XEzbmXkhNO/V2uM/eX/6R///8wefLkjB6/833+BEns6BmbBDH1oh7/jxEREIp2+5zIjOUQxoiSdA4DSIQCKJpsQQPTPJjKo1HZJbYwa8WTlX+HlvF51My+Hw4WkTZY7iOpkZMhJ/7//M4xCAaMzruXBlZcy3/06///9///PoqRjjXHEK6kEQEurWetO0d5WFlcpp9VV96YYXGkTow4f0Nl8wK5ysMK1q7PcZmC548uI5XLtD9CXKWKK3nND62a3ba6yMQpaHBBRxnCi+hloREQEqI//M4xBYT8lryWhhFbtddrvjObw6cl+xxINZqrc3AxPrzxDOZCk/+SaCblGQ+ULL///pozOzDkB5bWptgug1+YADxATNoEyJFWGf6260QVq7UmZQ+LanTeIhAmkPjZOxFRQdHAQyt63HJblLm//M4xCUVIUbq+snE4hrN8uSq1E4TvbY6als7GfILfRyv1sf6ndyAQowZH/+HQoL9ANf/1jHrf//+RlvBqgABVtZmq7PL2MTcMKmneJG56ESzcrTOpJbX/WDGkfoVqzkSQQYc84XzqRqPwyjJ//M4xC8USWKmWVloAPqPpepNJ2dqP60UfQNUfv9jaZcEl3MIiVP//+RDVT/iLZVVZYr//7TbbkkkjYAyXy6ogXWRUAcoUkeaSSvUeZUACGuuYUFFgcVlgfitAbx+PDhpwUJXM0y1A8a02pd9//M4xDwkUx6xn5pYA8fXUVP/8f+3/533LZa5DmL31E7vc5T2Q346qXV3DW126GsmGtJI8keSiorao1En2ShC4mEVCw/njAeg/wapM11yl5aee6HJOX6+f7dNG7JZTHM6RkEVjFktFttttttt//M4xAkXaucCX48oArbW4hGZ5aZ/CRK9W2z4e5YP7B8XVkdbHOqXSfOcUuZboZLyKchOiOm705GkmbSrfahMje5i1+hMiNs+6M/96N/6qmr39h8/V3RloeLgRn1zn/Av/Hpy26y/azSAAJRG//M4xAoUsqK+X8YQAGpAQuto3ovcXpcVX7sLGCo7bHEFIY5D0MGORwylzJZWWp3IzWghSp9WUpSCqp8vTzmmMamaYz5abeb0///+vCiTo8NfgXlYqGosMQj05ZY2KVjw36LH2EjJOSRjjwFZ//M4xBYUubayPnjE8DKNgrK/RBkIt3rcA04XcgBJ3QiM6cRCnXOu7mLe4cWRNGU88nkJ0buu/Q4dBjhh2i53/uprTFQ6a0f/ldPHn/ySlRLDrWPPynlLaWM2NtwGDCsy1DPY9JkezMZkuV7D//M4xCIYSbawAMJLDGKyIdYfUFBQrDKICRzTHtG5dKcoEAoYJQ2UKAEAASHIhA4OVGOcn0I1XIQnop0ZgHA4EcGJyrs/4ADEQHPbDCrQ14c32SAiXzoHEATWW+8HYqn8F4ujkFtIS3J/q/AB//M4xB8UGbLAylvQqEUNdxHoFtf2yt9jhXju8mm3D8kFJsjRryOhEmqr/mfhL572Yo4oQiDjYB5sNf7/2G1VYEDALKG6AdQHh8uw8EAbj6pcjRGC49QmFiw2iMi80WbX2ruyLUpVLlzKFEzZ//M4xC0T0arGNgsEHFvmcpcMKJo/Nbze/syscqIHPPdylBJv+siPDj/tcLAms/oVwAbqWXhO+BySVgcWbStDeIkUiyMMsaQhlDBAxu9TGsgpyto5najJ0c15W2M5gJ6GejfzM5Sl95SoZRLM//M4xDwVGa7A90kQArbqxCdAoaAu3kYNfysRCUsVln1elg1IicoAH9/7ZKNSWwUBABzxCJDa/rBUJOckIQB04sIfgIJDQkrubo9rTZaOsgASQ+gMAfD2PwC4DkgC1MQDSxysl59G5gkM+9p+//M4xEYmGyaRn5lYANtxD99xehclyxeXtL7j7d19fn1SeT1TcviqY6KuDtGy1Qw9tHeXkkE8f1jdeZ3/8cs////qTNpufebrok8///////////0s+oZUSfXafH////M1sVIqFttttttFttAo//M4xAwYWy7mX4w4ABQGBI466QNKhpdIUZ+4kP4EYuLHlc8m+g9RGM3Iu7atduxy3Z/f63Wcrnf2/alC7X0o+vkF70VzFccNk////0qOEGG5M0SzBeEABg25v+b//8aCR/1KpBAds9n8jCEO//M4xAkVGqLOX88oAPfWMJgTVWvo2ddiV0bf3XKKj+l1M4qr0RSVGFYrK7tv0b1MqENQRQNFWEpjqp1IzXVFTZZHM+ioUYMp///rT/1rzp/+0gsX/675zRVoJVy3W7WIMHhSqSIEmrGZyAwg//M4xBMVGbLKXjpGIL6Wy7UCPvwhgmRIwOkFQVQimfn59OflQR5xIavldzIr7mU0RC8lXjBnUm16RYzEIQmtJKhLf8v/aXuyIRJnwiaDakd4lmaHX7WVABQQwse6D56IqjCa5JeBtWImCs0q//M4xB0UoU7LHhMGIKn8VcoCWoJaqW9LzPXK6b5seXJlRmlTpq4vaL0KOEbmsNPFCLj8Sqh3/LmpVnZ4vYfNhKzQypXnI3JJI9Eq+0rFYphHAbiaABgIBTwGUScH+XxCA2F21hueiPT7gqkG//M4xCkU2TbaXHpGoiONTbyiq5Mk/5uabEj4IACCTY7KrUUMoc6JwfBJL+8Qolrv/9Sf/16k8zU6O1/xOJEKAYa1LpprxDowVBQXKd6RLuL6HEIGdAILUmN1u6I6tEdit2gftc8i5W6ZEDWV//M4xDQUiTbGXsaKcKomnnOPb69IgOfU87SnGAg7/dnCb////+tcCmf+CSp8UMmx9dnAI2LSI8/A1mLcrYyLsQOiFRI0F2Yvj/aQLBrZcbJipL/SBwEruy7eaEzH2a+aGsY61CClEKf9yMp9//M4xEAVAbLGVnpGyO599ZVEAI4cCnQPYWKxn//PYiYWciB6Bxm5ILwu2xnaqZyAaVFYep6t13Yr7YNjN2K3BHfyq3Vvp+X6v83///kqcplK3//q9aEI3oTqBoIaosiCG86HkEKiGhED/wcA//M4xEsUepqxiEYEMRH+CB4eI/fjOwA0NaVQTMKsfUgtYxinNtTpNV/cqf+/ru57I48tSDEFmFxgqYYLhwIKIhxRAUeuxxIofO7MOMMRVIOlYjruKCjRQzlFSj4PolW6ezf/+6NWAmNxh4RV//M4xFgSsxrFimgK+C6EmwizgOakTIrOw6gmyr7AadRG7uxujlGA8BwXDx4iOmuhRY+FAACtxPoyD9bf/T6noY0XWAGYqPKll10ud975sIq52Xb7e2wrKfPXLrYFo/+ctplHHP+tAdtmRFx1//M4xGwT6KLyXDYMDu3yxsC5sZ6VIUMX0pvNmczNCP6UBCXU0T/0hHTgilWQQNR6Ow42bcn//qPRKdV/r//+qkwRBLZyowUfd9vJ7iUpHjnS+JlG8VTGmBUCVQqMTaD7Q5n1Gu1hmLUB74BC//M4xHsToTb2XMMGyhXfcckE//mIq+9j5/7IFxnhQrL1lJolDb923vNAuA3f/+902+b/N+EAVVBvgWMdcyYSAiOZT0+o6qWH8txsBS6KZlKpvlqsnJMLFlloz0/fKX0cvmN5mF2YwDCxXo5n//M4xIsUuTa2VMPMWGuVKuSlmV76vLyGEhYVV/b/iUjUBmimLiAwWwIwB169580+TrrHjfmaR0pWX+sxfkZlCaUt4naCSYs4aGAHliSUOFhGuvhv+5j1bi5WyQUgRPaKVh7yrFB0sJQVJ79R//M4xJcT+bK1tMMKkEGSF3//bpvPjQABlMVo2vAz1SV4wsswW8SXvhUhu/kFgNOzeG53BwiIP9cyxj4K7MkCCkkxbNd5sFNT/q/zB9lRhABg+lZRFyVmKZtyjCMTYm9klETajD2k7w73nP7S//M4xKYUAT6ZlE5QFHIrEo9jdVTWb/RdiBUm0lbc12BjMzbvmmQQN+PAb+yFufH1d2xKUkSMHGi1aeYZgnAxEaLmPEvw0HrYmHBkmA3ZWLY79Nfc78cFCFbmRp9rGpmRZEUX/Iv0bIWTOFV5//M4xLUYOaaaVtPKyFvhUEVDsp33p10tmtPRJD2uGmTdETrGLue56PnXqiQDLm1pyMRaj5cicVFWBqoXTzvMpLEYrJ4Zmo1lL6avSfYfB55dlevcuzWOF+8KfU6OCJzIOBAgYoAiXVwsSrPp//M4xLMcSn7GXnjFNtM2EmCEoj46OVE9Us+WktAZe8z/8P0q9y/c//937BrxxSf9OyaNRLJ8xBoXQF0GOTSC82///395KhU4jhUL88BwhivDGWsAA04G0GumRSZIlRsMn3paWqdzTU59RbYm//M4xKAeUpamNsDNO4RnKwhGBAjuyIYlKrR2O8KCe1gAMe36N7uv0S9jsyLZG991IUh0//3vnv5HbZrbaOEopq/f97y61SQ4YSX7X8DIR51lsKsIGFE95/cFKScNLoKOzIEKeszyySllRnU4//M4xIUXmuaMzHmEXZKZlsTtfy8p2HqwscEQHSaNkZVJXSh6QaF3LxqYqErP2i4gGTNoM7XwjZ2KsQoRq1/X7hSL4lIrJMHMAEpuJRXsJW02x6Z728pi0j2oPl806nWggEVmRKJFCGP/9ZAb//M4xIUU2WaaVDBGwBKxBnKEcPOeUgmHjDiS70hMKGplNehSP4oWKL7KzHFmfhMxH/OasPD1sM0mwh7InQoLQQSrNhCeE5oDBEPgaCkDQLDQNCQyQNAAEx5KX/wrq3i0tc6sP91GaCBJFe2A//M4xJAU+ZqVlDBHQG0bdzYjI8WiZ2+3yUtraL9ugk9B0d///SoN1XAaHuADCHfh3EMwsNA2FOXDwNnjAOHI7UYHYYr2FTurFOquB9U78XLFLaFyysmEQEBATvEBc+lyz4uOBcc2TtAzFi4s//M4xJsVGaJ4AHpGdDAwHQqIg46e/3/5Y838JKJVACiwHgAy0zFcWEYE+KRI9J9oEqjzkCAUaoEtZEhirWm9AhwtpANAve3BaOxch5YPf+3anYYXRXtoEwgh0syeVnP1F2HWo2NSpbHsu/////M4xKUVmJqWNh7YFFONumYRPhkPqoAzHaSjcoqyq3+6yLolCrq9e1KiUqMFJCor26Z2gAKSap2RMs9o1FTVss5qCi4zI2otSHisGUwC+yi5jCx5HhOh2QCMs2im68Td+JMsFpZbWnGZrUPp//M4xK0ViNKRdE6MPKqt9qNRg4oT5wIgcsEORrw+4ELwIZevNGIG6e3/+GBigBWBMQAuR1wAO95/uUYDS5xWmQ5EYLkbicQuMR9MM5BSrROZIo84dyCb3//4U9/WX7+IPdS16W9SI9jxZtrS//M4xLUd2XqxnsGFYu546ZSgRjAXC/a/SzaV4Jqa4ESGTsrjSpHBTibPIIYmWJs+TphcYd98F0D2ORqqL47lMB4UZqM4dLZmDhBOA5GQGBDTE5bZpbO5/ssiUQg1/7nWKbEwi6bktvAhXjXh//M4xJwkMd69vn4YvhgiC1xqHWT6tLygUBAOlEQR2f1e/4LF3/l+3rR5mnw2s7m9llYBzhhrcS0zgX8y40tJW46Efq1pEMJwhGlwiTmHoMiLAvI1HhSBnERPnW6pq+YcfxoEbKCe7/0/mkxb//M4xGob6dLmPnoefk9TRYSA4nU1BbjLsL3OTzKGRtNRLsWJugjpdVocSi73fZIBwCeL67AfwmDzX/wXt/EpaGTkCmo3PNVKJKz33/ZlZ77/q3Mkf/cUveBfiChez/yi3vgoQ7alXr/ZlufV//M4xFkiaq7SLsPQ1pb/lf3kOQaiwihwUSKmgmDQss6hSd388XU+VkG4ynWhjnT6wWV8ZZlLyZn/DCxIUOVsxxnyAY7eK/428nEvO3wasDmArCMMG92KMK4fiSZkOnOi/8zlw5A4BBRzUYgk//M4xC4Y4VbePnsQ6sAOrrj6wDc/npgV39MHQ0+2Gmxb36w6fwcqC3qEwlARU2We+R1DngqnrlRRksFD3o//yrQutZlUwY2nLt9hcUbPBICsTWuH3UEUEEQLv1R2iB/yodqNO3dAFfcxm71g//M4xCkV2kLuXljFEqX31UBVSoZqpFQzsqrhgYmdzWM/7/N9nf//c2ilyf6hxhcRiAwPnKLP+rE8Bu1KgwA5ttQZNrOEaAzyJrzyRYAYGz/r4MgOfh+FMx3qQOACOYSBgYhTIQGHcj+f2OeS//M4xDAZWyK43opEUXc+2301TIf9y8qKuc/tJ93VGpNW6ezseIMDdhite7F/OgN3QK7HQQH1eWjWDB4AQOqBYCPrFUMHQGO0AWMsf5l0zmoGvM55rZUJLORbtyG5WnefyA9e6sjszK0vpBM///M4xCkYiVKtlsvw7MluSOBr6x3o+f/W0ZRBG0u9vS/UFBJYsvnKvarjl33usZXLEQcBxN/rDdPT85+U/SPaCMmGG/ZPSCpTCABckAHNd/87YKHllR3t/tN9IHXa914HdBM0Zt9w23nOjfv+//M4xCUduhK5fsvM+O8PG+v7safVX1/l+uGnfxJIpFtuYorAZA9BSg/tOEFOSIgwyw0uxubYspZUVU5BydoWz53l/HvjfsmA0zC4ZyabTv++Jr+7uU1WTJg+lYf76C+NuUlxoF27cZ9/8L6Q//M4xA0Wqmr6XnrKrr3m1HwEdQ2PJ7wLiWr+RHHxn8Jh9e/4q9uNb6N8h29RpfzdomAgeJZTiBvQBTHSrhMUQnUxV9jKyyCJhe1lSnQVqc04lf9AEkxif/i4qqNjgE3ADIQwOQLFVhqgj50y//M4xBEU2hbFdgvKOhzI4lC1CrqOT0lsX/7YVbFr8okZHrEgGs9SirN6GMhmCQGFn5ZS/7tUpZL/0eUuVS//6GaVHURCSh+gs+JV/xFiI9UCAN17AB3XpkXAUlTkQxnb35RkRV+yPGE3x0Bg//M4xBwUwf6xpqPEXB3qJGHIwnOqIt9n4pRMCcxnkOOnGU0mpWZOLCDA3ciuQG6WIdf7esgqr2Ujd/////sQc4PZcmh0YZa/CrUQ7g9rBYi12K3oKVOrlyZ2IyFlASAFiT1GgpDJpiLAv1WK//M4xCgeOa6oKsPNLM0V2aVQKCMltX8G2GIGWFW2g1YQhhDyWsj5gaiQkiU8zkySD6BWEvfMlT/DVrV4Gr0YGukS2MGnOfiLkgk6JcHAZ4JOxAIJBuVCr+skalQiLBAxU7SABbaB/8avBJp6//M4xA4YQcbOXniTKEDP2t51L4AfgHhnhyrpdgEwWsroFoPSR/sk720wE3lK/mFegYBCVy0TP/y2VF2/tStRi9Tyozr/xEwBDq6YpQBdzEWpCzh5kWiyg+oc05p58n+USJMHfcIAWwC5GXAM//M4xAwWwnLSXgsKOdKf/SIixfnPHRuWWCqIqZpCNpmaCYfO3QW9VEWflR/Vv/6P+w0RS4o4SIt3KFlMQouwQYrvp1Tb2oi3evoqj7CgfIoMUeoH3/vs8gYo2ZtoxMpSRsBuAByPlnDtcB8P//M4xBAVGlbWXhvKLhR5bCBhoKNyWYTBDm0EhI/OJmFm1lFS9JTLdDP2aYy9Eql61b6L8s1cqOxWQz/5qUNM9WM+xWWj6KUgsJTr2/Qt5IKC1ef3Jd9Zd+AG2pyKJAQUKFig0oJTWl6JcyVr//M4xBoUomrZvkBFasVb/JHXV4ySTGi1VVnbYnSzEAetn2Uw/iLRTk1UBEOzGLcz/9H8tW72KjI6tdWRbiTzt3I1HhgslS1JbSbqAqGBaYVIIUSYxwvR9F6i7zI9xuuob15G+sggLP+gLcqO//M4xCYVCZ6Mf08YAMZL8v3fFQ06jBjy8SifKTBneN+v+eAnHytGK3fNLF4skjvUOFiqUo1hCj3K9/aqhNSEcXa/khpW/V2cOGd3eS/y9FvjxY8ZFBM7mzytR/8uJAUFoOyCHCUX/FpzAMLD//M4xDAhC36YAZg4AIxw8PDYbfxcDw0VkzHHRFB6ZKCUlU+Pu7EyJMwwH4lgUAYQBYDwgJf/6u5h5FzDFPPPPIGISdyhSJf//HGY4gcx43SpMwursyHnqzMppv///Un/ytVYJZYdn3xDVAh6//M4xAoUUaK+f88QAMsriHEIuysLFGUwGxRzXtiAVKzBi6lV8x6wgC7vIAOWynX1lb2MjmopVsvfff/7e6vGGKsFQo89UDpeyBlEavET55j/4vlKFW1Frtdrmqwh6zezqJLhypHyjkbcSY2u//M4xBcUoTLOXMJK4reztUkE163Mzc4wuK2bdJQJteB3lSHTq5nCxEu7gRfNbjEqkVFjUivy1KDYGEKQmDe8Y+xg1C/67L/+tY1nvv99425RRdy5puqfF73AgxwRot/6C5ksZjlavThFLeay//M4xCMVAa7eXsLEyrDZ+1sD5vuUE76BSXs4U/UK3ZGY9EfRDE/+v0PosSFWrpUYRO1BdtnpMjwgoZ/u/+lIw122yUBfItPRtBwAKrrJxINYAQqbYD7ZTZh6q9seqa8MijHXX9ofN75wJlIb//M4xC4VCaq+XHpE6uq+QW/KZ20WrbPrVU///8ADrVkMDT4pLHdnw41JM58QZYUgP9iUKlAjbJdbUUoBH32NqEmEvfvI65D9cMHauG0uoHYkjfiHKfrmU0otfVUN9eAU03BQfFKwWPpC4cgc//M4xDgU+LrGXnjM5h8PtM/mqiREKESElWWasRf8VIqIAUz8JNSjT9cAOeheD7a5VttLOwMSreVilSHAYBpZS2f5UAIM153C414Y4bOJlzYDojGx1eoWd9jkbfR0IoNUN7NuZMrT+6cpe1kc//M4xEMU+a6QdNJElCgIHSPF943EQa7iz3HVuO/SkBoIYopdQNhXQbciaCQ0Vwghg3OpoYHUjTWbkRnSKW6xJI4lLVaJhWhVjGdKYiw2qxxhJnKUZ+kbPj0GyIGDg+gWUwUZ/vQ0S/pEvftT//M4xE4UUWqEFMpGkOS/5eoAdIyeW3IC1fUiY0GBw460sI3KbuSKpbTlsSahXSEKmm7YEAHGmTAlKAUAjSV5zviESjQs4AnWqIDzSD3u6xMNdPtIj4419xcmouy8Ki+8AEVfcmoEtqJ/O3EA//M4xFsU0KqRtjBS4HVW3AMXFllgWXpkQQU7yTxJ4rBZmtUUdjRFpfWvd+f2A4FK8fvnsYIoBAocGoSpKDAUGUnMteHm6UKKnJlYep7ZU4KtGh8RzNqzxlTv0wb445W1LLQ6Twg+pLCmbFNO//M4xGYVOT6NtjIGdLbCWnvCtShac9hjGNSqxhzkyarbPp5h3lZlFGd0S7Es25Dl/+DlPuv7syortIpSEpf6v39q/y+ti7nfVe+0kfIr8cHVDablqoEuAcFDai0CGUKDS6ys1zNMWdJdNzk0//M4xHAVIuqRvjDE3D7O+hzGtsmhBqvD48KxULjjtb8x8q6Q2TACVL/UNfaVggEusF9VmqHWUiy3CmmUScLMtq1n2Pr6SKob+i6/W/bAGVyGsoxOzUxE09+k+b/5lR6LQltE3X4mtA1yIzf///M4xHoVCap89EmGkHtOUumVFpYaqnLYRmyFOEXN2RkrRK9GZdPN7/vn7Zv63BIbNC6Ve+PhUWrqDGBvCKHISRCXSjgqAfysJUjWvJ47ehKUpNDlTpmbJKOLFh3ddFR4K/P/8iOw6V3MTkqL//M4xIQUEoaZvjDE3G+ZSlSctukQcFzhBojSKqPPKoCz6l6qLdcXQlbsfX/Zpv0VAmh5QQR9L66hWliM6WCFHmDne5nmpoiq3zhdCBgwYBEgiTadyDbxxX8o8OcUcplwhCKPKgYZBZaEOif+//M4xJIVWY5oKnmGjJx1VubzN4DBve5GLCt9v8jucHrif2ilySsNSh6ifE4WE87nfMBfQhuD69qlEIVOVMb4qbOSrPbJec+whlLL/b7ufv1Gmjvi2hPmxKKKh8lHHdz4d0M/P7PzPn+ik3Tf//M4xJsVAdZwVHmGaNY/cyPL5cy2hVi+N/yU0QKaRSh3VW4W0IgZFmkBXRMS2rIkpKDkKR6VA2bdWsJaiavS5nY7ELGIwKATqR+Rkzk0qx1Q7dhJ82WbK5BngdB1tQeHSXWNrqJYTGJv/nFq//M4xKYYMppsNHmGnDrkq/S6UnfpE0FMHQy19s0OcNQwkHRrGKtnoNVNrKsVEQaLgZBYXBFACRQTKuk9VJNOGx9YOJlYEaxqsP/zyn8NYamqNVjKZUmqxgzrsYkiIgUAuPDISfyQUCoCKp9j//M4xKQZEppkLHsGDAZ/2BIBFSxI8e1KQw6O/2pMQU1FMy4xMDCqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq//M4xJ4TqWIwAEgGwKqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"
        var wav = Wav()
        wav.wav = wavBase64
//        wav.wav = emb

        val exSynPath = "/sdcard/MyApp/audio/example.mp3"
        val apiService = ApiService()
        val call = apiService.getEmbed(wav)

        try {
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
                Log.e("API Get Embed Failure" , t.message + " "+ t.cause)
                playBase64?.visibility = View.VISIBLE
            }
            })
        }catch(e: IOException){

        }



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