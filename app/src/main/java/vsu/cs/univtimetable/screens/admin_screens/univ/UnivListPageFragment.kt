package vsu.cs.univtimetable.screens.admin_screens.univ

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import android.widget.SearchView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UnivApi
import vsu.cs.univtimetable.dto.univ.UnivDto
import vsu.cs.univtimetable.repository.UnivRepository
import vsu.cs.univtimetable.screens.adapter.OnUnivClickInterface
import vsu.cs.univtimetable.screens.adapter.OnUnivDeleteInterface
import vsu.cs.univtimetable.screens.adapter.OnUnivEditInterface
import vsu.cs.univtimetable.screens.adapter.UnivListAdapter
import vsu.cs.univtimetable.screens.adapter.UserListAdapter

class UnivListPageFragment : Fragment(), OnUnivEditInterface, OnUnivDeleteInterface,
    OnUnivClickInterface {

    private lateinit var univApi: UnivApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UnivListAdapter
    private lateinit var searchView: SearchView
    private lateinit var univViewModel: UnivViewModel
    private var alertDialog: AlertDialog? = null

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
        initRV(recyclerView)
        searchView = view.findViewById(R.id.enterUnivName)

        val token = SessionManager.getToken(requireContext())!!
        val univRepository = UnivRepository(univApi, token)

        univViewModel =
            ViewModelProvider(
                requireActivity(),
                UnivViewModelFactory(univRepository, token)
            )[UnivViewModel::class.java]

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
            findNavController().navigate(R.id.action_univListPageFragment_to_createUniversityFragment)
        }

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_univListPageFragment_to_adminMainPageFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        univViewModel.univList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        univViewModel.errorMsg.observe(viewLifecycleOwner) {
        }
        getUniversities(null, null)
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

    override fun onItemClick(id: Int) {
        val bundle = Bundle()
        bundle.putInt("univId", id)
        findNavController().navigate(
            R.id.action_univListPageFragment_to_facultyListPageFragment,
            bundle
        )
    }

    private fun update(id: Int, updatedUniversity: UnivDto) {
        univViewModel.editUniversity(id, updatedUniversity)
//        val token: String? = SessionManager.getToken(requireContext())
//        Log.d("API Request failed", "${token}")
//        val call = univApi.editUniversity(
//            "Bearer ${token}",
//            updatedUniversity.id.toInt(),
//            updatedUniversity
//        )
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Log.d("API Request okay", "Обновили ${response.code()}")
//                    showToastNotification("Университет успешно обновлен")
//                    val code = response.code()
//                } else {
//                    if (response.code() == 400) {
//                        showToastNotification("Такой университет уже существует")
//                    }
//                    if (response.code() == 403) {
//                        showToastNotification("Недостаточно прав доступа для выполнения")
//                    }
//                    if (response.code() == 404) {
//                        showToastNotification("Университет по переданному id не был найден")
//                    }
//                    Log.d("API Request failed", "${response.code()}")
//                    Log.d("API Request failed", "${response.body()}")
//                    Log.d("API Request failed", "${response.errorBody()}")
//                }
//                callback(response.code())
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                // Обработка ошибки
//            }
//        })
    }

    private fun delete(id: Int) {
        val token: String? = SessionManager.getToken(requireContext())
        univViewModel.deleteUniversity(id)
//        Log.d("API Request failed", "${token}")
//        val call = univApi.deleteUniversity(
//            "Bearer ${token}", id
//        )
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Log.d("API Request okay", "Удалили ${response.code()}")
//                    showToastNotification("Университет успешно удален")
//                } else {
//                    if (response.code() == 403) {
//                        showToastNotification("Недостаточно прав доступа для выполнения")
//                    }
//                    if (response.code() == 404) {
//                        showToastNotification("Университет по переданному id не был найден")
//                    }
//                    Log.d("API Request failed", "${response.code()}")
//                }
//                callback(response.code())
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                // Обработка ошибки
//            }
//        })
    }

    private fun getUniversities(param: String?, order: String?) {
        univViewModel.getUniversities(param, order)
//        val token: String? = SessionManager.getToken(requireContext())
//        Log.d("API Request failed", "${token}")
//        val call = univApi.getUniversities("Bearer ${token}", param, order)
//
//
//        call.enqueue(object : Callback<UnivResponseDto> {
//            override fun onResponse(
//                call: Call<UnivResponseDto>,
//                response: Response<UnivResponseDto>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d("API Request successful", "Получили ${response.code()}")
//                    val dataResponse = response.body()
//                    println(dataResponse)
//                    if (dataResponse != null) {
//                        adapter = UnivListAdapter(
//                            requireContext(),
//                            dataResponse.universitiesPage.contents,
//                            this@UnivListPageFragment
//                        )
//                    }
//                    recyclerView.adapter = adapter
//
//                } else {
//                    println("Не успешно")
//                }
//            }
//
//            override fun onFailure(call: Call<UnivResponseDto>, t: Throwable) {
//                println("Ошибка")
//                println(t)
//            }
//        })
    }

    private fun showToastNotification(message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    override fun onEditClick(id: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.update_univ_dialog, null)
        val editUnivName = dialogView.findViewById<AppCompatEditText>(R.id.updUnivName)
        val editUnivCity = dialogView.findViewById<AppCompatEditText>(R.id.updUnivCity)
        val btnUpdate = dialogView.findViewById<AppCompatButton>(R.id.updateUnivBtn)

        univViewModel.getUniversity(id.toLong())
        univViewModel.univ.observe(viewLifecycleOwner) {
            editUnivName?.setText(it.universityName)
            editUnivCity?.setText(it.city)
        }


        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()

        btnUpdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val univName = editUnivName?.text.toString()
                val city = editUnivCity?.text.toString()
                update(id, UnivDto(id.toLong(), univName, city))
                getUniversities(null, null)
                alertDialog.dismiss()
            }
        })
//        if (dialogView.parent != null) {
//            (dialogView.parent as ViewGroup).removeView(dialogView) // <- fix
//        }
    }

    override fun onDeleteClick(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление университета")
            .setMessage("Вы уверены что хотите удалить  из списка?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(id)
                getUniversities(null, null)
            }
            .setNegativeButton(
                "Отмена"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    private fun initRV(rv: RecyclerView) {
        adapter = UnivListAdapter(
            this@UnivListPageFragment,
            this@UnivListPageFragment,
            this@UnivListPageFragment
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
    }
}



