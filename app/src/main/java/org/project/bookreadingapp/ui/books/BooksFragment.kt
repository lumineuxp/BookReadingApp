package org.project.bookreadingapp.ui.books

import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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


        return root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val apiService = ApiService()
//
//        val calls = apiService.getTales()
//
//        calls.enqueue(object : Callback<List<Tales>>{
//            override fun onResponse(call: Call<List<Tales>>, response: Response<List<Tales>>) {
//                if (response.isSuccessful){
//                    val list = response.body()
//                    Log.i("API","${list!!.size}")
//                }
//            }
//            override fun onFailure(call: Call<List<Tales>>, t: Throwable) {
//               Log.e("API", t.message.toString())
//            }
//
//        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sampleTxt:TextView = binding.Txt

//        val recyclerview:RecyclerView = binding.recyclerView
//        recyclerview.setHasFixedSize(true)
//        recyclerview.layoutManager = LinearLayoutManager(activity)
//
        val apiService = ApiService()
        val calls = apiService.getTales()
        calls.enqueue(object : Callback<List<Tales>>{
            override fun onResponse(call: Call<List<Tales>>, response: Response<List<Tales>>) {
                if (response.isSuccessful){
                    val list = response.body()!!
                    val myString = StringBuilder()
                    for (mydata in list){
                        myString.append(mydata.tald_id)
                        myString.append("\n")
                    }
                    sampleTxt.text = myString
                }
            }

            override fun onFailure(call: Call<List<Tales>>, t: Throwable) {
                Log.e("API","Fail : "+ t.message)
            }

        })

    }

//    private fun getMyTale(){
//        val calls = apiService.getTales()
//        calls.enqueue(object : Callback<List<Tales>>{
//            override fun onResponse(call: Call<List<Tales>>, response: Response<List<Tales>>) {
//                val tale = response.body()
//                if (tale != null){
//                    myAdapter = MyAdapter(requireContext(),tale)
//                    myAdapter.notifyDataSetChanged()
//                    recyclerview.adapter = myAdapter
//
//                }
//            }
//
//            override fun onFailure(call: Call<List<Tales>>, t: Throwable) {
//                d("BooksFragment","Fail : "+ t.message)
//            }
//
//        })
//    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}