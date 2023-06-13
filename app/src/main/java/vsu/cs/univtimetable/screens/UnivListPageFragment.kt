package vsu.cs.univtimetable.screens

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UnivApi
import vsu.cs.univtimetable.dto.UnivDto
import vsu.cs.univtimetable.dto.UnivResponseDto
import vsu.cs.univtimetable.screens.adapter.OnUnivItemClickListener
import vsu.cs.univtimetable.screens.adapter.UnivListAdapter

class UnivListPageFragment : Fragment(), OnUnivItemClickListener {

    private lateinit var univApi: UnivApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UnivListAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        univApi = TimetableClient.getClient().create(UnivApi::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_univ_list_page, container, false)
        recyclerView = view.findViewById(R.id.univsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val sortBtn = view.findViewById<ImageButton>(R.id.sortButton)
        var order = "ASC"
        sortBtn.setOnClickListener {
            order = if (order.equals("ASC")) {
                "DESC"
            } else {
                "ASC"
            }
            getUniversities(null, order)
        }

        val addUnivBtn = view.findViewById<AppCompatButton>(R.id.addNewUnivBtn)
        addUnivBtn.setOnClickListener {
            findNavController().navigate(R.id.action_univListPageFragment_to_createUniversityFragment3)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUniversities(null, null)
        searchView = view.findViewById(R.id.enterUnivName)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getUniversities(newText, null)
                }

                return true
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

        btnUpdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val id = univ.id
                val univName = editUnivName?.text.toString()
                val city = editUnivCity?.text.toString()
                update(UnivDto(id, univName, city)) { code ->
                    if (code == 200) {
                        getUniversities(null, null)
                    }
                }
                alertDialog.dismiss()
            }
        })
    }

    override fun onDeleteClick(univ: UnivDto) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление университета")
            .setMessage("Вы уверены что хотите удалить ${univ.universityName} из списка?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(univ.id.toInt()) { code ->
                    if (code == 200) {
                        getUniversities(null, null)
                    }
                }
            }
            .setNegativeButton(
                "Отмена"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    override fun onItemClick(id: Int) {
        val bundle = Bundle()
        bundle.putInt("univId", id)
        findNavController().navigate(
            R.id.action_univListPageFragment_to_univMainPageFragment,
            bundle
        )
    }

    private fun update(updatedUniversity: UnivDto, callback: (Int) -> Unit) {
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
                    Log.d("API Request okay", "Обновили ${response.code()}")
                    val code = response.code()
                } else {
                    Log.d("API Request failed", "${response.code()}")
                    Log.d("API Request failed", "${response.body()}")
                    Log.d("API Request failed", "${response.errorBody()}")
                }
                callback(response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    private fun delete(id: Int, callback: (Int) -> Unit) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = univApi.deleteUniversity(
            "Bearer ${token}", id
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API Request okay", "Удалили ${response.code()}")
                } else {
                    Log.d("API Request failed", "${response.code()}")
                }
                callback(response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    private fun getUniversities(param: String?, order: String?) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = univApi.getUniversities("Bearer ${token}", param, order)


        call.enqueue(object : Callback<UnivResponseDto> {
            override fun onResponse(
                call: Call<UnivResponseDto>,
                response: Response<UnivResponseDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
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
}



