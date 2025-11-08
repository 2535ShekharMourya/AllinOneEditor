package com.example.collageproject.ui.common


import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.GONE
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.collageproject.R
import com.example.collageproject.data.model.commonmodel.ImageCoordinate
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.databinding.FragmentImageCollageBinding
import com.example.collageproject.extensions.animateClick
import com.example.collageproject.extensions.logMessage
import com.example.collageproject.extensions.onOneClick
import ja.burhanrashid52.photoeditor.PhotoEditor
import java.io.File
import java.io.FileOutputStream

class ImageCollageFragment : Fragment() {
    private var _binding: FragmentImageCollageBinding? = null
    private val binding get() = _binding!!
    private lateinit var myViewModel: CollageViewModel
    private var templateImageData: TemplateItem? = null
    private var uris: ArrayList<Uri>? = null

    // Add this variable to your class
    private var selectedImagePosition: Int = -1
    private var width = 0
    private var height = 0
    private var imageDetails: MutableList<ImageCoordinate> = mutableListOf()
    val mediaType =
        ActivityResultContracts.PickVisualMedia.ImageOnly as ActivityResultContracts.PickVisualMedia.VisualMediaType
    private val multiMediaPicker =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { selectedImageUris ->
            if (selectedImageUris.isNotEmpty()) {
                uris = selectedImageUris?.let {
                    extendUriList(
                        ArrayList(selectedImageUris),
                        imageDetails.size
                    )
                }
                myViewModel._userImages.value = uris
                binding.rlMainContainer.removeAllViews()
                addUserSelectedImages()
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        templateImageData =
            arguments?.getParcelable<TemplateItem>("feed_template")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  val view = inflater.inflate(R.layout.fragment_image_collage , container, false)
        _binding = FragmentImageCollageBinding.inflate(inflater, container, false)
        myViewModel = ViewModelProvider(requireActivity()).get(CollageViewModel::class.java)
        setUpMainImage(templateImageData)
        templateImageData?.image_coordinates?.forEach {
            imageDetails.add(it)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.shareLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //  view.updatePadding(bottom = systemBars.bottom + 18.dpToPx()) // Keep your original padding
            insets
        }
        binding.imgBack?.setOnClickListener() {
            requireActivity().finish()
        }
//        binding.btnShare?.apply {
//            onOneClick {
//                "btnShare clicked".logMessage()
//                Toast.makeText(requireContext(), "Share", Toast.LENGTH_SHORT).show()
//            }
//            animateClick()
//        }
//
//        binding.btnDownload?.apply {
//            onOneClick {
//                "btnDownload clicked".logMessage()
//                Toast.makeText(context, "Download", Toast.LENGTH_SHORT).show()
//            }
//            animateClick()
//        }

//        binding.whatsapp.setOnClickListener {
//            onClick(it)
//        }
//        binding.facebook3.setOnClickListener {
//            onClick(it)
//        }
//        binding.instagram3.setOnClickListener {
//            onClick(it)
//        }
        binding.shareMore.setOnClickListener {
            onClick(it)
        }
        binding.download3.setOnClickListener {
            onClick(it)
        }

        setUpListeners()
//        onClick(view)
    }

    private fun setUpMainImage(image: TemplateItem?) {
        binding.lottieView.visibility = View.VISIBLE
        binding.storiesThumbnailLottie.playAnimation()

        val imageListener =
            PhotoEditor.ImageListener { imageView ->
                Glide.with(this)
                    .load(imageView.drawable)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            width = resource.intrinsicWidth
                            height = resource.intrinsicHeight
                            val params =
                                binding.flContainerParent.layoutParams as ConstraintLayout.LayoutParams
                            params.dimensionRatio = "${width}:${height}"
                            binding.flContainerParent.layoutParams = params
                            binding.photoEditorViewForImage.viewTreeObserver.addOnGlobalLayoutListener(
                                object : ViewTreeObserver.OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        binding.photoEditorViewForImage.viewTreeObserver.removeOnGlobalLayoutListener(
                                            this
                                        )
                                        val viewWidth = binding.photoEditorViewForImage.width
                                        val viewHeight = binding.photoEditorViewForImage.height
                                        // Proceed only when valid size is available
                                        if (viewWidth > 0 && viewHeight > 0) {
                                            addUserSelectedImages() //Add User Selected Images according to png image
                                        } else {
                                            Log.w(
                                                "LayoutWarning",
                                                "photoEditorViewForImage size not ready"
                                            )
                                        }
                                    }
                                })

                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })

            }
        val mPhotoEditor = PhotoEditor.Builder(context, binding.photoEditorViewForImage, false)
            .setPinchTextScalable(true)
            .build()
        mPhotoEditor.addFullImage(
            image?.image_url,
            false,
            imageListener,
            null
        )
    }


    private fun addUserSelectedImages() {
        // Container which holds all customViews based on imageCoordinates
        val relativeLayout = RelativeLayout(context)
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        relativeLayout.layoutParams = layoutParams

        // Get view dimensions
        val viewWidth = binding.photoEditorViewForImage.width
        val viewHeight = binding.photoEditorViewForImage.height

        imageDetails.forEachIndexed { index, imageDetail ->
            imageDetail.left_ratio = (imageDetail.left_in_px?.toFloat() ?: 257f) / width.toFloat()
            imageDetail.top_ratio = (imageDetail.top_in_px?.toFloat() ?: 634f) / height.toFloat()
            imageDetail.width_ratio = (imageDetail.width_in_px?.toFloat() ?: 120f) / width.toFloat()
            imageDetail.height_ratio = (imageDetail.height_in_px?.toFloat() ?: 120f) / height.toFloat()

            val customImageview = SquareTouchImageView(requireContext())

            val widthPx = (imageDetail.width_ratio ?: .1f).times(viewWidth.toFloat())
            val heightPx = (imageDetail.height_ratio ?: .1f).times(viewHeight.toFloat())

            val imageLayoutParams = RelativeLayout.LayoutParams(widthPx.toInt(), heightPx.toInt())
            customImageview.layoutParams = imageLayoutParams

            // Add tag to identify the image position
            customImageview.tag = index

            // Add clickable property to handle click events
            customImageview.isClickable = true
            customImageview.isFocusable = true

            // APPLY ROTATION from imageDetail
            val rotationDegrees = imageDetail.rotation_degrees?.toFloat()
            if (rotationDegrees != null) {
                customImageview.rotation = rotationDegrees
            }

            // Set up click listener for image actions
            customImageview.setOnClickListener { view ->
                showImageActions(view as SquareTouchImageView, index)
            }
            customImageview.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (customImageview.drawable != null) {
                        customImageview.setupInitialMatrix()
                        customImageview.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            })
            myViewModel.userImages.observe(viewLifecycleOwner) { listUri ->
                if (!listUri.isNullOrEmpty() && index < listUri.size) {
                    val bitmap = uriToBitmap(requireContext(), listUri[index])
                    // Add fade-in animation
                    customImageview.alpha = 0f
                    customImageview.setImageBitmap(bitmap)
//                    Glide.with(this)
//                        .load(listUri[index])
//                        .into(customImageview)
                    customImageview.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                } else {
                    // Add a subtle border or highlight to indicate it's interactive
                    // Add fade-in animation
                    customImageview.alpha = 0f
                    customImageview.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }

            }

            val drawable = ResourcesCompat.getDrawable(
                resources,
                com.example.collageproject.R.drawable.bg_gray_with_plus,
                null
            )
            customImageview.background = drawable

            relativeLayout.addView(customImageview)
            customImageview.x = (imageDetail.left_ratio ?: 0f).times(viewWidth.toFloat())
            customImageview.y = (imageDetail.top_ratio ?: 0f).times(viewHeight.toFloat())
        }
        binding.rlMainContainer.removeAllViews()
        binding.rlMainContainer.addView(relativeLayout)
        binding.lottieView.visibility = GONE
        //  myViewModel.userImages.value?.clear()
    }

    /**
     * Shows action popup for the tapped image
     */
    private fun showImageActions(imageView: SquareTouchImageView, position: Int) {

        val location = IntArray(2)
        imageView.getLocationOnScreen(location)
        val anchorX = location[0]
        val anchorY = location[1]  // + imageView.height
        selectedImagePosition = position
        // Launch image picker for single image
        pickSingleImage()
    }


    /**
     * Setup single image picker
     */
    private val singleImagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                // Get the current list of URIs
                val currentUris = myViewModel.userImages.value?.toMutableList() ?: mutableListOf()
                Log.d("userImageList", "currentUris size: ${currentUris.size}")

                // If the position is valid
                if (selectedImagePosition >= 0) {
                    Log.d("userImageList", "selectedImagePosition: ${selectedImagePosition}")
                    // Ensure list is large enough
                    while (currentUris.size <= selectedImagePosition) {
                        currentUris.add(Uri.EMPTY)
                    }

                    // Replace the URI at the selected position
                    currentUris[selectedImagePosition] = uri

                    // Update the view model
                    myViewModel._userImages.value = ArrayList(currentUris)

                    // Update just the specific image view
                    updateSingleImageView(selectedImagePosition, uri)
                }
            }
        }

    /**
     * Launches picker for a single image
     */
    private fun pickSingleImage() {
        singleImagePicker.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    /**
     * Updates a single image view without rebuilding the entire layout
     */
    private fun updateSingleImageView(position: Int, uri: Uri) {
        // Find the image view with the specified tag
        val relativeLayout = binding.rlMainContainer.getChildAt(0) as? RelativeLayout
        relativeLayout?.let { container ->
            for (i in 0 until container.childCount) {
                val view = container.getChildAt(i)
                if (view is SquareTouchImageView && view.tag == position) {
                    val bitmap = uriToBitmap(requireContext(), uri)
                    view.setImageBitmap(bitmap)
                    view.setupInitialMatrix()
                    break
                }
            }
        }
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        Log.d("UriStatus", "uri: ${uri}")
        return try {
            Log.d("UriStatus try", "uri: ${uri}")
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Get the correct orientation and rotate the bitmap
            val orientedBitmap = getOrientedBitmap(context, uri, originalBitmap)
            orientedBitmap
        } catch (e: Exception) {
            Log.d("UriStatus catch", "uri: ${uri}")
            e.printStackTrace()
            null
        }
    }

    private fun getOrientedBitmap(context: Context, uri: Uri, bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.postRotate(90f)
                    matrix.postScale(-1f, 1f)
                }

                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.postRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }

                else -> return bitmap // No rotation needed
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.e("OrientationFix", "Error reading EXIF data", e)
            return bitmap // Return original bitmap if EXIF reading fails
        }
    }


    private fun extendUriList(selectedUris: ArrayList<Uri>, n: Int): ArrayList<Uri> {
        if (selectedUris.isEmpty()) return arrayListOf() // Edge case

        val result = arrayListOf<Uri>()
        var index = 0

        while (result.size < n) {
            result.add(selectedUris[index % selectedUris.size]) // Cycle through selected images
            index++
        }
        return result
    }

        fun onClick(v: View?) {
        Log.d("status", "onclick : ${v}")
        when (v) {
//            binding.whatsapp -> {
//
//                shareBitmapVia(
//                    requireContext(),
//                    getBitmapFromView(binding.flContainerParent),
//                    "com.whatsapp"
//                )
//            }
//
//            binding.facebook3 -> {
//                shareBitmapVia(
//                    requireContext(),
//                    getBitmapFromView(binding.flContainerParent),
//                    "com.facebook"
//                )
//            }
//
//            binding.instagram3 -> {
//                shareBitmapViaInstagram(
//                    requireContext(),
//                    getBitmapFromView(binding.flContainerParent)
//                )
//            }

            binding.shareMore -> {
                saveBitmapAndGetUri(
                    requireContext(),
                    getBitmapFromView(binding.flContainerParent)
                )?.let { it1 ->
                    shareImage(
                        requireContext(),
                        it1
                    )
                }
            }

            binding.download3 -> {
                saveBitmapAndGetUri(
                    requireContext(),
                    getBitmapFromView(binding.flContainerParent)
                )
                Toast.makeText(requireContext(), "Image Saved successfully", Toast.LENGTH_SHORT)
                    .show()
            }

//            binding.wm -> {
//                Values.interfaceForParent?.loadAds(requireActivity(), LoadAds.REWARDED) {
//                    binding.wm.postDelayed({
//                        val explosionField: ExplosionField =
//                            ExplosionField.attach2Window(requireActivity())
//                        explosionField.explode(binding.wmImage)
//                        binding.wm.visibility = GONE
//                    }, 50)
//                }
//            }
        }
    }
    private fun setUpListeners() {
        binding.mbChangeImage.apply {
            setOnClickListener {
                multiMediaPicker.launch(
                    PickVisualMediaRequest.Builder().setMediaType(mediaType).build()
                )
            }
            animateClick()
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        binding.outline.visibility = View.INVISIBLE
        binding.wmClose.visibility = View.INVISIBLE
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        binding.outline.visibility = View.VISIBLE
        binding.wmClose.visibility = View.VISIBLE
        return bitmap
    }
//
//    private fun shareBitmapVia(context: Context, bitmap: Bitmap, packageName: String) {
//        try {
//            val cachePath = File(context.cacheDir, "images")
//            cachePath.mkdirs()
//            val file = File(cachePath, "shared_image.png")
//            val fileOutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
//            fileOutputStream.close()
//            val uri =
//                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
//
//            val intent = Intent(Intent.ACTION_SEND).apply {
//                Intent.setType = "image/png"
//                putExtra(Intent.EXTRA_STREAM, uri)
//                Intent.setPackage = packageName
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//            context.startActivity(intent)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val fileName = "BroChill_${System.currentTimeMillis()}.png"

            // Use MediaStore for Android 10+ (Scoped Storage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/BroChill Images"
                    )
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                }
                uri // Return Uri
            }
            // Use FileProvider for older versions
            else {
                val parentDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "BroChill Images"
                )
                if (!parentDir.exists()) parentDir.mkdirs()

                val file = File(parentDir, fileName)
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()

                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun shareImage(context: Context, imageUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant temporary access
        }

        // Show the chooser so the user can select an app
        context.startActivity(Intent.createChooser(intent, "Share Image via"))
    }


    //    private fun shareBitmapViaInstagram(context: Context, bitmap: Bitmap) {
//        val instagramPackage = "com.instagram.android"
//        try {
//            val cachePath = File(context.cacheDir, "images")
//            cachePath.mkdirs()
//            val file = File(cachePath, "shared_image.png")
//            val fileOutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
//            fileOutputStream.close()
//            val uri =
//                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
//
//            val intent = Intent(Intent.ACTION_SEND).apply {
//                Intent.setType = "image/png"
//                putExtra(Intent.EXTRA_STREAM, uri)
//                Intent.setPackage = instagramPackage
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            context.startActivity(intent)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(
//                context,
//                "Instagram not found or unable to share directly",
//                Toast.LENGTH_SHORT
//            ).show()
//            // Fallback to Intent Chooser
//            val generalIntent = Intent(Intent.ACTION_SEND).apply {
//                type = "image/png"
//             //   putExtra(Intent.EXTRA_STREAM, uri)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//            context.startActivity(Intent.createChooser(generalIntent, "Share image via..."))
//        }
//    }


}
