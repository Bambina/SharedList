package com.bambina.sharedlist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bambina.sharedlist.R
import com.bambina.sharedlist.SharedListApi
import com.bambina.sharedlist.SharedListApplication.Companion.application
import com.bambina.sharedlist.model.ErrandList
import com.bambina.sharedlist.model.Task
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.list_task.view.*

import javax.inject.Inject

/**
 * Created by hirono-mayuko on 2017/06/06.
 */
class TaskRecyclerAdapter(val data : ErrandList, val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_task, parent, false)
        return ViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ViewHolder).setData(data.errand.id, data.tasks[position])
    }

    override fun getItemCount(): Int = data.tasks.size

    class ViewHolder(itemView : View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        @Inject
        lateinit var api : SharedListApi

        init {
            application.component.inject(this)
        }

        fun setData(errandId: String, task: Task) {
            itemView.list_item_text.text = task.name
            val drawableId = if(task.done) R.drawable.ic_done_green_24px else R.drawable.ic_done_grey_24px
            val doneTapped = if(task.done) "0" else "1"

            itemView.done.setImageResource(drawableId)
            val hm = hashMapOf("done" to doneTapped)

            RxView.clicks(itemView).subscribe {
                updateTask(errandId, task.id, hm, doneTapped)
            }
        }

        private fun updateTask(errandId: String, taskId: String, hm: HashMap<String, String>, doneTapped: String) {
            api.updateTaskDone(errandId, taskId, hm)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                if(doneTapped.equals("0")){
                                    itemView.done.setImageResource(R.drawable.ic_done_grey_24px)
                                } else {
                                    itemView.done.setImageResource(R.drawable.ic_done_green_24px)
                                }
                            },
                            onComplete = { println("update done.") },
                            onError = { displayToast(it.toString()) }
                    )
        }

        private fun displayToast(msg : String){
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
