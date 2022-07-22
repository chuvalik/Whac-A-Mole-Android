package com.example.whac_a_mole.presentation.game_field

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.whac_a_mole.databinding.FragmentGameFieldBinding
import com.example.whac_a_mole.presentation.game_field.model.Cell
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

        observeUiState()
        observeUiEffect()

        setActionListenerToGameField()

    }

    private fun setActionListenerToGameField() {
        binding.gameField.actionListener = { row, column, field ->

            when (field.getCell(row, column)) {
                Cell.MOLE -> {
                    viewModel.clickedOnTheMole()
                }
                else -> Unit
            }
        }
    }

    private fun observeUiState() {
        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            processUiState(state)
        }
    }

    private fun processUiState(state: GameFieldState) {
        binding.gameField.gameField = state.gameField
        binding.tvScore.text = state.playerScore.toString()
        binding.tvGameTime.text = state.currentTime.toString()
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