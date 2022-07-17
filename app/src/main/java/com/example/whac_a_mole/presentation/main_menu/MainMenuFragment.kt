package com.example.whac_a_mole.presentation.main_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.whac_a_mole.R
import com.example.whac_a_mole.databinding.FragmentMainMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainMenuViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPlayerDataToUi()

        processButtonClicks()

        observeUiEffect()
    }

    private fun setPlayerDataToUi() {
        binding.tvBestScore.text = getString(R.string.best_score, viewModel.bestScore.toString())
    }

    private fun processButtonClicks() {
        binding.btnStartGame.setOnClickListener {
            viewModel.startGameButtonClicked()
        }
    }

    private fun observeUiEffect() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.menuResultEffect.collect { event ->
                when (event) {
                    MainMenuViewModel.MenuUiEffect.NavigateToGameField -> {
                        findNavController().navigate(R.id.action_main_menu_to_game_field)
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