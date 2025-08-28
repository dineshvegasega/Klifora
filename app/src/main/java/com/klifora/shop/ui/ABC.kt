package com.klifora.shop.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klifora.shop.databinding.AbcBinding
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import java.lang.Exception

class ABC  : AppCompatActivity(), PaymentResultWithDataListener, ExternalWalletListener, DialogInterface.OnClickListener {

    private val TAG = "MainActivity"

    lateinit var binding: AbcBinding

    private val RECORD_REQUEST_CODE: Int = 101

    lateinit var pagerAdapter: FragmentStateAdapter

    lateinit var videoList: ArrayList<String>
    private lateinit var alertDialogBuilder: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AbcBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Checkout.preload(applicationContext)

        alertDialogBuilder = AlertDialog.Builder(this@ABC)
        alertDialogBuilder.setTitle("Payment Result")
        alertDialogBuilder.setCancelable(true)
        alertDialogBuilder.setPositiveButton("Ok",this@ABC)

        binding.btLogin.setOnClickListener {
            startPayment()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun startPayment() {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()
//        val etApiKey = findViewById<EditText>(com.razorpay.R.id.et_api_key)
//        val etCustomOptions = findViewById<EditText>(com.razorpay.R.id.et_custom_options)
     //   if (!TextUtils.isEmpty("rzp_test_Ce5CQWqb8wSD9U")){
            co.setKeyID("rzp_test_Ce5CQWqb8wSD9U")
//        }

        val sss = 130361 * 100
        Log.e("TAG", "totalXXXDD: "+sss)

        try {
            var options = JSONObject()
//            if (!TextUtils.isEmpty("etCustomOptions.text.toString()")){
                options = JSONObject()
//            }else{
                options.put("name","Abc")
                options.put("description","Demoing Charges")
                //You can omit the image option to fetch the image from dashboard
                options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
                options.put("currency","INR")
                options.put("amount", ""+sss)
                options.put("send_sms_hash",true);

                val prefill = JSONObject()
                prefill.put("email","abc@gmail.com")
                prefill.put("contact","9988397521")

                options.put("prefill",prefill)
//            }

            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try{
            val payJSON = JSONObject(p1?.data.toString())
            val payName = payJSON.getString("razorpay_payment_id")
            Log.e("TAG", "payName " + payName)

            alertDialogBuilder.setMessage("Payment Successful : Payment ID: $p0\nPayment Data: ${p1?.data}")
            alertDialogBuilder.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            alertDialogBuilder.setMessage("Payment Failed : Payment Data: ${p2?.data}")
            alertDialogBuilder.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try{
            alertDialogBuilder.setMessage("External wallet was selected : Payment Data: ${p1?.data}")
            alertDialogBuilder.show()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
    }
}