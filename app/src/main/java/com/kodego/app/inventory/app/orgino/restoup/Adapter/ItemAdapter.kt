package com.kodego.app.inventory.app.orgino.restoup.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kodego.app.inventory.app.orgino.restoup.Model.ItemList
import com.kodego.app.inventory.app.orgino.restoup.databinding.SubMenuBinding

class ItemAdapter(var itemList: MutableList<ItemList>): RecyclerView.Adapter<ItemAdapter.ItemListViewHolder>(){

    inner class ItemListViewHolder(var binding:SubMenuBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SubMenuBinding.inflate(layoutInflater,parent,false)
        return ItemListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        holder.binding.apply {
            tvTableName.text = itemList[position].category
            tvItemPrice.text = itemList[position].itemPrice.toString().toInt().toString()

        }
    }

    override fun getItemCount(): Int {
        return  itemList.size
    }
}
