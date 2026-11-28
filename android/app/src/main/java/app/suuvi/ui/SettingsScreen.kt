package app.suuvi.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.SettingsRules
import app.suuvi.ui.components.BackBar
import app.suuvi.ui.components.SegmentedPill

/** Preferencias locais (nada sai do aparelho). */
object Prefs {
    private fun sp(c: Context) = c.getSharedPreferences("suuvi", Context.MODE_PRIVATE)
    fun name(c: Context) = sp(c).getString("profileName", "") ?: ""
    fun setName(c: Context, v: String) = sp(c).edit().putString("profileName", v).apply()
    fun tier(c: Context) = sp(c).getString("defaultTier", SettingsRules.tiers[0]) ?: SettingsRules.tiers[0]
    fun setTier(c: Context, v: String) = sp(c).edit().putString("defaultTier", v).apply()
    fun showDaily(c: Context) = sp(c).getBoolean("showDaily", true)
    fun setShowDaily(c: Context, v: Boolean) = sp(c).edit().putBoolean("showDaily", v).apply()
    /** Visual do 3D: suave (Chaikin + bevel) ou low-poly; a escolha fica guardada. */
    fun viewerSmooth(c: Context) = sp(c).getBoolean("viewerSmooth", true)
    fun setViewerSmooth(c: Context, v: Boolean) = sp(c).edit().putBoolean("viewerSmooth", v).apply()
}

/** Configuracoes: lista agrupada (perfil, preferencias, sobre). Tudo fica no aparelho. */
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var name by remember { mutableStateOf(Prefs.name(ctx)) }
    var tier by remember { mutableStateOf(Prefs.tier(ctx)) }
    var showDaily by remember { mutableStateOf(Prefs.showDaily(ctx)) }

    Column(Modifier.fillMaxSize().background(SuuviColor.canvas).windowInsetsPadding(WindowInsets.systemBars)) {
        BackBar(stringResource(R.string.settings_title), onBack, Modifier.padding(horizontal = SuuviSpace.s5))
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = SuuviSpace.s5),
            verticalArrangement = Arrangement.spacedBy(SuuviSpace.s5),
        ) {
            Group(stringResource(R.string.settings_profile)) {
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.settings_name), fontSize = 17.sp, color = SuuviColor.text)
                    Box(Modifier.weight(1f).padding(start = SuuviSpace.s3), contentAlignment = Alignment.CenterEnd) {
                        if (name.isEmpty()) {
                            Text(stringResource(R.string.settings_name_placeholder), fontSize = 17.sp, color = SuuviColor.textMuted)
                        }
                        BasicTextField(
                            value = name, onValueChange = { name = it }, singleLine = true,
                            textStyle = TextStyle(fontSize = 17.sp, color = SuuviColor.textMuted, textAlign = TextAlign.End),
                            cursorBrush = SolidColor(SuuviColor.text),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                name = SettingsRules.normalizeName(name)
                                Prefs.setName(ctx, name)
                            }),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            Group(stringResource(R.string.settings_preferences)) {
                Column(Modifier.fillMaxWidth().padding(vertical = SuuviSpace.s3), verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2)) {
                    Text(stringResource(R.string.settings_format), fontSize = 17.sp, color = SuuviColor.text)
                    SegmentedPill(
                        options = listOf(
                            stringResource(R.string.tier_coin), stringResource(R.string.tier_pin), stringResource(R.string.tier_magnet),
                        ),
                        selected = SettingsRules.tiers.indexOf(tier).coerceAtLeast(0),
                        onSelect = { tier = SettingsRules.tiers[it]; Prefs.setTier(ctx, tier) },
                    )
                }
                Divider()
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.settings_daily), Modifier.weight(1f), fontSize = 17.sp, color = SuuviColor.text)
                    Switch(
                        checked = showDaily, onCheckedChange = { showDaily = it; Prefs.setShowDaily(ctx, it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = SuuviColor.accent, checkedThumbColor = SuuviColor.bg,
                            uncheckedTrackColor = SuuviColor.border, uncheckedThumbColor = SuuviColor.bg,
                            uncheckedBorderColor = SuuviColor.border,
                        ),
                    )
                }
                Divider()
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.settings_language), Modifier.weight(1f), fontSize = 17.sp, color = SuuviColor.text)
                    Text(stringResource(R.string.settings_language_value), fontSize = 17.sp, color = SuuviColor.textMuted)
                }
            }
            Group(stringResource(R.string.settings_about)) {
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.settings_version), Modifier.weight(1f), fontSize = 17.sp, color = SuuviColor.text)
                    Text("0.1.0", fontSize = 17.sp, color = SuuviColor.textMuted)
                }
                Divider()
                Text(stringResource(R.string.settings_credits_icons), Modifier.heightIn(min = 52.dp).wrapMid(), fontSize = 17.sp, color = SuuviColor.textMuted)
                Divider()
                Text(stringResource(R.string.settings_credits_emoji), Modifier.heightIn(min = 52.dp).wrapMid(), fontSize = 17.sp, color = SuuviColor.textMuted)
            }
            Text(
                stringResource(R.string.settings_privacy), Modifier.padding(horizontal = SuuviSpace.s4),
                fontSize = 13.sp, color = SuuviColor.textMuted,
            )
        }
    }
}

private fun Modifier.wrapMid() = this.fillMaxWidth().padding(vertical = 14.dp)

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(SuuviColor.border))
}

/** Grupo: titulo pequeno em cima e cartao branco arredondado com as linhas. */
@Composable
private fun Group(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2)) {
        Text(
            title.uppercase(), Modifier.padding(horizontal = SuuviSpace.s4),
            fontSize = 13.sp, fontWeight = FontWeight.Normal, color = SuuviColor.textMuted,
        )
        Column(
            Modifier.fillMaxWidth().background(SuuviColor.bg, RoundedCornerShape(SuuviRadius.md))
                .padding(horizontal = SuuviSpace.s4),
        ) { content() }
    }
}
