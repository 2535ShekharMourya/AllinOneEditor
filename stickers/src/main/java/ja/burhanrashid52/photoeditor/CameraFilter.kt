package ja.burhanrashid52.photoeditor

data class CameraFilter(
    val logo_url: String,
    val filter:List<FilterNew>,
    val logo_position:List<String>,
    val logo:LogoModel,
    val watermark:LogoModel,
    val default_frame_id :List<DefaultFrameIdX>
)
