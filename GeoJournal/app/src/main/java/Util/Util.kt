package Util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ACID.geojournal.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

const val EXTRA_PERSON_ID="com.example.appw4.PersonID"
class Util {
    companion object{
        var personID: String? = null
        fun openActivity(context: Context,
                         objClass: Class<*>,
                         key: String="",
                         value: String?=null){
            val intent= Intent(context
                , objClass).apply { putExtra(key,value) }
            context.startActivity(intent)
        }
        fun openActivity(context: Context,
                         objClass: Class<*>,
                         key: String="",
                         finished: Boolean=false,
                         value: String?=null){
            val intent= Intent(context
                , objClass).apply { putExtra(key,value) }
            if(finished)
                context.startActivity(intent)
            else
                context.startActivity(intent)
        }
        fun openActivityAndFinish(context: Context, targetActivityClass: Class<*>) {
            val intent = Intent(context, targetActivityClass).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }

        fun parseStringToDateModern(dateString: String, pattern: String): LocalDate? {
            return try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                LocalDate.parse(dateString, formatter)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun parseStringToDateTimeModern(dateTimeString: String, pattern: String): LocalDateTime? {
            return try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                LocalDateTime.parse(dateTimeString, formatter)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun parseStringToDateLegacy(dateString: String, pattern: String): Date? {
            return try {
                val formatter = SimpleDateFormat(pattern, Locale.getDefault())
                return formatter.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun showShortToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // Extension function for Context
        /*fun Context.showShortToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Extension function for Activity
        fun AppCompatActivity.showShortToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }*/

        fun showDialogCondition(context: Context, questionText: String, callback: () ->  Unit){
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(questionText)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.TextYes), DialogInterface.OnClickListener{
                        dialog, id -> callback()
                })
                .setNegativeButton(context.getString(R.string.TextNo), DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.setTitle(context.getString(R.string.TextTitleDialogQuestion))
            alert.show()
        }
        fun showPhotoSelectionDialog(context: Context, onTakePhoto: () -> Unit, onSelectFromGallery: () -> Unit) {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select Profile Photo")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> onTakePhoto()
                    1 -> onSelectFromGallery()
                    2 -> dialog.dismiss()
                }
            }
            builder.show()
        }

        //region Image Picker Utilities

        /** Creates camera launcher for taking pictures */
        fun createCameraLauncher(
            activity: AppCompatActivity,
            onPhotoCaptured: (Bitmap) -> Unit,
            onCancel: () -> Unit = {}
        ) = activity.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                onPhotoCaptured(bitmap)
            } else {
                onCancel()
            }
        }

        /** Creates gallery launcher for selecting images */
        fun createGalleryLauncher(
            activity: AppCompatActivity,
            onPhotoSelected: (Bitmap) -> Unit,
            onError: (String) -> Unit = {},
            onCancel: () -> Unit = {}
        ) = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { imageUri ->
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, imageUri)
                        onPhotoSelected(bitmap)
                    } catch (e: Exception) {
                        onError("Error loading image: ${e.message}")
                    }
                }
            } else {
                onCancel()
            }
        }

        /** Launches camera to take photo */
        fun takePhoto(cameraLauncher: Any) {
            if (cameraLauncher is androidx.activity.result.ActivityResultLauncher<*>) {
                @Suppress("UNCHECKED_CAST")
                (cameraLauncher as androidx.activity.result.ActivityResultLauncher<Unit?>).launch(null)
            }
        }

        /** Launches gallery to select photo */
        fun selectPhoto(galleryLauncher: Any) {
            if (galleryLauncher is androidx.activity.result.ActivityResultLauncher<*>) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                @Suppress("UNCHECKED_CAST")
                (galleryLauncher as androidx.activity.result.ActivityResultLauncher<Intent>).launch(intent)
            }
        }

//endregion
    }
}