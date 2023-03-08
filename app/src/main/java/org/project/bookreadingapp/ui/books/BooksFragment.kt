package org.project.bookreadingapp.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Api
import org.project.bookreadingapp.R
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


        return root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiService = ApiService()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerview:RecyclerView = binding.recyclerView
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(activity)

    }

    private fun getMyTale(){
        val calls = apiService.getTales()
        calls.enqueue(object : Callback<List<Tales>>{
            override fun onResponse(call: Call<List<Tales>>, response: Response<List<Tales>>) {

            }

            override fun onFailure(call: Call<List<Tales>>, t: Throwable) {

            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}