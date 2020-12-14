package com.raiyansoft.alpwapaelaqaria.ui.activity.function

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.makeramen.roundedimageview.RoundedImageView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.util.Common
import com.raiyansoft.alpwapaelaqaria.model.EditProfileResponse
import com.raiyansoft.alpwapaelaqaria.model.ProfileResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import kotlinx.android.synthetic.main.activity_edite_profile.*
import kotlinx.android.synthetic.main.custom_dialog.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditeProfileActivity : AppCompatActivity() {

    private lateinit var back: ImageView
    private lateinit var upload: ImageView
    private lateinit var photo: RoundedImageView
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var save: Button

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private var startName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edite_profile)

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val lang = shared.getString("lang", "ar")!!
        currentLang = shared.getString("lang", "ar")!!
        Common.setLocale(lang, this)

        back = imgEditBack
        upload = imgUpload
        photo = imgEditImage
        name = edEditUsername
        phone = edEditPhone
        save = btnSaveChanges

        if (currentLang != "ar"){
            back.setImageResource(R.drawable.ic_back)
        }

        phone.isEnabled = false
        phone.isClickable = false

        val sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!

        loading = ProgressDialog(this)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))

        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        photo.setImageResource(R.drawable.profile)

        fillInfo()

        back.setOnClickListener {
            backClick()
        }

        upload.setOnClickListener {
            uploadClick()
        }

        save.setOnClickListener {
            saveClick()
        }

    }

    private fun fillInfo() {

        loading.show()
        val service =  ServiceBuilder.apis!!
        val call = service.getProfile(currentLang, "Bearer $token")
        call.enqueue(object : Callback<ProfileResponse> {
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        Glide
                            .with(this@EditeProfileActivity)
                            .load(body.data!!.info!!.image)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(photo)
                        name.text = body.data!!.info!!.name
                        startName = body.data!!.info!!.name!!
                        phone.text = body.data!!.info!!.mobile!!
                        loading.dismiss()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }
        })
    }

    private fun backClick() {
        val intent = Intent(this, InterActivity::class.java)
        intent.putExtra("open", 4)
        startActivity(intent)
        finish()
    }

    private fun uploadClick() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, 100)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@EditeProfileActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result: String = cursor.getString(column_index)
        cursor.close()
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data!!.data != null) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data.data

            loading.show()
            val imagefile = File(getRealPathFromURI(fileUri!!))
            val reqBody = RequestBody.create("avatar".toMediaTypeOrNull(), imagefile)
            val partImage = MultipartBody.Part.createFormData("avatar", imagefile.name, reqBody)


            val service = ServiceBuilder.apis!!
            val cal = service.editProfileImage("Bearer $token", currentLang, partImage)
            cal.enqueue(object : Callback<EditProfileResponse> {
                override fun onResponse(
                    call: Call<EditProfileResponse>,
                    response: Response<EditProfileResponse>
                ) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code!! == 200) {
                            photo.setImageURI(fileUri)
                            loading.dismiss()
                        } else {
                            title.text = getString(R.string.attention)
                            details.text = getString(R.string.upload_error)
                            dialog.show()
                            loading.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<EditProfileResponse>, t: Throwable) {
                    title.text = getString(R.string.attention)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                    loading.dismiss()
                }

            })

        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveClick() {
        val name = name.text.toString()
        loading.show()
        if (name != startName) {
            val service =  ServiceBuilder.apis!!
            val cal = service.editProfileName("Bearer $token", currentLang, name)
            cal.enqueue(object : Callback<EditProfileResponse> {
                override fun onResponse(
                    call: Call<EditProfileResponse>,
                    response: Response<EditProfileResponse>
                ) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code!! == 200) {
                            loading.dismiss()
                            Toast.makeText(
                                this@EditeProfileActivity,
                                getString(R.string.save_success),
                                Toast.LENGTH_LONG
                            ).show()
                            val intent =
                                Intent(this@EditeProfileActivity, InterActivity::class.java)
                            intent.putExtra("open", 4)
                            startActivity(intent)
                            finish()
                        } else {
                            title.text = getString(R.string.attention)
                            details.text = getString(R.string.somthing_wrong)
                            dialog.show()
                            loading.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<EditProfileResponse>, t: Throwable) {
                    title.text = getString(R.string.attention)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                    loading.dismiss()
                }

            })
        } else {
            loading.dismiss()
            val intent =
                Intent(this@EditeProfileActivity, InterActivity::class.java)
            intent.putExtra("open", 4)
            startActivity(intent)
            finish()
        }
    }
}