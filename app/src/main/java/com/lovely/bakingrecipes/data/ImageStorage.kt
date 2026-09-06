package com.lovely.bakingrecipes.data

import android.content.Context
import android.net.Uri
import java.io.File

// Deletes photos we copied into the app's internal storage; ignores other URIs.
object ImageStorage {

    fun deleteIfLocal(context: Context, uriString: String?) {
        if (uriString.isNullOrBlank()) return
        val uri = Uri.parse(uriString)
        if (uri.scheme != "file") return
        val path = uri.path ?: return
        val file = File(path)
        if (file.exists() && file.parentFile == context.filesDir) {
            file.delete()
        }
    }
}
