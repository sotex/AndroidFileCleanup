package de.felixnuesse.disky.scanner

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import de.felixnuesse.disky.extensions.tag
import de.felixnuesse.disky.model.FileCategory
import java.io.File
import java.io.FileInputStream
import android.media.ExifInterface

class FileMetadataExtractor {

    private val audioExtensions = setOf(
        "mp3", "wav", "ogg", "flac", "aac", "m4a", "wma", "amr", "mid", "midi", "opus", "aiff", "alac"
    )

    private val videoExtensions = setOf(
        "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "mpeg", "mpg", "3gp", "3g2", "m4v", "f4v"
    )

    private val imageExtensions = setOf(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "heif", "raw", "tiff", "tif"
    )

    private val documentExtensions = setOf(
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf", "csv", "xml", "json",
        "md", "markdown", "odt", "ods", "odp", "pages", "numbers", "key"
    )

    private val archiveExtensions = setOf(
        "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "cab", "iso", "dmg", "apk"
    )

    fun getFileCategory(file: File): FileCategory {
        val extension = getFileExtension(file).lowercase()
        return when {
            audioExtensions.contains(extension) -> FileCategory.AUDIO
            videoExtensions.contains(extension) -> FileCategory.VIDEO
            imageExtensions.contains(extension) -> FileCategory.IMAGE
            documentExtensions.contains(extension) -> FileCategory.DOCUMENT
            extension == "apk" -> FileCategory.APK
            archiveExtensions.contains(extension) -> FileCategory.ARCHIVE
            else -> FileCategory.OTHER
        }
    }

    fun getFileExtension(file: File): String {
        val name = file.name
        val lastDotIndex = name.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < name.length - 1) {
            name.substring(lastDotIndex + 1)
        } else {
            ""
        }
    }

    fun extractAudioMetadata(file: File): AudioMetadata {
        var duration = 0L
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = durationStr?.toLongOrNull() ?: 0L
            retriever.release()
        } catch (e: Exception) {
            Log.e(tag(), "Failed to extract audio metadata for ${file.name}: ${e.message}")
        }
        return AudioMetadata(duration)
    }

    fun extractVideoMetadata(file: File): VideoMetadata {
        var duration = 0L
        var width = 0
        var height = 0
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = durationStr?.toLongOrNull() ?: 0L
            
            val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            width = widthStr?.toIntOrNull() ?: 0
            
            val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            height = heightStr?.toIntOrNull() ?: 0
            
            retriever.release()
        } catch (e: Exception) {
            Log.e(tag(), "Failed to extract video metadata for ${file.name}: ${e.message}")
        }
        return VideoMetadata(duration, width, height)
    }

    fun extractImageMetadata(file: File): ImageMetadata {
        var width = 0
        var height = 0
        try {
            val inputStream = FileInputStream(file)
            val exif = ExifInterface(inputStream)
            
            val widthStr = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
            width = widthStr?.toIntOrNull() ?: 0
            
            val heightStr = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
            height = heightStr?.toIntOrNull() ?: 0
            
            inputStream.close()
        } catch (e: Exception) {
            Log.e(tag(), "Failed to extract image metadata for ${file.name}: ${e.message}")
        }
        return ImageMetadata(width, height)
    }

    data class AudioMetadata(
        val duration: Long
    )

    data class VideoMetadata(
        val duration: Long,
        val width: Int,
        val height: Int
    )

    data class ImageMetadata(
        val width: Int,
        val height: Int
    )
}