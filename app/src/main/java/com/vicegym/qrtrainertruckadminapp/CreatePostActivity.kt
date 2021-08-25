package com.vicegym.qrtrainertruckadminapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vicegym.qrtrainertruckadminapp.data.Post
import com.vicegym.qrtrainertruckadminapp.databinding.ActivityCreatePostBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_WRITE = 105
        private const val REQUEST_CAMERA = 106
    }

    private var isPictureTaken = false
    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var photoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.ivDailyChallengePicture.setOnClickListener { checkCameraPermission() }
        binding.btnDailyChallengeSendPost.setOnClickListener { sendClick() }
    }

    private fun checkCameraPermission() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                createPhoto()
            }
            else -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        }
    }

    private fun checkWritePermission() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PackageManager.PERMISSION_GRANTED -> {
                return
            }
            else -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    createPhoto()
                } else {
                    val dialog = AlertDialog.Builder(this).setTitle("FIGYELEM")
                        .setMessage("Engedély nélkül nem tudom megnyitni a kamerát :(")
                        .setPositiveButton("Engedély megadása") { _, _ ->
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
                        }
                        .setNegativeButton("Engedély elutasítása") { dialog, _ -> dialog.cancel() }
                        .create()
                    dialog.show()
                }
                return
            }
            REQUEST_WRITE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    createPhoto()
                } else {
                    val dialog = AlertDialog.Builder(this).setTitle("FIGYELEM")
                        .setMessage("Engedély nélkül nem tudom elmenteni a fotót a háttértárra :(")
                        .setPositiveButton("Engedély megadása") { _, _ ->
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE)
                        }
                        .setNegativeButton("Engedély elutasítása") { dialog, _ -> dialog.cancel() }
                        .create()
                    dialog.show()
                }
                return
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun createPhoto() {
        checkWritePermission()
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e("Create file for Photo", "Error")
                    Toast.makeText(this, "Error while creating the file for photo", Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.vicegym.qrtrainertruckadminapp.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                }
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return if (storageDir != null) {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
                .apply { photoPath = absolutePath }
        } else {
            Log.e("PhotoSave", "Photo not saved")
            Toast.makeText(this, "Photo not saved", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CAMERA) {
            setPic()
            isPictureTaken = true
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = binding.ivDailyChallengePicture.width
        val targetH: Int = binding.ivDailyChallengePicture.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(photoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = 1.coerceAtLeast((photoW / targetW).coerceAtMost(photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(photoPath, bmOptions)?.also { bitmap ->
            binding.ivDailyChallengePicture.rotation = 90f
            binding.ivDailyChallengePicture.setImageBitmap(bitmap)
        }
    }

    private fun sendClick() {
        when {
            !isPictureTaken -> Toast.makeText(baseContext, "Nem készítettél képet!", Toast.LENGTH_SHORT).show()
            binding.etDailyChallengeTime.text.isNullOrEmpty() -> Toast.makeText(baseContext, "Nem írtad be a futott idődet!", Toast.LENGTH_SHORT)
                .show()
            else -> {
                try {
                    uploadPostWithImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun uploadPostWithImage() {
        val storageReference = Firebase.storage.reference
        val newImageName = "Admin" + "_${Date().time}" + ".jpg"
        val newImageRef = storageReference.child("images/$newImageName")

        val bitmap: Bitmap = (binding.ivDailyChallengePicture.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()

        newImageRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }

                newImageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                uploadPost("gs://qrtrainertruck.appspot.com/admin/profileimage.jpg", downloadUri.toString())
            }
    }

    private fun uploadPost(profilePicture: String? = null, imageUrl: String? = null) {
        val newPost = Post(
            "admin",
            profilePicture,
            "Admin",
            binding.etDailyChallengeTime.text.toString(),
            binding.etDailyChallengeDescription.text.toString(),
            imageUrl
        )

        val db = Firebase.firestore

        db.collection("posts").document("${Date().time}")
            .set(newPost)
            .addOnSuccessListener {
                Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e -> Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show() }
    }
}