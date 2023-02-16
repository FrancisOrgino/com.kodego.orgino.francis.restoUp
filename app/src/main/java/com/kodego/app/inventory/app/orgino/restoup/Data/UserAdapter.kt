package com.kodego.app.inventory.app.orgino.restoup.Data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityLoginOptionsBinding
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityLoginOptionsHelperBinding

class UserAdapter(options: FirebaseRecyclerOptions<User>):FirebaseRecyclerAdapter<User, UserListViewHolder>(options) {
    var userList = mutableListOf<User>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ActivityLoginOptionsHelperBinding.inflate(layoutInflater, parent, false)
        return UserListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int, model: User) {
        userList.add(model)
    }

}
class UserListViewHolder(val binding: ActivityLoginOptionsHelperBinding):RecyclerView.ViewHolder(binding.root) {

}