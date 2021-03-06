package com.example.KLSDinfo.Historic.TableFragments

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.example.KLSDinfo.CustomTable.CustomTableDialog
import com.example.KLSDinfo.Historic.adapters.TableFiveAdapter
import com.example.KLSDinfo.Models.*
import com.example.KLSDinfo.R
import com.example.KLSDinfo.Volley.VolleySingleton
import java.io.UnsupportedEncodingException
import java.lang.Exception


class TableFiveFrag : Fragment() {

    private lateinit var dateStr: String
    private lateinit var dateStr2: String
    val TAG: String = "FullScreenDialog"
    lateinit var map : MutableMap<String, Long>
    lateinit var id: String
    lateinit var url: String
    private lateinit var queue: RequestQueue
    lateinit var progress: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: TableFiveAdapter
    lateinit var dividerItemDecoration: DividerItemDecoration



    companion object {
        fun newInstance(): TableFiveFrag {
            return TableFiveFrag()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.table_five_layout, container, false)
        val linearLayoutManager = LinearLayoutManager(context)

        recyclerView = view.findViewById(R.id.tableFourRV)
        recyclerView.layoutManager = GridLayoutManager(context,2)
        recyclerView.setHasFixedSize(true)
//        dividerItemDecoration = DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
//        recyclerView.addItemDecoration(dividerItemDecoration)

        queue = VolleySingleton.getInstance(context).requestQueue

        val bundle: Bundle? = arguments

        if (bundle == null || bundle.isEmpty){
            // Todo: Não há resultados

        }else {
            //todo: tratar os unix para o caso de vir null
            val places: List<PhysicalSpace>? = bundle.getParcelableArrayList("resources")
            val unix: Long = bundle.getLong("date")
            val unixPast: Long = bundle.getLong("date2")

            try{
                dateStr = bundle.getString("dateStr")!!
                dateStr2 = bundle.getString("dateStr2")!!

            }catch (e:Exception){}


            if (places == null || places.isEmpty()){
                // Todo: tratar isso dai
            }else{
                Log.i("recebido5", places.toString())
                Log.i("recebido5", "${bundle.getLong("date")} and ${bundle.getLong("date2")}")
                id = ""
                map = mutableMapOf()

//                for (place in places){
                id += "${places[0].holder.id}"
//                    map[place.name] = 0
//                }
                Log.i("recebido", id)

                url = "http://smartlab.lsdi.ufma.br/service/physical_spaces/$id/persons/${unixPast+10800}/${unix+10800}"

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
            Response.Listener { response ->
                // Display the first 500 characters of the response string.

                val lista: List<TableFiveResource> = FakeRequest().getTableFiveData(response)
                if (lista.isNotEmpty()) {


                    //Todo: esse trecho está funcionando, porem não da melhor forma possivel
                    val map: MutableMap<String, Long> = mutableMapOf()
                    val countPerson: MutableMap<String, Long> = mutableMapOf()
                    var countDuration: Long = 0
                    val childMap: MutableMap<String, MutableList<TableFiveResource>> = mutableMapOf()
                    val childAux: MutableMap<String, MutableMap<String, Long>> = mutableMapOf()

//                    generateParentTable(map, lista)



                    for (resource in lista) {
                        if(!map.containsKey(resource.physical_space)){
                            map[resource.physical_space] = resource.getDuration()
                        }else{
                            map[resource.physical_space] = resource.getDuration() + map[resource.physical_space]!!
                        }

                        if (!childMap.containsKey(resource.shortName)) {
                            childMap[resource.shortName] = mutableListOf(resource)
                        } else {
                            val aux: MutableList<TableFiveResource> = childMap[resource.shortName]!!
                            aux.add(resource)
                            childMap[resource.shortName] = aux
                        }
                    }

                    for (entry in childMap) {

                        if (!childAux.containsKey(entry.key)) {
                            childAux[entry.key] = mutableMapOf()
                        } else {

                        }

                        val childDuration: MutableMap<String, Long> = mutableMapOf()
                        for (resource in entry.value) {
                            if (!childDuration.containsKey(resource.physical_space)) {
                                childDuration[resource.physical_space] = resource.getDuration()
                            } else {
                                childDuration[resource.physical_space] =
                                    resource.getDuration() + childDuration[resource.physical_space]!!

                            }
                        }
                        childAux[entry.key] = childDuration
                    }

                    val countMap: MutableMap<String, Int> = mutableMapOf()

                    for (entry in childAux){
                        for (element in entry.value){

                            if(!countMap.containsKey(element.key)){
                                countMap[element.key] = 1
                            }else{
                                countMap[element.key] = countMap[element.key]!! + 1
                            }
                        }
                    }

                    Log.i("recebido4", "map $map")
                    Log.i("recebido4", "child map$childMap")
                    Log.i("recebido4", "aux map$childAux")
                    Log.i("recebido4", "count map$countMap")


//                    val lista: MutableList<Map<String, Long>> = mutableListOf()

//                    for (element in childAux){
//                        lista.add(element.value)
//
//                    }


                    val map2: MutableMap<String, MutableList<TableFiveResource>> = mutableMapOf()

                    for (element in lista) {

                        if (!map2.containsKey(element.physical_space)) {
                            map2[element.physical_space] = mutableListOf(element)
                        } else {
                            val i: MutableList<TableFiveResource> = map2[element.physical_space]!!
                            i.add(element)
                            map2[element.physical_space] = i
                        }
                    }


                    Log.i("recebido4", "map2: $map2")




                    generateParentTable(map, childAux, lista, countMap)
                    mAdapter = TableFiveAdapter(context!!, generateData(map2))
                    recyclerView.adapter = mAdapter
                    mAdapter.notifyDataSetChanged()



                    Log.i("recebido5", lista.toString())


                }
                alertDialog.dismiss()


            },
            Response.ErrorListener {
                VolleyLog.e("Error: " + it.message)
                alertDialog.dismiss()

                //Todo: Tratar o caso do request falhar
            })

        // Add the request to the RequestQueue.

        stringRequest.retryPolicy = DefaultRetryPolicy(10 * 1000, 3, 1.0f)
        stringRequest.tag = this


        queue.add(stringRequest)

    }



    private fun generateData(childMap: MutableMap<String, MutableList<TableFiveResource>>): MutableList<AuxResource5> {
        val lista: MutableList<AuxResource5> = mutableListOf()
        for (entry in childMap){
            lista.add(AuxResource5(entry.key, entry.value))
        }
        return lista
    }

    // Todo: esse trecho nao é null safe. Corrigir
    private fun generateParentTable(
        map: MutableMap<String, Long>,
        childAux: MutableMap<String, MutableMap<String, Long>>,
        lista: List<TableFiveResource>,
        countMap: MutableMap<String, Int>

    ) {

        val x: MutableList<Table4Aux> = mutableListOf()

        for (element in map){

            x.add(Table4Aux(element.key, countMap[element.key]!!, map[element.key]!!))
        }


        val card: CardView = view!!.findViewById(R.id.tableFourCardView)

        (card.findViewById(R.id.btn_detail) as Button).setOnClickListener {
            // Todo: details
            val bundle = Bundle()
            var ref ="detail5"
            bundle.putString("ref", ref)
            bundle.putParcelableArrayList("resources", x as ArrayList<out Parcelable>) // ??
            val dialog = CustomTableDialog()
            dialog.arguments = bundle
            val activity: AppCompatActivity = context as AppCompatActivity // ??
            val transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            dialog.show(transaction, "FullScreenDialog")

        }

        (card.findViewById(R.id.btn_log) as Button).setOnClickListener {

            val bundle = Bundle()
            var ref ="log5"
            bundle.putString("ref", ref)
            bundle.putParcelableArrayList("resources", lista as ArrayList<out Parcelable>) // ??
            val dialog = CustomTableDialog()
            dialog.arguments = bundle
            val activity: AppCompatActivity = context as AppCompatActivity // ??
            val transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
            dialog.show(transaction, "FullScreenDialog")

        }




        var count: Long = 0
        for (entry in map){
            count += entry.value
        }


        (card.findViewById(R.id.nameTV4) as TextView).text = ("Physical Space History")
        (card.findViewById(R.id.descriptionTV4) as TextView).text =("Physical Spaces Found: ${map.size}")
        (card.findViewById(R.id.nplacesTV4) as TextView).text = ("Total Time Elapsed: ${count/60}m")
        card.visibility = View.VISIBLE
    }




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
            var parsed: String

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