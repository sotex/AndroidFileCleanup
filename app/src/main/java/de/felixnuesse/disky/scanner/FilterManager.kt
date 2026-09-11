package de.felixnuesse.disky.scanner

import de.felixnuesse.disky.model.FileCategory
import de.felixnuesse.disky.model.StorageBranch
import de.felixnuesse.disky.model.StorageLeaf
import de.felixnuesse.disky.model.StoragePrototype

class FilterManager {

    var selectedCategories: Set<FileCategory> = emptySet()
    var minSize: Long = 0
    var maxSize: Long = Long.MAX_VALUE
    var minDuration: Long = 0
    var maxDuration: Long = Long.MAX_VALUE
    var minResolution: Int = 0

    fun filter(items: List<StoragePrototype>): List<StoragePrototype> {
        if (!hasActiveFilters()) {
            return items
        }

        return items.filter { item ->
            when (item) {
                is StorageLeaf -> matches(item)
                is StorageBranch -> hasMatchingDescendant(item)
                else -> true
            }
        }
    }

    /**
     * 递归判断文件夹（或其子文件夹）中是否存在匹配筛选条件的文件。
     * 只有包含匹配项的文件夹才会被保留显示。
     */
    private fun hasMatchingDescendant(branch: StorageBranch): Boolean {
        branch.getChildren().forEach { child ->
            when (child) {
                is StorageLeaf -> {
                    if (matches(child)) {
                        return true
                    }
                }
                is StorageBranch -> {
                    if (hasMatchingDescendant(child)) {
                        return true
                    }
                }
                else -> {}
            }
        }
        return false
    }

    /**
     * 递归计算筛选结果的总大小。
     * 只统计匹配筛选条件的文件大小；文件夹仅累加其中匹配文件的大小，
     * 而非文件夹的整体大小，保证筛选后概览栏显示的是筛选结果的大小。
     *
     * @param items 待统计的文件/文件夹列表
     * @return 筛选结果的总大小（字节）
     */
    fun calculateFilteredSize(items: List<StoragePrototype>): Long {
        var totalSize = 0L
        items.forEach { item ->
            when (item) {
                is StorageLeaf -> {
                    // 文件匹配筛选条件时累加其大小
                    if (matches(item)) {
                        totalSize += item.getCalculatedSize()
                    }
                }
                is StorageBranch -> {
                    // 文件夹包含匹配文件时，递归累加其中匹配文件的大小
                    if (hasMatchingDescendant(item)) {
                        totalSize += calculateFilteredSize(item.getChildren())
                    }
                }
                else -> {}
            }
        }
        return totalSize
    }

    fun matches(leaf: StorageLeaf): Boolean {
        if (!hasActiveFilters()) {
            return true
        }

        if (selectedCategories.isNotEmpty() && !selectedCategories.contains(leaf.fileCategory)) {
            return false
        }

        if (leaf.size < minSize || leaf.size > maxSize) {
            return false
        }

        when (leaf.fileCategory) {
            FileCategory.AUDIO -> {
                if (leaf.audioDuration < minDuration || leaf.audioDuration > maxDuration) {
                    return false
                }
            }
            FileCategory.VIDEO -> {
                if (leaf.videoDuration < minDuration || leaf.videoDuration > maxDuration) {
                    return false
                }
                val videoResolution = leaf.videoWidth * leaf.videoHeight
                if (minResolution > 0 && videoResolution < minResolution) {
                    return false
                }
            }
            FileCategory.IMAGE -> {
                val imageResolution = leaf.imageWidth * leaf.imageHeight
                if (minResolution > 0 && imageResolution < minResolution) {
                    return false
                }
            }
            else -> {}
        }

        return true
    }

    fun hasActiveFilters(): Boolean {
        return selectedCategories.isNotEmpty() ||
                minSize > 0 ||
                maxSize != Long.MAX_VALUE ||
                minDuration > 0 ||
                maxDuration != Long.MAX_VALUE ||
                minResolution > 0
    }

    fun reset() {
        selectedCategories = emptySet()
        minSize = 0
        maxSize = Long.MAX_VALUE
        minDuration = 0
        maxDuration = Long.MAX_VALUE
        minResolution = 0
    }

    fun toggleCategory(category: FileCategory) {
        selectedCategories = if (selectedCategories.contains(category)) {
            selectedCategories - category
        } else {
            selectedCategories + category
        }
    }

    fun setSizeRange(min: Long, max: Long) {
        minSize = min
        maxSize = max
    }

    fun setDurationRange(min: Long, max: Long) {
        minDuration = min
        maxDuration = max
    }

    fun applyMinResolution(resolution: Int) {
        minResolution = resolution
    }

    fun getActiveFiltersDescription(): String {
        val descriptions = mutableListOf<String>()
        
        if (selectedCategories.isNotEmpty()) {
            descriptions.add("类型: ${selectedCategories.joinToString { it.name }}")
        }
        
        if (minSize > 0 || maxSize != Long.MAX_VALUE) {
            val sizeDesc = StringBuilder("大小")
            if (minSize > 0) {
                sizeDesc.append(">=${formatFileSize(minSize)}")
            }
            if (maxSize != Long.MAX_VALUE) {
                if (minSize > 0) sizeDesc.append(" ")
                sizeDesc.append("<=${formatFileSize(maxSize)}")
            }
            descriptions.add(sizeDesc.toString())
        }
        
        if (minDuration > 0 || maxDuration != Long.MAX_VALUE) {
            val durationDesc = StringBuilder("时长")
            if (minDuration > 0) {
                durationDesc.append(">=${formatDuration(minDuration)}")
            }
            if (maxDuration != Long.MAX_VALUE) {
                if (minDuration > 0) durationDesc.append(" ")
                durationDesc.append("<=${formatDuration(maxDuration)}")
            }
            descriptions.add(durationDesc.toString())
        }
        
        if (minResolution > 0) {
            descriptions.add("分辨率>=$minResolution")
        }
        
        return if (descriptions.isEmpty()) "无筛选" else descriptions.joinToString(", ")
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.1f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }

    private fun formatDuration(milliseconds: Long): String {
        val seconds = (milliseconds / 1000).toInt()
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> String.format("%d小时%d分", hours, minutes % 60)
            minutes > 0 -> String.format("%d分钟", minutes)
            else -> String.format("%d秒", seconds)
        }
    }
}