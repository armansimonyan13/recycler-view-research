package com.example.armansimonyan.layoutmanager

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button

const val MAX_COUNT = 3

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val recyclerView: RecyclerView = findViewById(R.id.recycler)
		val removeButton: Button = findViewById(R.id.button_remove)
		val resetButton: Button = findViewById(R.id.button_reset)
		val addButton: Button = findViewById(R.id.button_add)

		val layoutManager = LayoutManager()
		val adapter = Adapter()
		recyclerView.layoutManager = layoutManager
		recyclerView.adapter = adapter
		adapter.notifyDataSetChanged()

		val fullList = listOf(
				Color.RED,
				Color.GREEN,
				Color.BLUE,
				Color.MAGENTA
		)
		val halfList = listOf(
				Color.RED,
				Color.BLUE,
				Color.MAGENTA
		)

		removeButton.setOnClickListener({
			adapter.data = halfList
			adapter.notifyItemRemoved(1)
		})
		resetButton.setOnClickListener({
			adapter.notifyDataSetChanged()
		})
		addButton.setOnClickListener({
			adapter.data = fullList
			adapter.notifyItemInserted(1)
		})
	}

	class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
		var data = listOf(
				Color.RED,
				Color.GREEN,
				Color.BLUE,
				Color.MAGENTA
		)

		override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
			if (parent == null) throw NullPointerException()
			return ViewHolder(View(parent.context))
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
			holder?.itemView?.setBackgroundColor(data[position])
		}

		override fun getItemCount(): Int {
			return data.size
		}

		class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

		}
	}

	class LayoutManager : RecyclerView.LayoutManager() {

		override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
			return RecyclerView.LayoutParams(100, 100)
		}

		override fun supportsPredictiveItemAnimations(): Boolean {
			return true
		}

		override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
			if (recycler == null || state == null) {
				return
			}

			detachAndScrapAttachedViews(recycler)

			(0 until MAX_COUNT).map {
				val view = recycler.getViewForPosition(it)
				addView(view)
				measureChildWithMargins(view, 0, 0)
				layoutDecoratedWithMargins(view, 0, view.measuredHeight * it, view.measuredWidth, view.measuredHeight * (it + 1))
			}

			if (state.isPreLayout) {
				layoutAppearingView(state, recycler)
			} else {
				layoutDisappearingView(recycler)
			}
		}

		private fun layoutAppearingView(state: RecyclerView.State, recycler: RecyclerView.Recycler) {
			val position = MAX_COUNT
			if (state.itemCount > position) {
				val view = recycler.getViewForPosition(position)
				addView(view)
				layoutView(view, position)
			}
		}

		private fun layoutDisappearingView(recycler: RecyclerView.Recycler) {
			recycler.scrapList
					.filter { !(it.itemView.layoutParams as RecyclerView.LayoutParams).isItemRemoved }
					.map {
						val view = it.itemView
						val lp = view.layoutParams as RecyclerView.LayoutParams
						val layoutPosition = lp.viewLayoutPosition
						addDisappearingView(view)
						layoutView(view, layoutPosition)
					}
		}

		private fun layoutView(view: View, position: Int) {
			measureChildWithMargins(view, 0, 0)
			layoutDecoratedWithMargins(view, 0, view.measuredHeight * position, view.measuredWidth, view.measuredHeight * (position + 1))
		}
	}
}
