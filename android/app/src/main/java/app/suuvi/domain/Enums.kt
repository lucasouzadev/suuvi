package app.suuvi.domain

enum class PieceTier {
    PIN, MAGNET, COIN;

    companion object { val Default = COIN }
}

enum class SouvenirStatus { DRAFT, READY, FAILED }
