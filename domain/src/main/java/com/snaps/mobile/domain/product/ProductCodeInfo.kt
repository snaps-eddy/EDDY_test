package com.snaps.mobile.domain.product

class ProductCodeInfo {
    private val photoBook = PhotoBook()

    enum class PhotoBookSize {
        None,
        Size6X6,
        Size8X8,
        Size10X10,
        Size5X7,
        Size8X10,
        SizeA4
    }

    private class PhotoBook {
        val classCode = "00800600"

        val size6x6Hard = "00800600130031"
        val size6x6Soft = "00800600130032"

        val size8x8Hard = "00800600130001"
        val size8x8Soft = "00800600130003"
        val size8x8Leather = "00800600130023"
        val size8x8HardLayflat = "00800600150001"
        val size8x8LeatherLayflat = "00800600150002"

        val size10x10Hard = "00800600130002"
        val size10x10Leather = "00800600130024"
        val size10x10HardLayflat = "00800600150003"
        val size10x10LeatherLayflat = "00800600150004"

        val size5x7Hard = "00800600130019"
        val size5x7Soft = "00800600130022"

        val size8x10Hard = "00800600130017"
        val size8x10Leather = "00800600130027"
        val size8x10HardLayflat = "00800600150009"
        val size8x10LeatherLayflat = "00800600150010"

        val sizeA4Hard = "00800600130007"
        val sizeA4Soft = "00800600130008"
        val sizeA4Leather = "00800600130026"
        val sizeA4HardLayflat = "00800600150007"
        val sizeA4LeatherLayflat = "00800600150008"

        fun getPhotoBookSize(code: String): PhotoBookSize {
            return when (code) {
                size6x6Hard,
                size6x6Soft -> PhotoBookSize.Size6X6

                size8x8Hard,
                size8x8Soft,
                size8x8Leather,
                size8x8HardLayflat,
                size8x8LeatherLayflat -> PhotoBookSize.Size8X8

                size10x10Hard,
                size10x10Leather,
                size10x10HardLayflat,
                size10x10LeatherLayflat -> PhotoBookSize.Size10X10

                size5x7Hard,
                size5x7Soft -> PhotoBookSize.Size5X7

                size8x10Hard,
                size8x10Leather,
                size8x10HardLayflat,
                size8x10LeatherLayflat -> PhotoBookSize.Size8X10

                sizeA4Hard,
                sizeA4Soft,
                sizeA4Leather,
                sizeA4HardLayflat,
                sizeA4LeatherLayflat -> PhotoBookSize.SizeA4

                else -> PhotoBookSize.None
            }
        }

        fun isLeatherCover(code: String): Boolean {
            return when (code) {
                size8x8Leather,
                size10x10Leather,
                size10x10LeatherLayflat,
                size8x10Leather,
                size8x10LeatherLayflat,
                sizeA4Leather,
                sizeA4LeatherLayflat -> true
                else -> false
            }
        }
    }

    fun isPhotoBook(code: String): Boolean {
        return code.startsWith(photoBook.classCode)
    }

    fun getPhotoBookSize(code: String): PhotoBookSize {
        return photoBook.getPhotoBookSize(code)
    }

    fun isLeatherCover(code: String): Boolean {
        return photoBook.isLeatherCover(code)
    }
}









