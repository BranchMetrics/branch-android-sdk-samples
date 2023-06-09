package com.example.branchandroidtestsampleapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import io.branch.referral.Branch
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.BranchError
import io.branch.referral.QRCode.BranchQRCode
import io.branch.referral.SharingHelper
import io.branch.referral.util.*
import org.w3c.dom.Text

import java.util.*

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        // ---------- Initialize Branch Session on App Open ----------

        Branch.sessionBuilder(this).withCallback { branchUniversalObject, linkProperties, error ->
            if (error != null) {
                Log.e(TAG, "branch init failed. Caused by -" + error.message)
            } else {
                Log.i(TAG, "branch init complete!")
                if (branchUniversalObject != null) {
                    Log.i(TAG, "title " + branchUniversalObject.title)
                    Log.i(TAG, "CanonicalIdentifier " + branchUniversalObject.canonicalIdentifier)
                    Log.i(TAG, "metadata " + branchUniversalObject.contentMetadata.convertToJson())
                }
                if (linkProperties != null) {
                    Log.i(TAG, "Channel " + linkProperties.channel)
                    Log.i(TAG, "control params " + linkProperties.controlParams)
                    Log.i(TAG,
                        linkProperties.controlParams["\$og_title"].toString()
                    )
                    // ---------- Intra App Linking Using Custom $deeplink_path ----------
                    // ---------- Intra-app linking (i.e. session reinitialization) requires an intent flag, "branch_force_new_session". ----------
                    if (linkProperties.controlParams["\$deeplink_path"].toString() == "color block page") {
                        val intent = Intent(this, ColorBlockPage::class.java)
                        intent.putExtra("branch_force_new_session", true)
                        startActivity(intent)
                    }
                    else if (linkProperties.controlParams["\$deeplink_path"].toString() == "read deep link page") {
                        val intent = Intent(this, ReadDeepLinkActivity::class.java)
                        intent.putExtra("branch_force_new_session", true)
                        startActivity(intent)
                    }
                }
            }
        }.withData(this.intent.data).init()

        createBranchLink()

        // ---------- Share Deep Link Button ----------

        val shareButton = findViewById<ImageButton>(R.id.shareLinkButton)
        shareButton.setOnClickListener {
            shareBranchLink()
        }

        // ---------- Create QR Code Button ----------
        val createQRCodeButton = findViewById<ImageButton>(R.id.qrCodeIconImage)
        createQRCodeButton.setOnClickListener {
            createQRCode()
        }

        // ---------- Read Deep Link Button ----------
        val readDeepLinkButton = findViewById<Button>(R.id.readDeepLinkButton)
        readDeepLinkButton.setOnClickListener {
            val readDeepLinkPageIntent = Intent(this, ReadDeepLinkActivity::class.java)
            readDeepLinkPageIntent.putExtra("branch_force_new_session", true)
            startActivity(readDeepLinkPageIntent)
        }

        // ---------- Branch Event Buttons ----------

        val customEventButton = findViewById<Button>(R.id.purchaseStandardEventButton);
        customEventButton.setOnClickListener {
            createCustomEvent()
        }

        val commerceEventButton = findViewById<Button>(R.id.commerceEventButton)
        commerceEventButton.setOnClickListener {
            createCommerceEvent()
        }

        // ---------- ViewModel Modifications ----------

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        val branchBadge = findViewById<ImageView>(R.id.branchBadgeDark)

        viewModel.displayLightBadge.observe(this, androidx.lifecycle.Observer {
            if (it) {
                branchBadge.setImageResource(R.drawable.branchbadgelightlarger)
            } else {
                branchBadge.setImageResource(R.drawable.branchcopybadgelarger)
            }
        })

        val changeBadgeEventButton = findViewById<Button>(R.id.changeBranchBadgeButton)
        changeBadgeEventButton.setOnClickListener {
            // Just toggle a boolean
            Log.i(TAG, "ChangeBadge onClick called " + viewModel.displayLightBadge.value)
            viewModel.displayLightBadge.postValue(!viewModel.displayLightBadge.value!!)

            changeBadgeEvent()
        }

        // ---------- Open Color Block Page Manually ----------
        val colorBlockButton = findViewById<Button>(R.id.colorBlockPageButton)
        colorBlockButton.setOnClickListener {
            val colorBlockIntent = Intent(this, ColorBlockPage::class.java)
            startActivity(colorBlockIntent)
        }
    }

    // ---------- Initialize New Branch Session ----------
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Branch.sessionBuilder(this).withCallback { referringParams, error ->
            if (error != null) {
                Log.e(TAG, error.message)
            } else if (referringParams != null) {
                Log.i(TAG, referringParams.toString())
                Log.i(TAG, "--- SDK LOGS ABOVE ---")
                if (referringParams.has("\$deeplink_path")) {
                    if (referringParams["\$deeplink_path"] == "color block page") {
                        val routingPageIntent = Intent(this, ColorBlockPage::class.java)
                        routingPageIntent.putExtra("branch_force_new_session", true)
                        startActivity(routingPageIntent)
                    }
                    else if (referringParams["\$deeplink_path"] == "read deep link page") {
                        val routingPageIntent = Intent(this, ReadDeepLinkActivity::class.java)
                        routingPageIntent.putExtra("branch_force_new_session", true)
                        startActivity(routingPageIntent)
                    }
                }
            }
        }.reInit()
    }

    // ---------- Create Branch Link ----------

    val buo = BranchUniversalObject()
        .setTitle("Title Link")
        .setContentDescription("Link created using the Branch SDK")

   private val branchLinkTest: TextView by lazy {findViewById(R.id.generatedLink)}

    private fun createBranchLink() {
        val lp = LinkProperties()
            .setCampaign("Sample Test App Campaign Example")
            .setChannel("Sample Test App Marketing")
            .setFeature("sharing")
            .addControlParameter("\$desktop_url", "https://example.com/home")
            .addControlParameter("\$deeplink_path", "color block page")
            .addControlParameter("blockColor", "Yellow")

        buo.generateShortUrl(this, lp, Branch.BranchLinkCreateListener { url, error ->
            if (error == null) {
                Log.i(TAG, "Branch Link to share: $url")
                branchLinkTest.updateLayoutParams {
                    width = WRAP_CONTENT
                    height = WRAP_CONTENT
                }
                branchLinkTest.text = url.toString();
            }
        })
    }

    // ---------- Share Branch Link ----------

    private fun shareBranchLink() {
        val lp = LinkProperties()
            .setChannel("facebook")
            .setFeature("sharing")
            .setCampaign("content 123 launch")
            .setStage("new user")

            // $deeplink_path routes users to a specific Activity. Uncomment one of the below to route to the specified page.
            .addControlParameter("\$deeplink_path", "color block page")
            //.addControlParameter("\$deeplink_path", "read deep link page")


            // You can set the 'blockColor' parameter to 'Blue', 'Yellow', 'Red', 'Green' or 'White' to modify the color block page.
            .addControlParameter("blockColor", "Green")

            .addControlParameter("\$desktop_url", "https://example.com/home")
            .addControlParameter("custom", "data")
            .addControlParameter("custom_random", Calendar.getInstance().timeInMillis.toString())

        val ss = ShareSheetStyle(this@MainActivity, "Check this out!", "This stuff is awesome: ")
            .setCopyUrlStyle(resources.getDrawable(androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha), "Copy", "Added to clipboard")
            .setMoreOptionStyle(resources.getDrawable(androidx.appcompat.R.drawable.abc_ic_search_api_material), "Show more")
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
            .setAsFullWidthStyle(true)
            .setSharingTitle("Share With")

        buo.showShareSheet(this, lp, ss, object : Branch.BranchLinkShareListener {
            override fun onShareLinkDialogLaunched() {}
            override fun onShareLinkDialogDismissed() {}
            override fun onLinkShareResponse(sharedLink: String?, sharedChannel: String?, error: BranchError?) {}
            override fun onChannelSelected(channelName: String) {}
        })
    }

    // ---------- Create QR Code ----------

    // This code dims the area around the QR code image
    fun PopupWindow.dimBehind() {
        val container = contentView.rootView
        val context = contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.7f
        wm.updateViewLayout(container, p)
    }


    private fun createQRCode() {

        val qrCode = BranchQRCode()
            .setCodeColor("#050E3C")
            .setBackgroundColor(Color.WHITE)
            .setMargin(1)
            .setWidth(512)
            .setImageFormat(BranchQRCode.BranchImageFormat.JPEG)
            .setCenterLogo("https://i.snipboard.io/5PW62T.jpg")

        val qrCodeLinkProperties = LinkProperties()
            .setChannel("QR Code Creator Channel")
            .setCampaign("QR Code Campaign")


        qrCode.getQRCodeAsImage(this@MainActivity, buo, qrCodeLinkProperties, object :
            BranchQRCode.BranchQRCodeImageHandler<Any?> {
            override fun onSuccess(qrCodeImage: Bitmap) {

                // Make the QR Code a Popup Window
                val qrCodeView = layoutInflater.inflate(R.layout.qr_code_popup_window, null)
                val qrCodeWindow = PopupWindow(qrCodeView, WRAP_CONTENT, WRAP_CONTENT, true)
                qrCodeWindow.contentView = qrCodeView
                qrCodeWindow.showAtLocation(qrCodeView, Gravity.CENTER, 0, 0)
                qrCodeWindow.dimBehind()

                // Set ImageView to created QR Code
                val qrCodeImageView = qrCodeView.findViewById<ImageView>(R.id.qrCodePopupImageView)
                qrCodeImageView.setImageBitmap(qrCodeImage)
                qrCodeImageView.setOnClickListener {
                    qrCodeWindow.dismiss()
                }
            }
            override fun onFailure(e: Exception) {
                Log.e(TAG, e.toString())
            }
        })
    }

    private fun createCustomEvent() {
            BranchEvent("MY_CUSTOM_PURCHASE").logEvent(this)
        Toast.makeText(this, "'MY_CUSTOM_PURCHASE' Custom Event sent!", Toast.LENGTH_SHORT).show()
        Log.i(TAG,"'MY_CUSTOM_PURCHASE' Custom Event sent!")
    }

    private fun createCommerceEvent() {
        BranchEvent(BRANCH_STANDARD_EVENT.ADD_TO_CART).logEvent(this)
        Toast.makeText(this, "'ADD_TO_CARD' Commerce Event sent!", Toast.LENGTH_SHORT).show()
        Log.i(TAG,"'ADD_TO_CART' Commerce Event sent!")
    }

    private fun changeBadgeEvent() {
        BranchEvent("CHANGE_BADGE").logEvent(this)
        Toast.makeText(this, "'Change Badge' Custom Event sent!", Toast.LENGTH_SHORT).show()
        Log.i(TAG,"'Change Badge' Custom Event sent!")
    }
}