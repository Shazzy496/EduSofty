package com.sharon.edusoft.DarajaMpesa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.DarajaListener
import com.androidstudy.daraja.model.AccessToken
import com.androidstudy.daraja.model.LNMExpress
import com.androidstudy.daraja.model.LNMResult
import com.androidstudy.daraja.util.Env
import com.androidstudy.daraja.util.TransactionType
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.sharon.edusoft.Home.Categories.CategoriesActivity
import com.sharon.edusoft.R
import com.sharon.edusoft.SetbookPdf.SetbookPdf
import kotlinx.android.synthetic.main.activity_mpesa.*

@Suppress("UNREACHABLE_CODE")
class MpesaActivity : AppCompatActivity(), MpesaListener{

    lateinit var daraja: Daraja
    lateinit var userList:
            MutableList<UserDetails>
    lateinit var editName: EditText
    lateinit var submit: Button
    private lateinit var identityStatus:String
    var phone_No = ""
    val TAG = "MpesaActivity"

    private var user: FirebaseUser? = null

    private var ref: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mMessageListener: ValueEventListener? = null
    companion object {
        lateinit var mpesaListener: MpesaListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpesa)
        identityStatus=intent!!.getStringExtra("identity")!!.toString()
        initialise()




    }

    private fun initialise() {

        mDatabase = FirebaseDatabase.getInstance()
        ref = mDatabase!!.reference.child("Successful Users")
        mAuth = FirebaseAuth.getInstance()
        val phone: EditText = findViewById(R.id.phone)
        FirebaseApp.initializeApp(this)
        mpesaListener = this

        daraja = Daraja.with(
                "eITkxCYuFsr9ACYMtjzjO6MZ7LqJyoFX",
                "WLEIRHxK4ffRpmYT",
                Env.SANDBOX, //for Test use Env.PRODUCTION when in production
                object : DarajaListener<AccessToken> {
                    override fun onResult(accessToken: AccessToken) {

                        Toast.makeText(
                                this@MpesaActivity,
                                "MPESA TOKEN : ${accessToken.access_token}",
                                Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: String) {

                    }
                })


        button.setOnClickListener {
            val phoneNumber = phone.text.trim().toString().trim()
            if (phoneNumber.isEmpty()) {
                phone.error = "Please enter your phone Number"
                return@setOnClickListener

            }
            val lnmExpress = LNMExpress(
                    "174379",
                    "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
                    TransactionType.CustomerPayBillOnline,
                    "1",
                    phoneNumber,
                    "174379",
                    phoneNumber,
                    "https://us-central1-edusoft-1b8b7.cloudfunctions.net/api/myCallbackUrl",
                    "001ABC",
                    "Goods Payment"
            )

            daraja.requestMPESAExpress(lnmExpress,
                    object : DarajaListener<LNMResult> {
                        override fun onResult(lnmResult: LNMResult) {

                            FirebaseMessaging.getInstance()
                                    .subscribeToTopic(lnmResult.CheckoutRequestID.toString())

                            Toast.makeText(
                                    this@MpesaActivity,
                                    "Response here ${lnmResult.ResponseDescription}",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onError(error: String) {

                            Toast.makeText(
                                    this@MpesaActivity,
                                    "Error here $error",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            )
            phone_No = "254" + phoneNumber.substring(1)


        }

    }

    override fun sendSuccesfull(amount: String, phone: String, date: String, receipt: String) {
        runOnUiThread {

            button.isClickable=false
            preference.setVisibility(View.VISIBLE)
            if (identityStatus.equals("VideoFeeds")) {
                val user=mAuth!!.currentUser
                val id=user!!.uid
                val ref = FirebaseDatabase.getInstance().getReference("SuccessFul Users")
                val mpesa = UserDetails(id,amount, phone, date,receipt)

                    ref.child(receipt).setValue(mpesa).addOnCompleteListener {
                        Toast.makeText(applicationContext, "Phone Number saved successfully", Toast.LENGTH_SHORT).show()
                    }
                val intent=Intent(this,CategoriesActivity::class.java)
                startActivity(intent)
                finish()
//                        val fragment=CategoriesFragment()
//                        val fragmentManager=supportFragmentManager
//                                .beginTransaction()
//                                fragmentManager.replace(R.id.categoryFragment,fragment)
//                                fragmentManager.disallowAddToBackStack()
//                                fragmentManager.commit()


//                Toast.makeText(
//                        this, "Payment Succesfull\n" +
//                        "Receipt: $receipt\n" +
//                        "Date: $date\n" +
//                        "Phone: $phone\n" +
//                        "Amount: $amount", Toast.LENGTH_LONG
//                ).show()
//
//                Log.d("data", "$phone, $amount,$receipt,$date")
            }else if (identityStatus.equals("SetBookMpesa")){
                val user=mAuth!!.currentUser
                val id=user!!.uid
                val reference = FirebaseDatabase.getInstance().getReference("UserPdf transactions")
                val mpesaUsers=UserDetails(id,amount, phone, date,receipt)
                Log.d("Dataaa",mpesaUsers.toString())
                reference.child(receipt).setValue(mpesaUsers).addOnCompleteListener{
                    Toast.makeText(applicationContext, "Phone Number saved successfully", Toast.LENGTH_SHORT).show()
                }
                val intent=Intent(this,SetbookPdf::class.java)
                startActivity(intent)
                finish()
            } else{
                Toast.makeText(this,"Payment wasnt successful",Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun sendFailed(reason: String) {
        runOnUiThread {
            Toast.makeText(
                    this, "Payment Failed\n" +
                    "Reason: $reason"
                    , Toast.LENGTH_LONG
            ).show()
        }
    }

}