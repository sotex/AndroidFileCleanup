package de.felixnuesse.disky.ui.dialogs

import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.felixnuesse.disky.R
import de.felixnuesse.disky.extensions.tag
import de.felixnuesse.disky.model.StoragePrototype
import java.io.File

class DeleteDialog(
    private var mContext: Context,
    private var file: File,
    private var item: StoragePrototype? = null,
    private val onDeleteComplete: (() -> Unit)? = null
) {

    fun askDelete() {
        val title = if(file.isDirectory) {
            R.string.delete_folder_title
        } else {
            R.string.delete_file_title
        }
        MaterialAlertDialogBuilder(mContext)
            .setTitle(mContext.getString(title))
            .setMessage(mContext.getString(R.string.delete_confirmation_text, file.name))
            .setPositiveButton(
                R.string.yes_delete
            ) { dialog, which ->
                try {
                    if(file.isDirectory) {
                        file.deleteRecursively()
                    } else {
                        file.delete()
                    }
                    // 删除成功后通知调用方局部刷新，不再全量重新扫描
                    onDeleteComplete?.invoke()
                } catch (e: Exception) {
                    Log.e(tag(), e.message.toString())
                }
            }
            .setNegativeButton(R.string.no_keep, null)
            .setIcon(R.drawable.icon_delete)
            .show()
    }
}
