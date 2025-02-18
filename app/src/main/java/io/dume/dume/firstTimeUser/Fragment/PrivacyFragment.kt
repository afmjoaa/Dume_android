package io.dume.dume.firstTimeUser.Fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.dume.dume.R
import io.dume.dume.firstTimeUser.*
import io.dume.dume.util.DumeUtils
import io.dume.dume.util.DumeUtils.getFacebookPageURL
import kotlinx.android.synthetic.main.fragment_privacy.*

class PrivacyFragment : Fragment(){
    private lateinit var navController: NavController
    private lateinit var viewModel: ForwardFlowViewModel
    private lateinit var parent: ForwardFlowHostActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.run {
            viewModel = ViewModelProvider(this).get(ForwardFlowViewModel::class.java)
        } ?: throw Throwable("invalid activity")
        navController = Navigation.findNavController(view)
        parent = activity as ForwardFlowHostActivity
        init()
    }

    private fun init() {
        initView()
        initObserver()
        youtube.movementMethod = LinkMovementMethod.getInstance()

        hotline.setOnClickListener{
            val u = Uri.parse("tel:" + "01303464617")
            val i = Intent(Intent.ACTION_DIAL, u)
            context?.startActivity(i)
        }
        fb_group.setOnClickListener{
            try {
                val facebookIntent = Intent(Intent.ACTION_VIEW)
                val facebookUrl = getFacebookPageURL(context, "https://www.facebook.com/groups/1623868617935891/", "1623868617935891")
                facebookIntent.data = Uri.parse(facebookUrl)
                context!!.startActivity(facebookIntent)
            } catch (err: Exception) {
                Log.e("PrivacyFragment", err.toString())
            }
        }

        fb_page.setOnClickListener{
            try {
                val facebookIntent = Intent(Intent.ACTION_VIEW)
                val facebookUrl = getFacebookPageURL(context, "https://www.facebook.com/dumebd/", "348655689093272")
                facebookIntent.data = Uri.parse(facebookUrl)
                context!!.startActivity(facebookIntent)
            } catch (err: Exception) {
                Log.e("PrivacyFragment", err.toString())
            }
        }

    }

    private fun initView() {
        if (viewModel.role.value == Role.STUDENT) {
            DumeUtils.configAppToolBarTitle(parent, "Student Guide")
            student_guide.visibility = View.VISIBLE
            teacher_guide.visibility = View.GONE
        } else if(viewModel.role.value == Role.TEACHER) {
            DumeUtils.configAppToolBarTitle(parent, "Teacher Guide")
            teacher_guide.visibility = View.VISIBLE
            student_guide.visibility = View.GONE
        }
    }

    private fun initObserver(){
        parent.onContinuePrivacyClick {
            it.let {
                parent.showProgress()
                viewModel.privacyContinue()
            }
        }

        viewModel.success.observe(viewLifecycleOwner, Observer{
            it.getContentIfNotHandled()?.let{
                //work here
                if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    if (viewModel.role.value == Role.STUDENT) {
                        viewModel.updateStudentCurrentPosition(ForwardFlowStatStudent.LOGIN)
                    } else {
                        viewModel.updateTeacherCurrentPosition(ForwardFlowStatTeacher.LOGIN)
                    }
                    parent.hideProgress()
                    navController.navigate(R.id.action_privacyFragment_to_loginFragment)
                }else{
                    if (viewModel.role.value == Role.STUDENT) {
                        viewModel.updateStudentCurrentPosition(ForwardFlowStatStudent.PERMISSION)
                    } else {
                        viewModel.updateTeacherCurrentPosition(ForwardFlowStatTeacher.PERMISSION)
                    }
                    parent.hideProgress()
                    navController.navigate(R.id.action_privacyFragment_to_permissionFragment)
                }
            }
        })
    }

}
