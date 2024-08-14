package com.example.composeapp

enum class DialogData(
    val invoke: String,
    val title: String,
    val message: String,
    val positive: String,
    val negative: String
) {
    COUNT_UP(
        "カウントアップ",
        "確認してください",
        "カウントアップしますか？",
        "OK",
        "CANCEL"
    ),
    COUNT_DOWN(
        "カウントダウン",
        "確認してください",
        "カウントダウンしますか？",
        "OK",
        "CANCEL"
    )
}
