package com.example.KLSDinfo.Fragments.DialogFragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.example.KLSDinfo.Adapters.TableFourAdapter
import com.example.KLSDinfo.Adapters.TableThreeAdapter
import com.example.KLSDinfo.Models.*
import com.example.KLSDinfo.R
import com.example.KLSDinfo.Volley.VolleySingleton
import kotlinx.android.synthetic.main.table_four_card_child.view.*
import kotlinx.android.synthetic.main.table_one_layout.view.*
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.text.NumberFormat
import java.util.ArrayList


class TableFourDialog : Fragment() {

    private lateinit var dateStr: String
    private lateinit var dateStr2: String
    val TAG: String = "FullScreenDialog"
    lateinit var map : MutableMap<String, Long>
    lateinit var id: String
    lateinit var url: String
    lateinit var linear: LinearLayout
    lateinit var parentLinear: LinearLayout
    private lateinit var queue: RequestQueue
    lateinit var progress: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: TableFourAdapter
    lateinit var dividerItemDecoration: DividerItemDecoration



    companion object {
        fun newInstance(): TableFourDialog {
            return TableFourDialog()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.table_four_layout, container, false)
        val linearLayoutManager = LinearLayoutManager(context)

        recyclerView = view.findViewById(R.id.tableFourRV)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        dividerItemDecoration = DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        queue = VolleySingleton.getInstance(context).requestQueue

        val bundle: Bundle? = arguments

        if (bundle == null || bundle.isEmpty){
            // Todo: Não há resultados

        }else {
            //todo: tratar os unix para o caso de vir null
            val persons: List<Person2>? = bundle.getParcelableArrayList("resources")
            val unix: Long = bundle.getLong("date")
            val unixPast: Long = bundle.getLong("date2")

            try{
                dateStr = bundle.getString("dateStr")!!
                dateStr2 = bundle.getString("dateStr2")!!

            }catch (e:Exception){}


            if (persons == null){
                // Todo: tratar isso dai
            }else{
                Log.i("recebido", persons.toString())
                Log.i("recebido", "${bundle.getLong("date")} and ${bundle.getLong("date2")}")
                id = ""
                map = mutableMapOf()

                for (person in persons){
                    id += "${person.holder.id}/"
                    map[person.shortName] = 0
                }
                Log.i("recebido", id)

                url = "http://smartlab.lsdi.ufma.br/service/persons/${id}physical_spaces/${unixPast+10800}/${unix+10800}"

                progress = AlertDialog.Builder(context)
                progress.setCancelable(false)
                progress.setView(R.layout.loading_dialog_layout)
                alertDialog = progress.create()
                alertDialog.show()
                makeRequest(url)


            }
        }



        return view
    }



    private fun makeRequest(url: String) {
        val stringRequest = VolleyUTF8EncodingStringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Log.i("Response", response)
                val lista: List<TableFourResource> = FakeRequest().getTableFourData(response)
                if(lista.isNotEmpty()){


                    //Todo: esse trecho está funcionando, porem não da melhor forma possivel
                    Log.i("recebido", lista.toString())
                    val map : MutableMap<String, Long> = mutableMapOf()
                    val childMap: MutableMap<String, MutableList<TableFourResource>> = mutableMapOf()
                    val childAux: MutableMap<String, MutableMap<String, Long>> = mutableMapOf()

                    for (resource in lista){
                        if(!map.containsKey(resource.shortName)){
                            map[resource.shortName] = resource.getDuration()
                        }else{
                            map[resource.shortName] = resource.getDuration() + map[resource.shortName]!!
                        }
                        if(!childMap.containsKey(resource.physical_space)){
                            childMap[resource.physical_space] = mutableListOf(resource)
                        }else{
                            val aux : MutableList<TableFourResource> = childMap[resource.physical_space]!!
                            aux.add(resource)
                            childMap[resource.physical_space] = aux
                        }
                    }

                    for (entry in childMap){

                        if(!childAux.containsKey(entry.key)){
                            childAux[entry.key] = mutableMapOf()
                        }else{

                        }

                        val childDuration: MutableMap<String, Long> = mutableMapOf()
                        for (resource in entry.value){
                            if(!childDuration.containsKey(resource.shortName)){
                                childDuration[resource.shortName] = resource.getDuration()
                            }else{
                                childDuration[resource.shortName] = resource.getDuration() + childDuration[resource.shortName]!!

                            }
                        }
                       childAux[entry.key] = childDuration
                    }

//                    val countMap: MutableMap<String, Int> = mutableMapOf()
//
//                    for (entry in childAux){
//                        for (element in entry.value){
//
//                            if(!countMap.containsKey(element.key)){
//                                countMap[element.key] = 1
//                            }else{
//                                countMap[element.key] = countMap[element.key]!! + 1
//                            }
//                        }
//                    }

                    Log.i("recebido4", "map $map")
                    Log.i("recebido4", "child map$childMap")
                    Log.i("recebido4", "aux map$childAux")
//                    Log.i("recebido4", "count map$countMap")


//                    val lista: MutableList<Map<String, Long>> = mutableListOf()

//                    for (element in childAux){
//                        lista.add(element.value)
//
//                    }


                    var map2: MutableMap<String, MutableList<TableFourResource>> = mutableMapOf()

                    for (element in lista){

                        if(!map2.containsKey(element.shortName)){
                            map2[element.shortName] = mutableListOf(element)
                        }
                        else{
                            val i: MutableList<TableFourResource> = map2[element.shortName]!!
                            i.add(element)
                            map2[element.shortName] = i
                        }
                    }


                    Log.i("recebido4", "map2: $map2")




                    generateParentTable(map, childAux)
                    mAdapter = TableFourAdapter(context!!, generateData(map2))
                    recyclerView.adapter = mAdapter
                    mAdapter.notifyDataSetChanged()




//                    for (element in childAux){
//                        generateTableChild(element.key, element.value)
//
//                    }



                }
                alertDialog.dismiss()



            },
            Response.ErrorListener {
                VolleyLog.e("Error: "+it.message)
                alertDialog.dismiss()

                //Todo: Tratar o caso do request falhar
            })

        // Add the request to the RequestQueue.

        stringRequest.retryPolicy = DefaultRetryPolicy(10 * 1000, 3, 1.0f)
        stringRequest.tag = this


        queue.add(stringRequest)

    }

    fun genMap2Data(map:MutableMap<String, MutableList<TableFourResource>>): MutableList<AuxResource4>{
        val lista: MutableList<AuxResource4> = mutableListOf()

        for (entry in map){
            lista.add(AuxResource4(entry.key, entry.value))
        }

        return lista

    }

    private fun generateData(childMap: MutableMap<String, MutableList<TableFourResource>>): MutableList<AuxResource4> {
        val lista: MutableList<AuxResource4> = mutableListOf()
        for (entry in childMap){
            lista.add(AuxResource4(entry.key, entry.value))
        }
        return lista
    }

    private fun generateParentTable(map: MutableMap<String, Long>, childAux: MutableMap<String, MutableMap<String, Long>>) {

        val card: CardView = view!!.findViewById(R.id.tableFourCardView)

        (card.findViewById(R.id.textView9) as TextView).text = "Person History - General Info"

        (card.findViewById(R.id.btn_detail) as Button).setOnClickListener {
            // Todo: details
            val bundle = Bundle()

            val dialog = FullscreenDialogFragment()
            dialog.arguments = bundle
            val activity: AppCompatActivity = context as AppCompatActivity // ??
            val transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            dialog.show(transaction, "FullScreenDialog")

        }

        var count: Long = 0
        for (entry in childAux){
            for(element in entry.value){
                count += element.value
            }
        }

        val table: TableLayout = card.findViewById(R.id.parent_table_layout)
        var row: TableRow = LayoutInflater.from(context).inflate(R.layout.table_four_parent_item, null) as TableRow
        (row.findViewById(R.id.table_item_name) as TextView).text = "${map.size}"
        (row.findViewById(R.id.table_item_count) as TextView).text = "${childAux.size}"
        (row.findViewById(R.id.table_item_duration) as TextView).text = "$count"
        var view: View = View(context).also {
            it.setBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1)
        }

        table.addView(row)
        table.addView(view)
        card.visibility = View.VISIBLE
    }


