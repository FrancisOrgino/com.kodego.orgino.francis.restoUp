package com.kodego.app.inventory.app.orgino.restoup.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kodego.app.inventory.app.orgino.restoup.Model.MainModel
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.databinding.MainMenuBinding

class MainAdapter(private val collections : List<MainModel>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_menu,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val collection = collections[position]
            tvMenuList.text=collection.menuTitle
            val subMenuAdapter = SubMenuAdapter(collection.subMenuModel)
            rvSubmenu.adapter=subMenuAdapter
            tvMenuList.setOnClickListener{
                rvSubmenu.visibility = if (rvSubmenu.isShown) View.GONE else View.VISIBLE
            }
        }
    }

    override fun getItemCount() = collections.size


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val binding = MainMenuBinding.bind(itemView)

    }

}