package com.example.whac_a_mole.presentation.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.whac_a_mole.R
import com.example.whac_a_mole.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ResultViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPlayerDataToUi()
        processButtonClicks()

        observeUiEffects()
    }

    private fun setPlayerDataToUi() {
        binding.tvBestScore.text = getString(R.string.best_score, viewModel.bestScore.toString())
        binding.tvResultScore.text =
            getString(R.string.game_score, viewModel.playerScore.toString())
    }

    private fun observeUiEffects() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.resultUiEffect.collect { event ->
                when (event) {
                    ResultViewModel.ResultUiEffect.NavigateToGameField -> {
                        findNavController().navigate(R.id.action_result_to_game_field)
                    }
                    ResultViewModel.ResultUiEffect.NavigateToMainMenu -> {
                        findNavController().navigate(R.id.action_result_to_main_menu)
                    }
                }
            }
        }
    }

    private fun processButtonClicks() {
        binding.btnPlayAgain.setOnClickListener {
            viewModel.playAgainButtonPressed()
        }
        binding.btnMenu.setOnClickListener {
            viewModel.menuButtonPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}