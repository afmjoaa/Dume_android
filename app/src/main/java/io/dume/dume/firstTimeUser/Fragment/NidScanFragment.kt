package io.dume.dume.firstTimeUser.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Hdr
import io.dume.dume.R
import io.dume.dume.firstTimeUser.ForwardFlowHostActivity
import io.dume.dume.firstTimeUser.ForwardFlowViewModel
import io.dume.dume.firstTimeUser.NID
import io.dume.dume.util.StateManager
import kotlinx.android.synthetic.main.fragment_nid_scan.*


class NidScanFragment : Fragment(), View.OnClickListener {
    private lateinit var navController: NavController
    private lateinit var viewModel: ForwardFlowViewModel
    private lateinit var hiwDialog: AlertDialog
    private lateinit var skipNIDScanDialog: BottomSheetDialog
    private lateinit var parent: ForwardFlowHostActivity
    private var flag: Boolean = false
    private lateinit var stateManager: StateManager
    private var NIDNo: Long? = null
    private var NIDName: String? = null
    private var NIDBirthDate: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nid_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.run {
            viewModel = ViewModelProviders.of(this).get(ForwardFlowViewModel::class.java)
        } ?: throw Throwable("invalid activity")
        navController = Navigation.findNavController(view)
        parent = activity as ForwardFlowHostActivity
        initDialog()
        init()
        configureCamera()
    }

    override fun onResume() {
        super.onResume()
        flag = false
    }

    override fun onPause() {
        super.onPause()
        camera.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.destroy()
    }


    private fun configureCamera() {

        camera?.setLifecycleOwner(viewLifecycleOwner)

        Log.e("debug", "configureCamera ${camera}  isOpen : ${camera.isOpened} isTakingPicture : ${camera.isTakingPicture}")
        stateManager = StateManager.getInstance(context)
        squareProgressBar?.setWidth(7)
        squareProgressBar?.setProgress(0)
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
        squareProgressBar?.setColor("#0288d1")
        squareProgressBar?.setRoundedCorners(true, px)
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.e("debug", "Picture Captured")
                camera?.takePictureSnapshot()
                if (!flag) {
                    handler.postDelayed(this, 1500)
                }
            }
        }, 2000)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                val bitmap = BitmapFactory.decodeByteArray(result.data, 0, result.data.size, null)
                squareProgressBar?.setImageBitmap(bitmap)
                val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
                if (firebaseVisionImage != null) {
                    recognizeText(firebaseVisionImage)
                } else {
                    Log.e("getText", "null image found")
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.nid_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.flush) {
            if (camera.getFlash() == Flash.TORCH) {
                item.icon = resources.getDrawable(R.drawable.flush_icon)
                camera.setFlash(Flash.OFF)
            } else {
                item.icon = resources.getDrawable(R.drawable.no_flush_icon)
                camera.setFlash(Flash.TORCH)
            }
        } else if (id == R.id.hdr) {
            if (camera.getHdr() == Hdr.ON) {
                item.icon = resources.getDrawable(R.drawable.hdr_icon)
                camera.setHdr(Hdr.OFF)
            } else {
                item.icon = resources.getDrawable(R.drawable.no_hdr_icon)
                camera.setHdr(Hdr.ON)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        setHasOptionsMenu(true)
        howNIDScanWork.setOnClickListener(this)
        dontHaveNIDBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.howNIDScanWork -> {
                hiwDialog.show()
            }
            R.id.dontHaveNIDBtn -> {
                skipNIDScanDialog.show()
            }
            else -> navController.navigate(R.id.action_nidFragment_to_registerFragment)
        }
    }

    fun initDialog() {
        /**
         * Dialog that notifies user about HOW NID VERIFICATION WORKS?
         * */
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
        val customLayout = layoutInflater.inflate(R.layout.custom_nid_scan_info, null, false)
        materialAlertDialogBuilder.setView(customLayout)
        hiwDialog = materialAlertDialogBuilder.create()
        try {
            val dismissBtn = customLayout.findViewById<Button>(R.id.dismiss_btn)
            dismissBtn.setOnClickListener { v -> hiwDialog.dismiss() }
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
        }

        /**
         *  if user do not have any nid card, they are able skip nid verification step after reading the warning message
         *  */
        skipNIDScanDialog = BottomSheetDialog(context!!)
        val skipNIDScanView = this.layoutInflater.inflate(R.layout.custom_bottom_sheet_dialogue_cancel, null)
        skipNIDScanDialog.setContentView(skipNIDScanView)
        val mainText = skipNIDScanDialog.findViewById<TextView>(R.id.main_text)
        val subText = skipNIDScanDialog.findViewById<TextView>(R.id.sub_text)
        val cancelYesBtn = skipNIDScanDialog.findViewById<Button>(R.id.cancel_yes_btn)
        val cancelNoBtn = skipNIDScanDialog.findViewById<Button>(R.id.cancel_no_btn)
        if (mainText != null && subText != null && cancelYesBtn != null && cancelNoBtn != null) {
            mainText.setText(getString(R.string.skip_nid))
            cancelYesBtn.setText(getString(R.string.yes_skip))
            cancelNoBtn.setText(getString(R.string.cancel))
            subText.setText(getString(R.string.nid_skip_details))
            cancelNoBtn.setOnClickListener(View.OnClickListener { skipNIDScanDialog.dismiss() })
            cancelYesBtn.setOnClickListener(View.OnClickListener { skipNIDScanDialog.dismiss(); navController.navigate(R.id.action_nidFragment_to_registerFragment) })
        }
    }

    private fun recognizeText(image: FirebaseVisionImage) {
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    val validPercent = validDataPercent(firebaseVisionText)
                    squareProgressBar.setProgress(validPercent)
                    if (validPercent == 100) {
                        flag = true
                        stateManager.setValue("NIDNo", NIDNo)
                        stateManager.setValue("NIDName", NIDName)
                        stateManager.setValue("NIDBirthDate", NIDBirthDate)
                        viewModel.scan.postValue(NID(NIDName!!, NIDBirthDate!!, NIDNo!!))
                        navController.navigate(R.id.action_nidFragment_to_registerFragment)

                    } else {
                        Log.e("debug", "ValidPercent is $validPercent")
                    }

                }
                .addOnFailureListener { e -> Log.e("getText", "Failed to get text") }
    }

    private fun validDataPercent(firebaseVisionText: FirebaseVisionText): Int {
        var returnPercent = 0
        val blocks = firebaseVisionText.textBlocks
        for (j in blocks.indices) {
            val block = blocks[j]
            val lines = block.lines
            for (i in lines.indices) {
                val line = lines[i]
                val inputLine = line.text
                if (inputLine.contains("National ID Card")) {
                    returnPercent = returnPercent + 25
                }
                if (inputLine.contains("Birth")) {
                    returnPercent = returnPercent + 25
                    NIDBirthDate = inputLine
                }
                if (inputLine.contains("Name")) {
                    try {
                        NIDName = blocks[j + 1].lines[0].text
                        returnPercent = returnPercent + 25
                    } catch (e: Exception) {
                        // parent.flush("Error in line number ${e.stackTrace[0].lineNumber} error : ${e.localizedMessage}")
                    }

                }
                try {
                    NIDNo = java.lang.Long.parseLong(inputLine.replace("\\s+".toRegex(), ""))
                    if (NIDNo!! >= 10) {
                        returnPercent = returnPercent + 25
                    }
                } catch (e: Exception) {
                    //  parent.flush(e.localizedMessage)
                }

            }
        }
        return if (returnPercent > 100) {
            100
        } else {
            returnPercent
        }
    }


}
