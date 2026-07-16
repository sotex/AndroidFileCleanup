package de.felixnuesse.disky.model

import kotlinx.serialization.Serializable

@Serializable
class StorageLeaf(
    var leafname: String,
    var leafStorageType: StorageType = StorageType.GENERIC,
    var size: Long = 0,
    var fileCategory: FileCategory = FileCategory.OTHER,
    var fileExtension: String = "",
    var audioDuration: Long = 0,
    var videoDuration: Long = 0,
    var videoWidth: Int = 0,
    var videoHeight: Int = 0,
    var imageWidth: Int = 0,
    var imageHeight: Int = 0
): StoragePrototype(leafname, leafStorageType) {

    override fun getCalculatedSize(forceRecalculation: Boolean): Long {
        return size
    }
}