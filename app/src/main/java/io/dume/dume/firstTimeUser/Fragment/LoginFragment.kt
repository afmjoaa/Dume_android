package io.dume.dume.firstTimeUser.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import io.dume.dume.R
import io.dume.dume.firstTimeUser.*
import io.dume.dume.util.DumeUtils
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var navController: NavController
    private lateinit var viewModel: ForwardFlowViewModel
    private lateinit var parent: ForwardFlowHostActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.run {
            viewModel = ViewModelProviders.of(this).get(ForwardFlowViewModel::class.java)
        } ?: throw Throwable("invalid activity")
        parent = activity as ForwardFlowHostActivity
        navController = Navigation.findNavController(view)
        init()
        initObservers()
    }


    private fun init() {
        sendCodeBtn.setOnClickListener(this)
        phoneNumberEditText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s.toString()
                if (phoneNumber.isEmpty()) {
                    showErrorInInput("Should not be empty")
                    sendCodeBtn.isEnabled = false
                } else if (!DumeUtils.isInteger(phoneNumber)) {
                    showErrorInInput("Only Digits Allowed (0-9)")
                    sendCodeBtn.isEnabled = false
                } else if (phoneNumber.length != 11) {
                    showErrorInInput("Should be 11 Digits")
                    sendCodeBtn.isEnabled = false
                } else {
                    viewModel.phoneNumber.postValue(s.toString())
                    sendCodeBtn.isEnabled = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = run { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = run { }

        })
    }

    private fun initObservers() {

        viewModel.autoVerified.observe(this, Observer { if (it) skipNextAction() })
        viewModel.codeSent.observe(this, Observer { flush("Conde sent $it"); if (it) nextAction() })
        viewModel.error.observe(this, Observer { it?.let { flush(it) } })
        viewModel.load.observe(this, Observer {
            if (it) {
                parent.showProgress();
                sendCodeBtn.isEnabled = false
            } else {
                parent.hideProgress()
                sendCodeBtn.isEnabled = true

            }
        })
    }


    private fun nextAction() {
        updateForwardFlowState()
        navController.navigate(R.id.action_loginFragment_to_verificationFragment)
    }

    private fun skipNextAction() {
        updateForwardFlowState()
        if (viewModel.role.value == Role.TEACHER) {
            navController.navigate(R.id.action_loginFragment_to_nidFragment)
        } else {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun flush(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorInInput(msg: String) {
        phoneNumberEditText.setError(msg)
    }

    private fun updateForwardFlowState() {
        if (viewModel.role.value == Role.STUDENT) {
            viewModel.updateStudentCurrentPosition(ForwardFlowStatStudent.VERIFICATION)
        } else {
            viewModel.updateTeacherCurrentPosition(ForwardFlowStatTeacher.VERIFICATION)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.sendCodeBtn -> {
                viewModel.login(phoneNumberEditText.text.toString())
            }
        }
    }
}

