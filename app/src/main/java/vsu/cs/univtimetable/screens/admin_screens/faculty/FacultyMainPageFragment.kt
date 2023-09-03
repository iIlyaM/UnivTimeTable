package vsu.cs.univtimetable.screens.admin_screens.faculty

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import vsu.cs.univtimetable.R

class FacultyMainPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faculty_main_page, container, false)

        val bundle = Bundle()
        bundle.putInt("facultyId", arguments?.getInt("facultyId") ?: -1)
        bundle.putInt("univId", arguments?.getInt("univId") ?: -1)

        val prevPageButton = view.findViewById<ImageButton>(R.id.prevPageButton)
        prevPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_facultyMainPageFragment_to_facultyListPageFragment)
        }

        val mainPageButton = view.findViewById<ImageButton>(R.id.mainPageButton)
        mainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_facultyMainPageFragment_to_adminMainPageFragment)
        }

        val audienceBtn = view.findViewById<AppCompatButton>(R.id.addAudienceBtn)
        audienceBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_facultyMainPageFragment_to_audienceListPageFragment,
                bundle
            )
        }

        val groupBtn = view.findViewById<AppCompatButton>(R.id.addGroupBtn)
        groupBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_facultyMainPageFragment_to_groupListPageFragment,
                bundle
            )
        }

        return view
    }

}