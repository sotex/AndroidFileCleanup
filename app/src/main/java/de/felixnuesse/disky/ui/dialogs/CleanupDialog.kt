package de.felixnuesse.disky.ui.dialogs

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.felixnuesse.disky.R
import de.felixnuesse.disky.background.ScanService
import de.felixnuesse.disky.extensions.tag
import de.felixnuesse.disky.extensions.readableFileSize
import de.felixnuesse.disky.model.StorageBranch
import de.felixnuesse.disky.model.StorageLeaf
import de.felixnuesse.disky.model.StoragePrototype
import java.io.File
import java.net.URI

class CleanupDialog(
    private var mContext: Context,
    private var items: List<StoragePrototype>,
    private val onDeleteComplete: (() -> Unit)? = null
) {

    fun askDelete() {
        val totalSize = items.sumOf { it.getCalculatedSize() }
        val fileCount = items.count { it is StorageLeaf }
        val folderCount = items.count { it is StorageBranch }

        val message = buildString {
            append(mContext.getString(R.string.cleanup_confirmation_text))
            append("\n\n")
            append(mContext.getString(R.string.cleanup_file_count, fileCount))
            append("\n")
            append(mContext.getString(R.string.cleanup_folder_count, folderCount))
            append("\n")
            append(mContext.getString(R.string.cleanup_total_size, readableFileSize(totalSize)))
        }

        MaterialAlertDialogBuilder(mContext)
            .setTitle(R.string.cleanup_title)
            .setMessage(message)
            .setPositiveButton(R.string.yes_delete) { dialog, which ->
                executeDelete()
                // 用户确认删除并执行完毕后，通知调用方（例如退出选择模式）
                onDeleteComplete?.invoke()
            }
            .setNegativeButton(R.string.no_keep, null)
            .setIcon(R.drawable.icon_delete)
            .show()
    }

    private fun executeDelete() {
        var deletedCount = 0
        var failedCount = 0

        items.forEach { item ->
            try {
                // uri 是 "file:///..." 格式，必须先通过 URI 解析，否则 File 路径无效
                val file = when (item) {
                    is StorageLeaf -> File(URI.create(item.uri).path)
                    is StorageBranch -> File(item.getParentPath())
                    else -> null
                }

                file?.let {
                    if (it.exists()) {
                        // delete/deleteRecursively 有返回值，必须检查是否真的删除成功
                        val success = if (it.isDirectory) {
                            it.deleteRecursively()
                        } else {
                            it.delete()
                        }
                        if (success) {
                            deletedCount++
                        } else {
                            failedCount++
                        }
                    } else {
                        failedCount++
                    }
                }
            } catch (e: Exception) {
                Log.e(tag(), "删除失败: ${item.name}, 错误: ${e.message}")
                failedCount++
            }
        }

        sendRefreshBroadcast()

        if (failedCount > 0) {
            showResultMessage(deletedCount, failedCount)
        }
    }

    private fun sendRefreshBroadcast() {
        val resultIntent = Intent(ScanService.SCAN_REFRESH_REQUESTED)
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(resultIntent)
    }

    private fun showResultMessage(deletedCount: Int, failedCount: Int) {
        val message = if (failedCount > 0) {
            mContext.getString(R.string.cleanup_result_partial, deletedCount, failedCount)
        } else {
            mContext.getString(R.string.cleanup_result_success, deletedCount)
        }

        MaterialAlertDialogBuilder(mContext)
            .setTitle(R.string.cleanup_result_title)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}