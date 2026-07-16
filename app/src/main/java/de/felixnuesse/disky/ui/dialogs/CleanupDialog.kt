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

class CleanupDialog(private var mContext: Context, private var items: List<StoragePrototype>) {

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
                val file = when (item) {
                    is StorageLeaf -> File(item.uri)
                    is StorageBranch -> File(item.getParentPath())
                    else -> null
                }

                file?.let {
                    if (it.exists()) {
                        if (it.isDirectory) {
                            it.deleteRecursively()
                        } else {
                            it.delete()
                        }
                        deletedCount++
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