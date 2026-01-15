package com.inavisys.navisdk.androiddemo.utils

import android.view.View
import android.view.View.OnLongClickListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.inavisys.navisdk.androiddemo.R

class ItemClickSupport private constructor(
    private val recyclerView: RecyclerView,
) {
    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null
    private val onClickListener: View.OnClickListener = View.OnClickListener { view ->
        onItemClickListener?.let {
            val holder = recyclerView.getChildViewHolder(view)
            it.onItemClicked(recyclerView, holder.adapterPosition, view)
        }
    }
    private val onLongClickListener: OnLongClickListener = object : OnLongClickListener {
        override fun onLongClick(view: View): Boolean {
            onItemLongClickListener?.let {
                val holder = recyclerView.getChildViewHolder(view)

                return it.onItemLongClicked(recyclerView, holder.adapterPosition, view)
            }

            return false
        }
    }
    private val attachListener: OnChildAttachStateChangeListener = object : OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            onItemClickListener?.let {
                view.setOnClickListener(onClickListener)
            }

            onItemLongClickListener?.let {
                view.setOnLongClickListener(onLongClickListener)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {}
    }

    init {
        this.recyclerView.setTag(R.id.item_click_support, this)
        this.recyclerView.addOnChildAttachStateChangeListener(attachListener)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?): ItemClickSupport {
        onItemClickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?): ItemClickSupport {
        onItemLongClickListener = listener
        return this
    }

    private fun detach(view: RecyclerView) {
        view.removeOnChildAttachStateChangeListener(attachListener)
        view.setTag(R.id.item_click_support, null)
    }

    interface OnItemClickListener {
        fun onItemClicked(
            recyclerView: RecyclerView?,
            position: Int,
            view: View?,
        )
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(
            recyclerView: RecyclerView?,
            position: Int,
            view: View?,
        ): Boolean
    }

    companion object {
        fun addTo(view: RecyclerView): ItemClickSupport {
            return view.getTag(R.id.item_click_support) as? ItemClickSupport ?: ItemClickSupport(view)
        }
    }
}
