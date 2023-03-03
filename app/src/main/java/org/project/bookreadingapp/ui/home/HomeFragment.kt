package org.project.bookreadingapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.project.bookreadingapp.Contact
import org.project.bookreadingapp.Help
import org.project.bookreadingapp.Instructions
import org.project.bookreadingapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

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

        return root
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