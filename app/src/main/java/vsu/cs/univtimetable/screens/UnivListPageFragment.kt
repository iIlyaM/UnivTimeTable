package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimeTableClient
import vsu.cs.univtimetable.api.UnivApi
import vsu.cs.univtimetable.dto.UnivDto
import vsu.cs.univtimetable.dto.UnivResponseDto
import vsu.cs.univtimetable.screens.adapter.OnUnivItemClickListener
import vsu.cs.univtimetable.screens.adapter.UnivListAdapter
import java.util.zip.Inflater

class UnivListPageFragment : Fragment(), OnUnivItemClickListener {

    private lateinit var univApi: UnivApi
    var data = ArrayList<UnivDto>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UnivListAdapter
//    private lateinit var call: Call<UnivResponseDto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        univApi = TimeTableClient.getClient().create(UnivApi::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_univ_list_page, container, false)
        val dialogView = inflater.inflate(R.layout.update_univ_dialog, null)
        recyclerView = view.findViewById(R.id.univsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        adapter = UnivListAdapter(requireContext(), emptyList())
//        recyclerView.adapter = adapter
//        val token:String? = SessionManager.getToken(requireContext())
        val token: String? = SessionManager.getToken(requireContext())
//        getUniversities(recyclerView, token)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = univApi.getUniversities("Bearer ${token}")


        call.enqueue(object : Callback<UnivResponseDto> {
            override fun onResponse(
                call: Call<UnivResponseDto>,
                response: Response<UnivResponseDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request failed", "${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        adapter = UnivListAdapter(
                            requireContext(),
                            dataResponse.universitiesPage.contents,
                            this@UnivListPageFragment
                        )
                    }
                    recyclerView.adapter = adapter

                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<UnivResponseDto>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    override fun onEditClick(univ: UnivDto) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.update_univ_dialog, null)
        val editUnivName = dialogView.findViewById<AppCompatEditText>(R.id.updUnivName)
        val editUnivCity = dialogView.findViewById<AppCompatEditText>(R.id.updUnivCity)
        val btnUpdate = dialogView.findViewById<AppCompatButton>(R.id.updateUnivBtn)

        editUnivName?.setText(univ.universityName)
        editUnivCity?.setText(univ.city)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()

//        btnUpdate.setOnClickListener {
//            val id = univ.id
//            val univName = editUnivName?.text.toString()
//            val city = editUnivCity?.text.toString()
//            update(UnivDto(id, univName, city))
//        }

        btnUpdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val id = univ.id
                val univName = editUnivName?.text.toString()
                val city = editUnivCity?.text.toString()
                update(UnivDto(id, univName, city))
            }

        })
    }

    override fun onDeleteClick(univ: UnivDto) {
        TODO("Not yet implemented")
    }


    private fun update(updatedUniversity: UnivDto) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = univApi.editUniversity(
            "Bearer ${token}",
            updatedUniversity.id.toInt(),
            updatedUniversity
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API Request okay", "${response.code()}")
                } else {
                    Log.d("API Request failed", "${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработка ошибки
            }
        })

    }

//    private fun getUniversities(recyclerView: RecyclerView, token: String?) {
//        Log.d("API Request failed", "${token}")
//        val call = univApi.getUniversities("Bearer ${token}")
//        call.enqueue(object : Callback<UnivResponseDto> {
//            override fun onResponse(
//                call: Call<UnivResponseDto>,
//                response: Response<UnivResponseDto>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()!!
//                    val data = responseBody.contents
//
//                    val adapter = UnivListAdapter(requireContext(), data)
//                    recyclerView.adapter = adapter
//                } else {
//                    // Обработка ошибки
//                }
//            }
//
//            override fun onFailure(call: Call<UnivResponseDto>, t: Throwable) {
//                Log.d("API Request failed", "${t}")
//            }
//        })
//    }

//    override fun onResume() {
//        super.onResume()
//        val token = SessionManager.getToken(requireContext())
//        call = univApi.getUniversities("Bearer ${token}")
//        call.enqueue(object : Callback<UnivResponseDto> {
//            override fun onResponse(call: Call<UnivResponseDto>, response: Response<UnivResponseDto>) {
//                if (response.isSuccessful) {
//                    adapter = UnivListAdapter(requireContext() ,response.body()!!.contents)
//                    recyclerView.adapter = adapter
//                } else {
//                    Log.d("API Request", "${response.body()}")
//                }
//            }
//
//            override fun onFailure(call: Call<UnivResponseDto>, t: Throwable) {
//                Log.d("API Request failed", "??")
//            }
//        })
//    }
//
//    override fun onPause() {
//        super.onPause()
//        call.cancel()
//    }
}



