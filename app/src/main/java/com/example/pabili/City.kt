data class City(
    val name: String? = null,
    val state: String? = null,
    val country: String? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isCapital: Boolean? = null,
    val population: Long? = null,
    val regions: List<String>? = null
)
