package com.example.KLSDinfo.Adapters

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.KLSDinfo.Fragments.DialogFragments.FullscreenDialogFragment
import com.example.KLSDinfo.Fragments.DialogFragments.TableThreeFrag
import com.example.KLSDinfo.Models.AuxResource3
import com.example.KLSDinfo.Models.Method
import com.example.KLSDinfo.R
import com.example.KLSDinfo.RealTime.RSelectionLocationFragment
import com.example.KLSDinfo.RealTime.RSelectionPersonFragment
import kotlinx.android.synthetic.main.table_three_layout.view.*
import kotlinx.android.synthetic.main.table_three_rv_item.view.*
import java.text.NumberFormat
import java.util.ArrayList

class TableThreeAdapter(
    private val context: Context,
    private val items: MutableList<AuxResource3>) : RecyclerView.Adapter<TableThreeAdapter.ResourceThreeViewHolder>()

{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceThreeViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.table_three_rv_item, parent, false)
        return ResourceThreeViewHolder(itemView)

    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ResourceThreeViewHolder, position: Int) {
        val src: AuxResource3 = items[position]

        var count: Long = 0
        for (element in src.resources){
            count += element.getDuration()
        }
        val nf = NumberFormat.getInstance() // get instance
        nf.maximumFractionDigits = 2 // set decimal places
        val s: String = nf.format(count.toFloat() / 3600)


        holder.nameTV.text = src.nome
        holder.numberRendzTV.text = ("""Nº de encontros: """ + src.resources.size).trimIndent()
        holder.durationTV.text = ("Tempo passado com o grupo: $s (h)")


        holder.optionsIB.setOnClickListener {
            val popup = PopupMenu(context, it.main_options)
            popup.inflate(R.menu.menu_card)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_details -> {
                        val bundle = Bundle()
                        bundle.putString("name", src.nome)
                        bundle.putParcelableArrayList("resources", src.resources as ArrayList<out Parcelable>) // ??
                        val dialog = FullscreenDialogFragment()
                        dialog.arguments = bundle
                        val activity: AppCompatActivity = context as AppCompatActivity // ??
                        val transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
                        dialog.show(transaction, "FullScreenDialog")

                    }
                    R.id.action_log -> {
                        val bundle = Bundle()
                        bundle.putString("name", src.nome)
                        bundle.putParcelableArrayList("resources", src.resources as ArrayList<out Parcelable>) // ??
                        val dialog = FullscreenDialogFragment()
                        dialog.arguments = bundle
                        val activity: AppCompatActivity = context as AppCompatActivity // ??
                        val transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
                        dialog.show(transaction, "FullScreenDialog")
                    }
                }
                false
            }
            popup.show()
        }



    }




    class ResourceThreeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val nameTV: TextView = itemView.findViewById(R.id.main_title)
        val numberRendzTV: TextView = itemView.findViewById(R.id.main_rating)
        val durationTV: TextView = itemView.findViewById(R.id.main_duration)
        val optionsIB: ImageButton = itemView.findViewById(R.id.main_options)
    }








}

