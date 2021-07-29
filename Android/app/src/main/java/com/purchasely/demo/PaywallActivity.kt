package com.purchasely.demo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.purchasely.ext.PLYProductViewResult
import io.purchasely.ext.Purchasely

class PaywallActivity : FragmentActivity() {

    private var purchaselyRefreshPaywall: ((refresh: Boolean) -> Unit)? = null
    private var purchaselyProcessToPayment: ((process: Boolean) -> Unit)? = null

    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paywall)

        Purchasely.setLoginTappedHandler { _, refreshPresentation ->
            purchaselyRefreshPaywall = refreshPresentation
            signIn()
        }

        Purchasely.setConfirmPurchaseHandler { _, processToPayment ->
            if(user != null) {
                processToPayment(true)
            } else {
                purchaselyProcessToPayment = processToPayment
                signIn()
            }
        }

        val fragment = Purchasely.presentationFragment { result, plan ->
            if(result == PLYProductViewResult.PURCHASED || result == PLYProductViewResult.RESTORED) {
                //do something
            }
            supportFinishAfterTransition()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "Purchasely_Paywall")
            .commit()
    }

    private fun signIn() {
        // Choose authentication providers
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            user = FirebaseAuth.getInstance().currentUser

            if(purchaselyRefreshPaywall != null) {
                purchaselyRefreshPaywall?.invoke(true)
                purchaselyRefreshPaywall = null
            }
            if(purchaselyProcessToPayment != null) {
                purchaselyProcessToPayment?.invoke(true)
                purchaselyProcessToPayment = null
            }
        }
    }

}