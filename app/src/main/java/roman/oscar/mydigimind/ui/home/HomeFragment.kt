package roman.oscar.mydigimind.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import roman.oscar.mydigimind.R
import roman.oscar.mydigimind.databinding.FragmentHomeBinding
import roman.oscar.mydigimind.ui.Task

class HomeFragment : Fragment() {

    private var adaptador: AdaptadorTareas? = null
    private var _binding: FragmentHomeBinding? = null
    private val db = FirebaseFirestore.getInstance()

    companion object {
        var tasks = ArrayList<Task>()
        var first = true
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = inflater.inflate(R.layout.fragment_home,container,false)

        adaptador = AdaptadorTareas(root.context, tasks)

        val gridView: GridView = root.findViewById(R.id.reminders)
        gridView.adapter = adaptador

        return root
    }

    fun fillTasks() {

        db.collection("actividades")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val actividad = document.getString("actividad")

                    val tiempo = document.getString("tiempo")
                    val dom = document.getBoolean("do")
                    val lu = document.getBoolean("lu")
                    val ma = document.getBoolean("ma")
                    val mi = document.getBoolean("mi")
                    val ju = document.getBoolean("ju")
                    val vi = document.getBoolean("vi")
                    val sa = document.getBoolean("sa")

                    val days = ArrayList<String>()
                    if (dom == true) days.add("Domingo")
                    if (lu == true) days.add("Lunes")
                    if (ma == true) days.add("Martes")
                    if (mi == true) days.add("Miércoles")
                    if (ju == true) days.add("Jueves")
                    if (vi == true) days.add("Viernes")
                    if (sa == true) days.add("Sábado")

                    val task = Task(actividad, days, tiempo)
                    tasks.add(task)
                }
                adaptador?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error obteniendo los registros: ", exception)
            }
    }

    override fun onResume() {
        super.onResume()
        tasks.clear()
        fillTasks()
    }

    private class AdaptadorTareas: BaseAdapter{
        var tasks = ArrayList<Task>()
        var contexto: Context?=null
        constructor(contexto: Context, tasks: ArrayList<Task>){
            this.contexto = contexto
            this.tasks = tasks
        }

        override fun getCount(): Int {
            return tasks.size
        }

        override fun getItem(p0: Int): Any {
            return tasks[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var task = tasks[p0]
            var inflador = LayoutInflater.from(contexto)
            var vista = inflador.inflate(R.layout.task_view,null)

            var tv_title: TextView = vista.findViewById(R.id.tv_title)
            var tv_time: TextView = vista.findViewById(R.id.tv_time)
            var tv_days: TextView = vista.findViewById(R.id.tv_days)

            tv_title.setText(task.title)
            tv_days.setText(task.days.toString())
            tv_time.setText(task.time)

            return vista

        }
    }

}