package ja.burhanrashid52.photoeditor

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import android.widget.Toast

class BitMapWorker(val context: Context) {

    var xMin : Int? = null
    var yMin : Int? = null
    var xMax : Int? = null
    var yMax : Int? = null
    fun findTransparentFromImageView(imageView: ImageView) : Rect?{
        val transparentRect: Rect? = findTransparentArea(imageView)
        if (transparentRect != null) {


            xMin = transparentRect.left
            yMin = transparentRect.top
            xMax = transparentRect.right
            yMax = transparentRect.bottom
            // Use the xMin, yMin, xMax, and yMax as needed
        }
        return transparentRect
    }

    fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun makeCircularBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val radius = Math.min(width, height) / 2

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        canvas.drawCircle(width / 2f, height / 2f, radius.toFloat(), paint)

        return output
    }

    fun findTransparentArea(imageView: ImageView): Rect? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {


            val bitmap = drawable.bitmap
            return findTransparentArea(bitmap)
        }
        else
        {

        }
        return null
    }

    fun findTransparentArea(bitmap: Bitmap): Rect? {


        val width = bitmap.width
        val height = bitmap.height

        var left = width
        var top = height
        var right = 0
        var bottom = 0


        for (x in 0 until width) {

            for (y in 0 until height) {

                val pixel = bitmap.getPixel(x, y)
                if (pixel shr 24 == 0) {


                    left = minOf(left, x)
                    top = minOf(top, y)
                    right = maxOf(right, x)
                    bottom = maxOf(bottom, y)
                    Log.e("VALUES", "${left} ${top} ${right} ${bottom}")
                }
                else{

                }
            }
        }

        if (left <= right && top <= bottom) {

            return Rect(left, top, right, bottom)
        }



        return null
    }

}