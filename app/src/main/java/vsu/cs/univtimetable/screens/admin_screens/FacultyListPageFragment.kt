package vsu.cs.univtimetable.screens.admin_screens

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.FacultyApi
import vsu.cs.univtimetable.dto.FacultyDto
import vsu.cs.univtimetable.dto.FacultyResponseDto
import vsu.cs.univtimetable.screens.adapter.FacultyListAdapter
import vsu.cs.univtimetable.screens.adapter.OnFacultiesItemClickListener

class FacultyListPageFragment : Fragment(), OnFacultiesItemClickListener {

    private lateinit var facultyApi: FacultyApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FacultyListAdapter
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        facultyApi = TimetableClient.getClient().create(FacultyApi::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faculty_list_page, container, false)
        recyclerView = view.findViewById(R.id.facultyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_facultyListPageFragment_to_univListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_facultyListPageFragment_to_adminMainPageFragment)
        }

        val sortBtn = view.findViewById<ImageButton>(R.id.sortFacultyListBtn)
        var order = "ASC"
        sortBtn.setOnClickListener {
            order = if (order.equals("ASC")) {
                "DESC"
            } else {
                "ASC"
            }
            getFaculties(null, order)
        }

//        val facItem = view.findViewById<>(R.id.facultyRecyclerView)

        val addFacultyBtn = view.findViewById<AppCompatButton>(R.id.addNewFacultyBtn)
        addFacultyBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("univId", getUnivId())
            findNavController().navigate(
                R.id.action_facultyListPageFragment_to_createFacultyPageFragment,
                bundle
            )
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFaculties(null, null)
        searchView = view.findViewById(R.id.searchFacultyView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getFaculties(newText, null)
                }

                return true
            }
        })
    }

    override fun onEditClick(faculty: FacultyDto) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.update_faculty_dialog, null)
        val editFacultyName = dialogView.findViewById<AppCompatEditText>(R.id.updFacultyName)
        val btnUpdate = dialogView.findViewById<AppCompatButton>(R.id.updateFacultyBtn)

        editFacultyName?.setText(faculty.name)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()

        btnUpdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val id = faculty.id
                val name = editFacultyName?.text.toString()
                update(FacultyDto(id, name)) { code ->
                    if (code == 200) {
                        getFaculties(null, null)
                    }
                }
                alertDialog.dismiss()
            }
        })
    }

    override fun onDeleteClick(faculty: FacultyDto) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление университета")
            .setMessage("Вы уверены что хотите удалить ${faculty.name} из списка?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(faculty.id) { code ->
                    if (code == 200) {
                        getFaculties(null, null)
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

    override fun onItemClick(facultyId: Int) {
        val bundle = Bundle()
        bundle.putInt("facultyId", facultyId)
        bundle.putInt("univId", getUnivId())

        findNavController().navigate(
            R.id.action_facultyListPageFragment_to_groupListPageFragment,
            bundle
        )
    }

    private fun update(updatedFaculty: FacultyDto, callback: (Int) -> Unit) {
        val token: String? = SessionManager.getToken(requireContext())
        Log.d("API Request failed", "${token}")
        val call = facultyApi.editFaculty(
            "Bearer ${token}",
            updatedFaculty.id,
            updatedFaculty
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API Request okay", "Обновили ${response.code()}")
                    showToastNotification("Университет успешно обновлен")

                    val code = response.code()
                } else {
                    if (response.code() == 400) {
                        showToastNotification("Такой Факультет уже существует")
                    }
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Факультет по переданному id не был найден")
                    }
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
        val call = facultyApi.deleteFaculty(
            "Bearer ${token}", id
        )
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API Request okay", "Удалили ${response.code()}")
                    showToastNotification("Факультет успешно удален")
                } else {
                    Log.d("API Request failed", "${response.code()}")
                    if (response.code() == 403) {
                        showToastNotification("Недостаточно прав доступа для выполнения")
                    }
                    if (response.code() == 404) {
                        showToastNotification("Факультет по переданному id не был найден")
                    }
                }
                callback(response.code())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    private fun getFaculties(name: String?, order: String?) {
        val token: String? = SessionManager.getToken(requireContext())

        Log.d("API Request failed", "${token}")
        val universityId = getUnivId()
        val call = facultyApi.getFaculties("Bearer ${token}", universityId, name, order)


        call.enqueue(object : Callback<FacultyResponseDto> {
            override fun onResponse(
                call: Call<FacultyResponseDto>,
                response: Response<FacultyResponseDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        adapter = FacultyListAdapter(
                            requireContext(),
                            dataResponse.facultiesPage.contents,
                            this@FacultyListPageFragment
                        )
                    }
                    recyclerView.adapter = adapter
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<FacultyResponseDto>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun showToastNotification (message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    private fun getUnivId(): Int {
        val id = arguments?.getInt("univId")
        var univId: Int = 0
        if (id != null) {
            univId = id
        }
        return univId
    }
}