//    private fun generateTableChild(
//        title: String,
//        value: MutableMap<String, Long>
//    ) {
//
//        val card: CardView = LayoutInflater.from(context).inflate(R.layout.table_four_card_child, null) as CardView
////        card.setCardBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
//
//
//        (card.findViewById(R.id.child_title) as TextView).text = title
//        (card.findViewById(R.id.child_pagination_txt) as TextView).text = "${value.size} results of ${value.size} "
//
//        (card.findViewById(R.id.child_options) as ImageButton).setOnClickListener {
//            val popup = PopupMenu(context, it.child_options)
//            popup.inflate(R.menu.menu_card)
//            popup.setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.action_details -> {
//                        Toast.makeText(context,"Expand Table - $title", Toast.LENGTH_LONG).show()
//                    }
//                    R.id.action_log -> {
//                        Toast.makeText(context,"Log - $title", Toast.LENGTH_LONG).show()
//                    }
//                }
//                false
//            }
//            popup.show()
//        }
//        for(item in value){
//            val nf = NumberFormat.getInstance() // get instance
//            val table: TableLayout = card.findViewById(R.id.child_table_layout)
//            val row: TableRow = LayoutInflater.from(context).inflate(R.layout.table_four_child_item, null) as TableRow
//
//            (row.findViewById(R.id.table_item_name) as TextView).text = item.key
//
//            nf.maximumFractionDigits = 2 // set decimal places
//
//            val s = nf.format(item.value.toFloat()/3600)
//
//            (row.findViewById(R.id.table_item_duration) as TextView).text = s
//
//            val view: View = View(context).also {
//                it.setBackgroundColor(ContextCompat.getColor(context!!, R.color.grey))
//                it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2)
//
//            }
//
//            table.addView(row)
//            table.addView(view)
//        }
//        val view: View = View(context).also {
//            it.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
//            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,6)
//
//        }
//        linear.addView(view)
//        linear.addView(card)
//
//
//    }



    override fun onStop() {
        super.onStop()
        queue.cancelAll(this)
        VolleyLog.e("Error: ", "Request Cancelado")

    }




    class VolleyUTF8EncodingStringRequest(
        method: Int, url: String, private val mListener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) : Request<String>(method, url, errorListener) {

        override fun deliverResponse(response: String) {
            mListener.onResponse(response)
        }

        override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
            var parsed = ""

            val encoding = charset(HttpHeaderParser.parseCharset(response.headers))

            // TODO: colcar o return dentro do try
            return try {
                parsed = String(response.data, encoding)
                val bytes = parsed.toByteArray(encoding)
                parsed = String(bytes, charset("UTF-8"))

                Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
            } catch (e: UnsupportedEncodingException) {
                Response.error(ParseError(e))
            }
        }
    }


}