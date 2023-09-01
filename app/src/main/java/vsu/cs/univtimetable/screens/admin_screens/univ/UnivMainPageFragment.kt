package vsu.cs.univtimetable.screens.admin_screens.univ

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import vsu.cs.univtimetable.R

class UnivMainPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_univ_main_page, container, false)
        val facultyBtn = view.findViewById<AppCompatButton>(R.id.facultiesButton)


        facultyBtn.setOnClickListener {
            val univId = arguments?.getInt("univId")
            val univMainBundle = Bundle()

            if (univId != null) {
                univMainBundle.putInt("univId", univId)
//                findNavController().navigate(
//                    R.id.action_univMainPageFragment_to_facultyListPageFragment,
//                    univMainBundle
//                )
            }
        }
        return view
    }
}