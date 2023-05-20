package ru.walkom.app.presentation.screens.excursion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.walkom.app.common.Constants.APP_ACTIVITY
import ru.walkom.app.databinding.FragmentActionExcursionBinding
import ru.walkom.app.presentation.components.fragment_alert_dialog.FragmentDialog
import java.io.File


class ActionsExcursionFragment : FragmentDialog() {

    private val args: ActionsExcursionFragmentArgs by navArgs()
    private lateinit var binding: FragmentActionExcursionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActionExcursionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteFiles.setOnClickListener {
            val folderFiles = File(APP_ACTIVITY.filesDir, args.excursionId)
            deleteFilesExcursion(folderFiles)
            findNavController().popBackStack()
        }
    }

    private fun deleteFilesExcursion(folderFiles: File) {
        if (folderFiles.isDirectory)
            for (child in folderFiles.listFiles()!!)
                deleteFilesExcursion(child)
        folderFiles.delete()
    }
}