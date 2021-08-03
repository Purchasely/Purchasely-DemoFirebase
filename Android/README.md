# Purchasely-Android-DemoFirebase

Implementing subscriptions with Purchasely and Firebase backend using Purchasely's Firebase Extension.

To run this example you will have to 
1. Install the dependecies
2. Configure Google Services
3. Set Purchasely's API Key
4. Set your own App Id and certificates

## 1. Install the dependencies

This example is using Gradle as a dependency management tool.
Please refer to our [documentation](https://docs.purchasely.com/quick-start/sdk-installation/quick-start) to add required Purchasely dependencies to your project.

## 2. Configure Google Services

1. Open [Firebase console](https://console.firebase.google.com)
2. Go to the settings of your Android project
3. Download `google-services.json` file
4. Add the file to the project in your application module next to the `build.gradle` file.

## 3. Set Purchasely's API Key

1. From [Purchasely console](https://console.purchasely.io), go to your app and *App settings*
2. Select *Backend & SDK configuration*
3. Copy your *Api Key*
4. In an Activity of your project, paste your *Api Key* and setup the store your want to use.
  ```kotlin
  Purchasely.Builder(applicationContext)
            .apiKey("your_api_key")
            .stores(listOf(GoogleStore()))
            .build()
  ```
 

## 4. Set your own App Id and certificates
 
You won't be able to process to payment right away.
In the project, don't forget to:

- Set the package name to the one of your app

You can now run the code example on your device.

## Add it to tour project

Now you want to add Purchaley inside your app.

### Add a state listener

Whenever the user changes, notify Purchasely that the user logged in.

```kotlin
private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    if (result.resultCode == RESULT_OK) {
        // Successfully signed in
        Purchasely.userLogin(FirebaseAuth.getInstance().currentUser.uid)
    } else {
        Purchasely.userLogout()
    }
}
```

### Update the UI when the user state changes

If you want to unlock a content or feature when the subscription is active, you need to listen to subscription changes.
You can do that with the following code inside your controller.

```kotlin
Firebase.firestore.collection("PurchaselySubscriptions")
            .whereEqualTo("user.vendor_id", user.uid)
            .whereEqualTo("is_subscribed", true)
            .addSnapshotListener { value, error ->
                val subscriptions = value?.documents?.map { it.data }
                                          ?.filter { it?.get("is_subscribed") == true }

                val isSubscribed = subscriptions?.isEmpty()?.not() ?: false
            }
```
