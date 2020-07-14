package com.datnht.android_extensions.extensions

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


@Throws(IOException::class)
fun createFile(context: Context, subDirectoryName: String? = null): File? {
    val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
    val fileName = "JPEG_$timeStamp.jpg"

    val storageDir: File? =
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    if (storageDir != null && storageDir.exists().not()) {
        storageDir.mkdirs()
    }

    if (subDirectoryName != null) {
        val file = File(storageDir, subDirectoryName)
        if (file.exists().not()) {
            file.mkdirs()
        }
        return File(file, fileName)
    }
    return File(storageDir, fileName)
}

fun loadShareableDataFromMediaStore(resolver: ContentResolver?, contentUri: Uri, id: Long) =
    resolver?.openInputStream(
        ContentUris.withAppendedId(
            contentUri,
            id
        )
    )

fun saveImageToMediaStoreForShareable(
    resolver: ContentResolver?,
    bitmap: Bitmap,
    subDirectory: String? = null
): Uri? {
    if (resolver == null) return null
    var newFile: File?
    var fos: OutputStream? = null
    var finalLocationUri: Uri? = null
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                UUID.randomUUID().toString() + ".jpg"
            )
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/MyAppForShare"
            )
            val imageUri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            fos = resolver.openOutputStream(imageUri!!)
            finalLocationUri = imageUri
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            if (subDirectory == null) {
                val image = File(imagesDir, UUID.randomUUID().toString() + ".jpg")
                fos = FileOutputStream(image)
                finalLocationUri = Uri.fromFile(image)
            } else {
                newFile = File(imagesDir, "/$subDirectory")

                if (!newFile.exists()) {
                    newFile.mkdir()
                }

                val image = File(newFile, UUID.randomUUID().toString() + ".jpg")
                finalLocationUri = Uri.fromFile(image)
                fos = FileOutputStream(image)
            }
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } catch (ex: Exception) {
        ex.printStackTrace()
    } finally {
        fos?.flush()
        fos?.close()
        return finalLocationUri
    }
}

fun queryMediaFromResolver(resolver: ContentResolver?) {
    val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
    val selection = null
    val selectionArgs = null
    val sortOrder = null

    resolver?.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
        }
    }
}

suspend fun read(context: Context, source: Uri): String = withContext(Dispatchers.IO) {
    val resolver: ContentResolver = context.contentResolver

    resolver.openInputStream(source)?.use { stream -> stream.readText() }
        ?: throw IllegalStateException("could not open $source")
}

private fun InputStream.readText(charset: Charset = Charsets.UTF_8): String =
    readBytes().toString(charset)

suspend fun write(context: Context, source: Uri, text: String) = withContext(Dispatchers.IO) {
    val resolver: ContentResolver = context.contentResolver

    resolver.openOutputStream(source)?.use { stream -> stream.writeText(text) }
        ?: throw IllegalStateException("could not open $source")
}

private fun OutputStream.writeText(
    text: String,
    charset: Charset = Charsets.UTF_8
): Unit = write(text.toByteArray(charset))