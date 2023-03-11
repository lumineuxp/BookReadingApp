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
    lateinit var linearLayoutManager: RecyclerView.LayoutManager
    lateinit var TaleList:List<Tales>
    //private lateinit var manager: RecyclerView.LayoutManager
    //val recyclerview:RecyclerView = binding.recyclerView


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


        //create recyclerview and display data on books
        val recyclerview:RecyclerView = binding.recyclerView
        linearLayoutManager = LinearLayoutManager(requireContext())

        val apiService = ApiService()
        val calling = apiService.getTales()

        calling.enqueue(object : Callback<List<Tales>>{
            override fun onResponse(call: Call<List<Tales>>, response: Response<List<Tales>>) {
              if (response.isSuccessful){
                  recyclerview.apply {
                      layoutManager = linearLayoutManager
                      myAdapter = MyAdapter(requireContext(),response.body()!!)
                      myAdapter.notifyDataSetChanged()
                      adapter = myAdapter
                  }
              }
            }

            override fun onFailure(call: Call<List<Tales>>, t: Throwable) {
                Log.e("API","Fail : "+ t.message)
            }

        })


        return root
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}