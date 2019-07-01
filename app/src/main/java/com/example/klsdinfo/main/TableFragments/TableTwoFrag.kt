package com.example.klsdinfo.main.TableFragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.example.klsdinfo.R
import com.example.klsdinfo.Volley.VolleySingleton
import com.example.klsdinfo.data.models.FakeRequest
import com.example.klsdinfo.data.models.Person2
import com.example.klsdinfo.data.models.TableTwoResource
import com.example.klsdinfo.main.adapters.TableTwoAdapter
import java.io.UnsupportedEncodingException


class TableTwoFrag : Fragment() {

    lateinit var cardview: CardView
    lateinit var textview: TextView
    lateinit var linear: LinearLayout
    lateinit var map : MutableMap<String, MutableList<Person2>>
    lateinit var mapResults : MutableMap<String, List<TableTwoResource>>
    lateinit var listResults: MutableList<MutableMap<String, List<TableTwoResource>>>
    lateinit var url: String
    private lateinit var queue: RequestQueue
    private lateinit var parentMap: MutableMap<String,List<TableTwoResource>>
    lateinit var progress: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog

    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: TableTwoAdapter
    lateinit var dividerItemDecoration: DividerItemDecoration
    lateinit var noResults: TextView

    companion object {
        fun newInstance(): TableTwoFrag {
            return TableTwoFrag()
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.table_two_layout, container, false)
        noResults = view.findViewById(R.id.no_result)

        recyclerView = view.findViewById(R.id.rv_resource_2)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.setHasFixedSize(true)
        parentMap = mutableMapOf()
        mapResults = mutableMapOf()
        val bundle: Bundle? = arguments
        queue = VolleySingleton.getInstance(context).requestQueue

        if (bundle == null || bundle.isEmpty){
            // Todo: Não há resultados

        }else{
            val persons: List<Person2>? = bundle.getParcelableArrayList("resources")
            if (persons == null){
                // Todo: tratar isso dai
            }else{
                Log.i("Response", "lista $persons")

                // classificar por role
                map = mutableMapOf()

                for(person in persons){
                    if(person.roles != null){
                        for(role in person.roles){
                            if(!map.containsKey(role.name)){
                                map[role.name] = mutableListOf(person)
                            }else{
                                val aux = map[role.name]
                                aux!!.add(person)
                                map[role.name] = aux
                            }
                        }
                    }
                }

                Log.i("Response", "map $map")


                for (entry in map){
                    var id = "/"
                    for(person in entry.value){
                        id+="${person.holder.id}/"
                    }
                    url = "http://smartlab.lsdi.ufma.br/service/persons/${id}physical_spaces/"

                    makeRequest(url, entry.key)
                }
                Log.i("Response", "map $mapResults")
            }
        }

//        alertDialog.dismiss()

        return view
    }

    // Todo: eliminar a necessidade de varios requests
    private fun makeRequest(url: String, role: String) {
        val stringRequest = VolleyUTF8EncodingStringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                val r: List<TableTwoResource> = FakeRequest()
                    .getTableTwoData(response)
                if (r.isNotEmpty()) {
                    noResults.visibility = View.GONE

                    mapResults[role] = r
                    mAdapter = TableTwoAdapter(context!!, mapResults)
                    recyclerView.adapter = mAdapter
                    mAdapter.notifyDataSetChanged()
                }else{
                    noResults.visibility = View.VISIBLE

                }

            },
            Response.ErrorListener {
                VolleyLog.e("Error: ", it.message)
                noResults.text = it.message
                noResults.visibility = View.VISIBLE


            })
        stringRequest.retryPolicy = DefaultRetryPolicy(20 * 1000, 3, 1.0f)
        stringRequest.tag = this
        queue.add(stringRequest)
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