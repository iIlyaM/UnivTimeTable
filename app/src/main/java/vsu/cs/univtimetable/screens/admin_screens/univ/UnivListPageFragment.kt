package vsu.cs.univtimetable.screens.admin_screens.univ

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.univtimetable.R
import vsu.cs.univtimetable.utils.token_utils.SessionManager
import vsu.cs.univtimetable.TimetableClient
import vsu.cs.univtimetable.api.UnivApi
import vsu.cs.univtimetable.dto.univ.UnivDto
import vsu.cs.univtimetable.repository.UnivRepository
import vsu.cs.univtimetable.screens.adapter.OnUnivClickInterface
import vsu.cs.univtimetable.screens.adapter.OnUnivDeleteInterface
import vsu.cs.univtimetable.screens.adapter.OnUnivEditInterface
import vsu.cs.univtimetable.screens.adapter.UnivListAdapter
import vsu.cs.univtimetable.utils.NotificationManager
import vsu.cs.univtimetable.utils.NotificationManager.setLoadingDialog
import vsu.cs.univtimetable.utils.Status

class UnivListPageFragment : Fragment(), OnUnivEditInterface, OnUnivDeleteInterface,
    OnUnivClickInterface {

    private lateinit var univApi: UnivApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UnivListAdapter
    private lateinit var searchView: SearchView
    private lateinit var univViewModel: UnivViewModel
    private lateinit var pDialog: ProgressDialog
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
        pDialog = ProgressDialog(context)
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

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_univListPageFragment_to_adminMainPageFragment)
        }

        val addUnivBtn = view.findViewById<ImageButton>(R.id.addNewUnivBtn)
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
        NotificationManager.setLoadingDialog(pDialog)
        getUniversities(null, null)
        pDialog.dismiss()
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
        univViewModel.editUniversity(id, updatedUniversity).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        getUniversities(null, null)
                        pDialog.dismiss()
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                        NotificationManager.showToastNotification(
                            requireContext(),
                            it.message.toString()
                        )
                    }
                    Status.LOADING -> {
                        NotificationManager.setLoadingDialog(pDialog)
                    }
                }
            }
        }
    }

    private fun delete(id: Int) {
        univViewModel.deleteUniversity(id).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        getUniversities(null, null)
                        pDialog.dismiss()
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                        NotificationManager.showToastNotification(
                            requireContext(),
                            it.message.toString()
                        )
                    }
                    Status.LOADING -> {
                        NotificationManager.setLoadingDialog(pDialog)
                    }
                }
            }
        }
    }

    private fun getUniversities(param: String?, order: String?) {
        univViewModel.getUniversities(param, order).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        if(param== null && order == null) {
                            if(it.data!!.isEmpty()) {
                                NotificationManager.showToastNotification(
                                    requireContext(),
                                    "Университеты не добавлены"
                                )
                            }
                            pDialog.dismiss()
                        }
                    }
                    Status.ERROR -> {
                        if(param== null && order == null) {
                            pDialog.dismiss()
                        }
                        pDialog.dismiss()
                        NotificationManager.showToastNotification(
                            requireContext(),
                            it.message.toString()
                        )
                    }
                    Status.LOADING -> {
                        if(param== null && order == null) {
                            setLoadingDialog(pDialog)
                        }
                    }
                }
            }
        }
    }

    override fun onEditClick(id: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.update_univ_dialog, null)
        val editUnivName = dialogView.findViewById<AppCompatEditText>(R.id.updUnivName)
        val editUnivCity = dialogView.findViewById<AppCompatEditText>(R.id.updUnivCity)
        val btnUpdate = dialogView.findViewById<AppCompatButton>(R.id.updateUnivBtn)

        univViewModel.getUniversity(id.toLong()).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        pDialog.dismiss()
                        editUnivName?.setText(it.data!!.universityName)
                        editUnivCity?.setText(it.data!!.city)
                    }
                    Status.ERROR -> {
                        pDialog.dismiss()
                        NotificationManager.showToastNotification(
                            requireContext(),
                            it.message.toString()
                        )
                    }
                    Status.LOADING -> {
                        NotificationManager.setLoadingDialog(pDialog)
                    }
                }
            }
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
                alertDialog.dismiss()
            }
        })
    }

    override fun onDeleteClick(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Удаление университета")
            .setMessage("Вы уверены что хотите удалить  из списка?")
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                delete(id)
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



