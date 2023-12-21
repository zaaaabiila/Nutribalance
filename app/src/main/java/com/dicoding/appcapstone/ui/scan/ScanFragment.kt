package com.dicoding.appcapstone.ui.scan

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.dicoding.appcapstone.R
import com.dicoding.appcapstone.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ScanFragment : Fragment() {

    private lateinit var selectBtn: Button
    private lateinit var predBtn: Button
    private lateinit var nameView: TextView
    private lateinit var imageView: ImageView
    private lateinit var viewModel: ScanViewModel
    private lateinit var dataView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(ScanViewModel::class.java)

        selectBtn = view.findViewById(R.id.selectBtn)
        predBtn = view.findViewById(R.id.predictBtn)
        nameView = view.findViewById(R.id.nameView)
        imageView = view.findViewById(R.id.imageView)
        dataView = view.findViewById(R.id.dataView)

        viewModel.calorieInfo.observe(viewLifecycleOwner, Observer { info ->
            dataView.text = info
        })


        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        selectBtn.setOnClickListener{
            var intent: Intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/")
            startActivityForResult(intent, 100)
        }

        val cameraBtn: Button = view.findViewById(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(takePictureIntent, 101)
            }
        }

        viewModel.query.observe(viewLifecycleOwner, Observer { query ->
            nameView.text = query
        })

        viewModel.bitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            imageView.setImageBitmap(bitmap)
        })

        predBtn.setOnClickListener{
            var tensorImage: TensorImage? = null
            if (viewModel.bitmap.value != null && viewModel.bitmap.value!!.width > 0 && viewModel.bitmap.value!!.height > 0) {
                tensorImage = TensorImage(DataType.UINT8)
                tensorImage.load(viewModel.bitmap.value!!)
            } else {
                Toast.makeText(requireContext(), "Please select a valid image first.", Toast.LENGTH_SHORT).show()
            }

            tensorImage?.let {
                val processedImage = imageProcessor.process(it)
                val model = MobilenetV110224Quant.newInstance(requireContext())

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
                inputFeature0.loadBuffer(processedImage.buffer)

                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                var maxIdx = 0
                outputFeature0.forEachIndexed { index, fl ->
                    if(outputFeature0[maxIdx]<fl){
                        maxIdx = index
                    }
                }

                val query = viewModel.labels[maxIdx].split(" ")[0] // Take the first word as the query
                viewModel.setQuery(query)
                nameView.setText(viewModel.query.value)

                // Call the CalorieNinjaApi with the query
                viewModel.getCalorieInfo(query)
                model.close()
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                viewModel.setBitmap(bitmap)
            }
        } else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            viewModel.setBitmap(imageBitmap)
        }
    }

}