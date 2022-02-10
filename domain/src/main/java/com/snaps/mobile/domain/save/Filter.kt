package com.snaps.mobile.domain.save

sealed class Filter {
    abstract val name: String
    abstract val code: String
    abstract val imageUri: String?

    data class None(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            "",
            ""
        )
    }

    data class Sharpen(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.Sharpen.internalName,
            FilterInfo.Sharpen.code
        )
    }

    data class Sephia(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.Sephia.internalName,
            FilterInfo.Sephia.code
        )
    }

    data class GrayScale(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.GrayScale.internalName,
            FilterInfo.GrayScale.code
        )
    }

    data class OldLight(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.OldLight.internalName,
            FilterInfo.OldLight.code
        )
    }

    data class Vintage(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.Vintage.internalName,
            FilterInfo.Vintage.code
        )
    }

    data class Winter(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.Winter.internalName,
            FilterInfo.Winter.code
        )
    }

    data class Warm(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.Warm.internalName,
            FilterInfo.Warm.code
        )
    }

    data class Aurora(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.Aurora.internalName,
            FilterInfo.Aurora.code
        )
    }

    data class Amerald(
        override val name: String,
        override val code: String,
        override val imageUri: String? = null
    ) : Filter() {
        constructor() : this(
            FilterInfo.Amerald.internalName,
            FilterInfo.Amerald.code
        )
    }

    companion object {
        private enum class FilterInfo(val internalName: String, val code: String) {
            Sharpen("SHARPEN", "0x00000020"),
            Sephia("SEPHIA", "0x00000001"),
            GrayScale("GRAY_SCALE", "0x00000040"),
            OldLight("OLD_LIGHT", "0x00040000"),
            Vintage("VINTAGE", "0x00000200"),
            Winter("WINTER", "0x00080000"),
            Warm("WARM", "0x00000002"),
            Aurora("AURORA", "0x00200000"),
            Amerald("AMERALD", "0x00000004"),
        }

        fun getList(): List<Filter> {
           return mutableListOf<Filter>().apply {
                enumValues<FilterInfo>().forEach {
                    add(fromCode(it.code))
                }
            }
        }

        fun fromCode(code: String?, imageUri: String? = null): Filter {
            if (code.isNullOrEmpty()) return None()
            return when (code) {
                FilterInfo.Sharpen.code -> Sharpen().copy(imageUri = imageUri)
                FilterInfo.Sephia.code -> Sephia().copy(imageUri = imageUri)
                FilterInfo.GrayScale.code -> GrayScale().copy(imageUri = imageUri)
                FilterInfo.OldLight.code -> OldLight().copy(imageUri = imageUri)
                FilterInfo.Vintage.code -> Vintage().copy(imageUri = imageUri)
                FilterInfo.Winter.code -> Winter().copy(imageUri = imageUri)
                FilterInfo.Warm.code -> Warm().copy(imageUri = imageUri)
                FilterInfo.Aurora.code -> Aurora().copy(imageUri = imageUri)
                FilterInfo.Amerald.code -> Amerald().copy(imageUri = imageUri)
                else -> None()
            }
        }
    }
}