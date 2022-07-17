package com.example.whac_a_mole.presentation.game_field

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.whac_a_mole.R
import com.example.whac_a_mole.databinding.FragmentGameFieldBinding
import com.example.whac_a_mole.presentation.game_field.model.GameFieldState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GameFieldFragment : Fragment() {

    private var _binding: FragmentGameFieldBinding? = null
    private val binding get() = _binding!!

    private var adapter: GameFieldAdapter? = null
    private val viewModel by viewModels<GameFieldViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFieldBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        observeUiState()
        observeUiEffect()
    }

    private fun observeUiState() {
        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            processUiState(state)
        }
    }

    private fun processUiState(state: GameFieldState) {
        binding.tvScore.text = getString(R.string.game_score, state.playerScore.toString())
        binding.tvGameTime.text = getString(R.string.time_left, state.currentTime.toString())

        adapter?.submitList(state.gameField)
        adapter?.notifyDataSetChanged()
    }

    private fun setupAdapter() {
        adapter = GameFieldAdapter { viewModel.clickedOnTheMole() }
        binding.recyclerView.adapter = adapter
    }

    private fun observeUiEffect() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.gameUiEffect.collect { event ->
                when (event) {
                    is GameFieldViewModel.GameUiEffect.NavigateToGameResult -> {
                        val action =
                            GameFieldFragmentDirections.actionGameFieldToResult(event.score)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}