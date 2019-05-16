package com.example.KLSDinfo.MainFragments
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.KLSDinfo.Adapters.RealMethodsAdapter
import com.example.KLSDinfo.Models.Method
import com.example.KLSDinfo.R

class RealTimeFragment : Fragment(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var methodsAdapter: RealMethodsAdapter
    private var items: MutableList<Method> = mutableListOf()

    companion object {
        fun newInstance(): RealTimeFragment{
            return RealTimeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        print("onCreateView")
        val view: View = inflater.inflate(R.layout.main_real_time_layout, container, false)
        initComponents(view)


        return view
    }


    private fun addItems(){

        items.clear()
        items.add(Method("Busca por Localização","Verifica quem são as pessoas em encontro com a(s) localizações selecionadas",R.drawable.ic_location_on_black_24dp))
        items.add(Method("Busca por Pessoas","Verifica a localização atual da(s) pessoa(s) selecionadas.",R.drawable.ic_location_on_black_24dp))

    }

    private fun initComponents(view: View) {

        recyclerView = view.findViewById(R.id.recyclerView2)
        recyclerView.layoutManager = GridLayoutManager(context,1)
        recyclerView.setHasFixedSize(true)
        addItems()
        methodsAdapter = RealMethodsAdapter(context!!, items)
        recyclerView.adapter = methodsAdapter



    }




    override fun onAttach(context: Context?) {
        super.onAttach(context)
        print("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        print("onCreate")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        print("onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        print("onStart")

    }

    override fun onResume() {
        super.onResume()
        print("onResume")
    }

    override fun onPause() {
        super.onPause()
        print("onPause")
    }

    override fun onStop() {
        super.onStop()
        print("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        print("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        print("onDestroy")
    }

    private fun print(msg: String){
        Log.d("Lifecycle", "RealTimeFragment: $msg")
    }



}


