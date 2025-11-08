package ja.burhanrashid52.photoeditor

data class FilterNew(
    val name: String,
    val type: String,
    val startColor: String,
    val endColor: String,
    val midColor: String,
    val rotation: Int,
    val opacity: Int
)
