import SwiftUI

/// Configuracoes: lista agrupada nativa (perfil, preferencias, sobre). Tudo fica no aparelho.
struct SettingsView: View {
    @Environment(\.dismiss) private var dismiss
    @AppStorage("profileName") private var name = ""
    @AppStorage("defaultTier") private var tier = SettingsRules.tiers[0]
    @AppStorage("showDaily") private var showDaily = true

    private var tierIndex: Binding<Int> {
        Binding(
            get: { SettingsRules.tiers.firstIndex(of: tier) ?? 0 },
            set: { tier = SettingsRules.tiers[$0] }
        )
    }

    private func sectionTitle(_ key: LocalizedStringKey) -> some View {
        Text(key).font(.system(size: 13)).foregroundStyle(SuuviColor.textMuted)
    }

    var body: some View {
        VStack(spacing: 0) {
            BackBar(title: "settings.title", onBack: { dismiss() })
                .padding(.horizontal, SuuviSpace.s5)
            List {
                Section {
                    HStack {
                        Text("settings.name")
                        TextField("settings.name.placeholder", text: $name)
                            .multilineTextAlignment(.trailing)
                            .foregroundStyle(SuuviColor.textMuted)
                            .submitLabel(.done)
                            .onSubmit { name = SettingsRules.normalizeName(name) }
                    }
                } header: { sectionTitle("settings.profile") }
                Section {
                    VStack(alignment: .leading, spacing: SuuviSpace.s2) {
                        Text("settings.format")
                        SegmentedPill(options: ["tier.coin", "tier.pin", "tier.magnet"], selection: tierIndex)
                    }
                    .padding(.vertical, SuuviSpace.s1)
                    Toggle("settings.daily", isOn: $showDaily).tint(SuuviColor.accent)
                    HStack {
                        Text("settings.language")
                        Spacer()
                        Text("settings.language.value").foregroundStyle(SuuviColor.textMuted)
                    }
                } header: { sectionTitle("settings.preferences") }
                Section {
                    HStack {
                        Text("settings.version")
                        Spacer()
                        Text("0.1.0").foregroundStyle(SuuviColor.textMuted)
                    }
                    Text("settings.credits.icons").foregroundStyle(SuuviColor.textMuted)
                    Text("settings.credits.emoji").foregroundStyle(SuuviColor.textMuted)
                } header: {
                    sectionTitle("settings.about")
                } footer: {
                    Text("settings.privacy").font(.system(size: 13)).foregroundStyle(SuuviColor.textMuted)
                }
            }
            .listStyle(.insetGrouped)
            .scrollContentBackground(.hidden)
            .font(.system(size: 17))
            .foregroundStyle(SuuviColor.text)
        }
        .background(SuuviColor.canvas)
        .navigationBarBackButtonHidden(true)
    }
}
