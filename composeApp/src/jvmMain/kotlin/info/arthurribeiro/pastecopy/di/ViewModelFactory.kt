package info.arthurribeiro.pastecopy.di

import info.arthurribeiro.pastecopy.data.monitor.ClipboardMonitorImpl
import info.arthurribeiro.pastecopy.data.repository.ClipboardRepositoryImpl
import info.arthurribeiro.pastecopy.domain.service.ClipboardService
import info.arthurribeiro.pastecopy.ui.viewmodel.ClipboardViewModel

/**
 * Factory para criar ViewModels com dependências
 */
fun createClipboardViewModel(): ClipboardViewModel {
    val repository = ClipboardRepositoryImpl()
    val monitor = ClipboardMonitorImpl()
    val service = ClipboardService(monitor, repository)

    return ClipboardViewModel(service)
}
