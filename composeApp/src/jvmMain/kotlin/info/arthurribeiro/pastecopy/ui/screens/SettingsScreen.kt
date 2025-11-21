package info.arthurribeiro.pastecopy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import info.arthurribeiro.pastecopy.domain.model.ActivationMode
import info.arthurribeiro.pastecopy.domain.model.AppConfig
import info.arthurribeiro.pastecopy.ui.viewmodel.ClipboardViewModel

/**
 * Tela de configurações do aplicativo
 * Permite configurar modo de ativação e outras preferências
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ClipboardViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config by viewModel.appConfig.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Seção: Modo de Ativação
            ActivationModeSection(
                currentMode = config.activationMode,
                onModeChange = { newMode ->
                    viewModel.updateActivationMode(newMode)
                }
            )

            Divider()

            // Seção: Limite de Histórico
            HistoryLimitSection(
                currentLimit = config.maxHistoryItems,
                onLimitChange = { newLimit ->
                    viewModel.updateMaxHistoryItems(newLimit)
                }
            )

            Divider()

            // Seção: Quick Search
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Habilitar Busca Rápida",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Mostrar campo de busca na tela principal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = config.enableQuickSearch,
                    onCheckedChange = { enabled ->
                        viewModel.updateQuickSearchEnabled(enabled)
                    }
                )
            }
        }
    }
}

@Composable
private fun ActivationModeSection(
    currentMode: ActivationMode,
    onModeChange: (ActivationMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Modo de Ativação",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Escolha como ativar a janela do PasteCopy",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActivationMode.entries.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = currentMode == mode,
                            onClick = { onModeChange(mode) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentMode == mode,
                        onClick = null
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when (mode) {
                                ActivationMode.KEYBOARD_SHORTCUT -> "Atalho de Teclado"
                                ActivationMode.PERSISTENT_ICON -> "Ícone Persistente"
                                ActivationMode.BOTH -> "Ambos"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = when (mode) {
                                ActivationMode.KEYBOARD_SHORTCUT -> "Cmd+Shift+V (Mac) / Ctrl+Shift+V (Windows)"
                                ActivationMode.PERSISTENT_ICON -> "Ícone sempre visível no canto da tela"
                                ActivationMode.BOTH -> "Atalho de teclado + Ícone persistente"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryLimitSection(
    currentLimit: Int,
    onLimitChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showUnlimited by remember { mutableStateOf(currentLimit == Int.MAX_VALUE) }
    var customLimit by remember { mutableStateOf(if (currentLimit == Int.MAX_VALUE) "100" else currentLimit.toString()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Limite de Histórico",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Histórico Ilimitado",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Salvar todos os itens copiados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = showUnlimited,
                onCheckedChange = { unlimited ->
                    showUnlimited = unlimited
                    if (unlimited) {
                        onLimitChange(Int.MAX_VALUE)
                    } else {
                        customLimit.toIntOrNull()?.let { onLimitChange(it) }
                    }
                }
            )
        }

        if (!showUnlimited) {
            OutlinedTextField(
                value = customLimit,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.isNotEmpty()) {
                        customLimit = newValue
                        newValue.toIntOrNull()?.let { limit ->
                            if (limit > 0) {
                                onLimitChange(limit)
                            }
                        }
                    } else if (newValue.isEmpty()) {
                        customLimit = ""
                    }
                },
                label = { Text("Número máximo de itens") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Mínimo: 1 item")
                }
            )
        }
    }
}
