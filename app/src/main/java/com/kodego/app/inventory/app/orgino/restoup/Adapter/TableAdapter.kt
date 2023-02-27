package com.kodego.app.inventory.app.orgino.restoup.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseError
import com.kodego.app.inventory.app.orgino.restoup.Model.TableModel
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.databinding.TableNameBinding

class TableAdapter(private val tableModel:List<TableModel>):RecyclerView.Adapter<TableAdapter.TableViewHolder>(){

    inner class TableViewHolder(var binding: TableNameBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TableNameBinding.inflate(layoutInflater,parent,false)
            return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.binding.apply {
            tvTableName.text =tableModel[position].tableName
        }
    }

    override fun getItemCount(): Int {
        return tableModel.size
    }
}