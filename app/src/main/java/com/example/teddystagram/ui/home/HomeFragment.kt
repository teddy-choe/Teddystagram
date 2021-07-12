package com.example.teddystagram.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.teddystagram.FcmPush
import com.example.teddystagram.R
import com.example.teddystagram.databinding.FragmentHomeBinding
import com.example.teddystagram.ui.navigation.CommentActivity
import com.example.teddystagram.ui.profile.ProfileFragment
import com.example.teddystagram.util.CONTENT_UID
import com.example.teddystagram.util.DESTINATION_UID
import com.example.teddystagram.util.USER_ID
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    var uid: String? = null
    var fcmPush: FcmPush? = null
    private lateinit var adapter: HomeAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(
            inflater, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewModel
        }
        uid = FirebaseAuth.getInstance().currentUser?.uid
        //fcmPush = FcmPush()

        adapter = HomeAdapter((viewModel))
        binding.rvHome.adapter = adapter
        binding.rvHome.addItemDecoration(HomeItemDecorator(1f, 12f, Color.GRAY))
        observeLiveData()
        viewModel.getHomeData()

        return binding.root
    }

    private fun observeLiveData() {
        viewModel.homeUiData.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.navigateProfileFragment.observe(viewLifecycleOwner, {
            val fragment = ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(DESTINATION_UID, it.first)
                    putString(USER_ID, it.second)
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment).commit()
        })

        viewModel.navigateCommentActivity.observe(viewLifecycleOwner, {
            startActivity(
                Intent(activity, CommentActivity::class.java).apply {
                    putExtra(CONTENT_UID, it.first)
                    putExtra(DESTINATION_UID, it.second)
                })
        })
    }
}