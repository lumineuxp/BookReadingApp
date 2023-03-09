package org.project.bookreadingapp.ui.books

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.project.bookreadingapp.data.Tales
import org.project.bookreadingapp.data.api.ApiService
import org.project.bookreadingapp.databinding.FragmentBooksBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//import org.project.bookreadingapp.databinding.FragmentNotificationsBinding

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    lateinit private var apiService: ApiService
    lateinit var myAdapter: MyAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var TaleList:List<Tales>
    private lateinit var manager: RecyclerView.LayoutManager


    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val booksViewModel =
            ViewModelProvider(this).get(BooksViewModel::class.java)
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val recyclerview:RecyclerView = binding.recyclerView
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerview.layoutManager = linearLayoutManager

        val apiService = ApiService()
        val calls = apiService.getTales()
        calls.enqueue(object : Callback<List<Tales>>{
            override fun onResponse(call: Call<List<Tales>>, response: Response<List<Tales>>) {
                if (response.isSuccessful){
                    val responseBody = response.body()!!
                    myAdapter = MyAdapter(requireContext(),responseBody)
                    myAdapter.notifyDataSetChanged()
                    recyclerview.adapter = myAdapter
                }
            }
            override fun onFailure(call: Call<List<Tales>>, t: Throwable) {
                Log.e("API","Fail : "+ t.message)
            }
        })
        return root
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}