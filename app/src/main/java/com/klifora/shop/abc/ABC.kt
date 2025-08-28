package com.klifora.shop.abc

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.klifora.shop.databinding.AbcBinding
import com.klifora.shop.networking.RAZORPAY_KEY
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainCategory
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray


@AndroidEntryPoint
class ABC : AppCompatActivity(), OnMapReadyCallback, PaymentResultWithDataListener,
    ExternalWalletListener {
    private val viewModel: ABCVM by viewModels()
    private var _binding: AbcBinding? = null
    val binding get() = _binding!!


    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .build()
        StrictMode.setThreadPolicy(policy)
        super.onCreate(savedInstanceState)
        _binding = AbcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Checkout.preload(getApplicationContext());

//        val mapFragment = getSupportFragmentManager()
//            .findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment!!.getMapAsync(this)

        val co = Checkout()
        co.setKeyID(RAZORPAY_KEY)
        binding.apply {
//            btLogin.setOnClickListener {
//                try {
//                    val options = JSONObject()
//                    options.put("name", "Razorpay Corp")
//                    options.put("description", "Demoing Charges")
////                    options.put("send_sms_hash", true)
////                    options.put("allow_rotation", true)
//                    //You can omit the image option to fetch the image from dashboard
////                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
//                    options.put("currency", "INR")
//                    options.put("amount", "100")
//                    options.put("receipt", "receipt#1")
//                    options.put("order_id", "order_DBJOWzybf0sJbb")
////                    options.put("status", "authorized")
//                    val preFill = JSONObject()
//                    preFill.put("email", "test@razorpay.com")
//                    preFill.put("contact", "9988397522")
//
//                    options.put("prefill", preFill)
//
//                    co.open(this@ABC, options)
//
////
////                    val options = JSONObject()
////
////                    options.put("name", "Merchant Name")
////                    options.put("description", "Reference No. #123456")
////                    options.put("image", "http://example.com/image/rzp.jpg")
////                    options.put("order_id", "107809789780") //from response of step 3.
//////                    options.put("theme.color", "#3399cc")
////                    options.put("currency", "INR")
////                    options.put("amount", "50000") //pass amount in currency subunits
////                    options.put("prefill.email", "gaurav.kumar@example.com")
////                    options.put("prefill.contact", "9988776655")
//////                    val retryObj = JSONObject()
//////                    retryObj.put("enabled", true)
//////                    retryObj.put("max_count", 4)
//////                    options.put("retry", retryObj)
////
////                    co.open(this@ABC, options)
//
//
//
////                    val payloadHelper = PayloadHelper("INR", 100, "order_XXXXXXXXX")
////                    payloadHelper.name = "Gaurav Kumar"
////                    payloadHelper.description = "Description"
////                    payloadHelper.prefillEmail = "gaurav.kumar@example.com"
////                    payloadHelper.prefillContact = "9988397522"
//////                    payloadHelper.prefillCardNum = "4386289407660153"
//////                    payloadHelper.prefillCardCvv = "111"
//////                    payloadHelper.prefillCardExp = "11/24"
//////                    payloadHelper.prefillMethod = "card"
////                    payloadHelper.prefillName = "MerchantName"
//////                    payloadHelper.setSendSmsHash(true)
//////                    payloadHelper.setRetryMaxCount(4)
//////                    payloadHelper.setRetryEnabled(true)
////                    payloadHelper.color = "#000000"
//////                    payloadHelper.setAllowRotation(true)
//////                    payloadHelper.setRememberCustomer(true)
//////                    payloadHelper.setTimeout(10)
//////                    payloadHelper.setRedirect(true)
////                    payloadHelper.recurring = "1"
//////                    payloadHelper.setSubscriptionCardChange(true)
//////                    payloadHelper.customerId = "cust_XXXXXXXXXX"
////                    payloadHelper.callbackUrl = "https://accepts-posts.request"
//////                    payloadHelper.subscriptionId = "sub_XXXXXXXXXX"
//////                    payloadHelper.setModalConfirmClose(true)
////                    payloadHelper.backDropColor = "#ffffff"
//////                    payloadHelper.setHideTopBar(true)
////                    payloadHelper.notes = JSONObject().apply {
////                        put("email", "test@razorpay.com")
////                    put("contact", "9988397522")
////                    }
//////                    payloadHelper.setReadOnlyEmail(true)
//////                    payloadHelper.setReadOnlyContact(true)
//////                    payloadHelper.setReadOnlyName(true)
//////                    payloadHelper.image = "https://www.razorpay.com"
//////                    payloadHelper.amount = 100
//////                    payloadHelper.currency = "INR"
//////                    payloadHelper.orderId = "order_XXXXXXXXXXXXXX"
////
////
////                    co.open(this@ABC, JSONObject(Gson().toJson(payloadHelper)))
//
//                } catch (e: Exception) {
//                    Toast.makeText(this@ABC, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
//                        .show()
//                    e.printStackTrace()
//                }
//            }
            val data = "[{\"record_id\":\"0\",\"diamond_clarity\":\"vvs2\",\"diamond_weight\":\"0.021\",\"diamond_shape\":\"Round\",\"diamond_color\":\"d\",\"diamond_number\":\"2\"},{\"record_id\":\"1\",\"diamond_clarity\":\"vvs2\",\"diamond_weight\":\"0.29\",\"diamond_shape\":\"Marquise\",\"diamond_color\":\"d\",\"diamond_number\":\"4\"},{\"record_id\":\"2\",\"diamond_clarity\":\"vvs2\",\"diamond_weight\":\"1.06\",\"diamond_shape\":\"Alphabet\",\"diamond_color\":\"d\",\"diamond_number\":\"1\"}]"
//            val data =
//                "[{\"userName\": \"sandeep\",\"age\":30},{\"userName\": \"vivan\",\"age\":5}]"


            val diamondItemArray : ArrayList<DiamondItem> = ArrayList()

            val diamondItem = DiamondItem(
                "",
                "Clarity",
                "Weight (Carat)",
                "Shape",
                "Color",
                "Number (Pieces)"
            )
            diamondItemArray.add(diamondItem)


            val jsonArr = JSONArray(data)
            for (i in 0..<jsonArr.length()) {
                val jsonObj = jsonArr.getJSONObject(i)
                val diamondItem = DiamondItem(
                jsonObj.getString("record_id"),
                jsonObj.getString("diamond_clarity"),
                    jsonObj.getString("diamond_weight"),
                jsonObj.getString("diamond_shape"),
                jsonObj.getString("diamond_color"),
                jsonObj.getString("diamond_number"),
                )
                diamondItemArray.add(diamondItem)
                println(jsonObj)
            }

            println("AAAAAAAAA "+diamondItemArray.size)

            recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = viewModel.diamondAdapter
            viewModel.diamondAdapter.notifyDataSetChanged()
            viewModel.diamondAdapter.submitList(diamondItemArray)




//            val jsonString = "{\"name\":\"Bob\",\"age\":25,\"city\":\"London\"}"
//            val gson = Gson()
//            try {
//                val jsonObject = gson.fromJson<JsonObject?>(jsonString, JsonObject::class.java)
//                println("Name: " + jsonObject.get("name").getAsString())
//                println("Age: " + jsonObject.get("age").getAsInt())
//            } catch (e: JsonSyntaxException) {
//                System.err.println("Error parsing JSON with Gson: " + e.message)
//            }

        }



//        viewModel.getCategories(){
//            Log.e("TAG", "getCategories "+it.toString())
//        }




    }




data class DiamondItem (
    var record_id : String,
                        var diamond_clarity : String,
                        var diamond_weight : String,
                        var diamond_shape : String,
                        var diamond_color : String,
                        var diamond_number : String)





    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        val sydney = LatLng(-34.0, 151.0)
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }




    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Log.e("TAG", "onPaymentSuccess "+p0.toString() +" ::: "+p1?.data.toString())

        Log.e("TAG", "payNamep0 " + p0.toString())
        Log.e("TAG", "paymentId " + p1!!.paymentId.toString())
        Log.e("TAG", "orderId " + p1!!.orderId.toString())
        Log.e("TAG", "signature " + p1!!.signature.toString())


//        val ddd = JSONObject(p1.toString())
//        Log.e("TAG", "onPaymentSuccessddd "+p0.toString() +" ::: "+ddd)
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.e("TAG", "onPaymentError "+p0.toString() +" ::: "+p1.toString() +" ::: "+p2?.data.toString())
    }

    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        Log.e("TAG", "onExternalWalletSelected "+p0.toString() +" ::: "+p1?.data.toString())

    }
}