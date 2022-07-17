package com.example.whac_a_mole.presentation.game_field

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.whac_a_mole.databinding.ItemGameFieldBinding
import com.example.whac_a_mole.presentation.game_field.model.Cell

class GameFieldAdapter(
    private val onClickListener: () -> Unit
) : ListAdapter<Cell, GameFieldAdapter.GameFieldViewHolder>(DiffCallback) {

    class GameFieldViewHolder(
        private val binding: ItemGameFieldBinding,
        private val onClickListener: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cell: Cell) {
            if (cell == Cell.MOLE) {
                binding.ivMole.isVisible = true
                binding.root.setOnClickListener { onClickListener() }
            } else if (cell == Cell.EMPTY) {
                binding.ivMole.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameFieldViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GameFieldViewHolder(
            ItemGameFieldBinding.inflate(layoutInflater, parent, false),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: GameFieldViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Cell>() {
        override fun areItemsTheSame(oldItem: Cell, newItem: Cell): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Cell, newItem: Cell): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
}