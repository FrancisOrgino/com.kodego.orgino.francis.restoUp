package com.kodego.app.inventory.app.orgino.restoup.Adapter

import android.view.LayoutInflater
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kodego.app.inventory.app.orgino.restoup.Model.SubMenuModel
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.databinding.SubMenuBinding

class SubMenuAdapter (private val subMenuModel: List<SubMenuModel>) : RecyclerView.Adapter<SubMenuAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sub_menu,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvSubItemName.text=subMenuModel[position].subMenuTitle
        }
    }

        override fun getItemCount() = subMenuModel.size


    inner class ViewHolder(menuView: View) : RecyclerView.ViewHolder(menuView){
        val binding = SubMenuBinding.bind(menuView)

    }

}