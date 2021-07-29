package com.purchasely.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.purchasely.demo.ui.theme.FirebaseDemoTheme
import io.purchasely.ext.Purchasely
import io.purchasely.google.GoogleStore

class MainActivity : ComponentActivity() {

    private var isSubscribed = MutableLiveData(false)
    private var user = MutableLiveData<FirebaseUser?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Purchasely.Builder(applicationContext)
            .apiKey("your_api_key")
            .stores(listOf(GoogleStore()))
            .isReadyToPurchase(true)
            .build()

        user.observe(this) {
            if(it != null)  onUserFetched(it)
            else            Purchasely.userLogout()
            reload()
        }

        isSubscribed.observe(this) {
            reload()
        }

        Purchasely.start()

        reload()
    }

    override fun onResume() {
        super.onResume()
        user.value = FirebaseAuth.getInstance().currentUser
    }

    private fun reload() {
        setContent {
            FirebaseDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Greeting(user.value?.displayName ?: "Anonymous")
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { displayPresentation() }) {
                            Text(text = "Display Presentation")
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Subscription active ? ${isSubscribed.value}")
                        Spacer(modifier = Modifier.weight(1f, fill = true))
                        if(user.value == null) {
                            Button(onClick = { signIn() }) {
                                Text(text = "Sign in")
                            }
                        } else {
                            Button(onClick = { signOut() }) {
                                Text(text = "Sign out")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun displayPresentation() {
        startActivity(Intent(applicationContext, PaywallActivity::class.java))
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Purchasely.userLogout()
        user.value = null
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
            user.value = FirebaseAuth.getInstance().currentUser
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun onUserFetched(user: FirebaseUser) {
        Purchasely.userLogin(user.uid)

        Firebase.firestore.collection("PurchaselySubscriptions")
            .whereEqualTo("user.vendor_id", user.uid)
            .whereEqualTo("is_subscribed", true)
            .addSnapshotListener { value, error ->
                parseSubscriptions(value)
            }
    }

    private fun parseSubscriptions(value: QuerySnapshot?) {
        val subscriptions =
            value?.documents?.map { it.data }?.filter { it?.get("is_subscribed") == true }

        isSubscribed.value = subscriptions?.isEmpty()?.not() ?: false
        Log.d("Purchasely", "isSubscribed ? ${subscriptions?.isEmpty()?.not()}")
        Log.d("Purchasely", "Subscriptions: ${subscriptions}")
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FirebaseDemoTheme {
        Greeting("Android")
    }
}