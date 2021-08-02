# Purchasely-iOS-DemoFirebase

Implementing subscriptions with Purchasely and Firebase backend using Purchasely's Firebase Extension.

To run this example you will have to 
1. Install the dependecies
2. Configure Google Services
3. Set Purchasely's API Key
4. Set your own App Id and certificates

## 1. Install the dependencies

This example is using Cocoapods as a dependency management tool. Purchasley also supports Swift PM and Carthage.

To install the required dependencies, make sure you have Cocoapods and run `pod install`.

## 2. Configure Google Services

1. Open [Firebase console](https://console.firebase.google.com)
2. Go to the settings of your iOS project
3. Download `GoogleService-Info.plist` file
4. Add the file to the project next to the `Info.plist`

## 3. Set Purchasely's API Key

1. From [Purchasely console](https://console.purchasely.io), go to your app and *App settings*
2. Select *Backend & SDK configuration*
3. Copy your *Api Key*
4. In the `AppDelegate.swift` file, paste your *Api Key* in `Purchasely.start(withAPIKey: "YOUR_API_KEY")`
 

## 4. Set your own App Id and certificates
 
You won't be able to process to payment 
In the project, don't forget to:

- Set the bundle identifier to the one of your app
- Set the appropriate Certificates or Team in the *Signing & Capabilities*

You can now run the code example on your device.

## Add it to tour project

Now you want to add Purchaley inside your app.

### Add a state listener

Whenever the user changes, notify Purchasely that the user logged in.

```
Auth.auth().addStateDidChangeListener { [weak self](auth, user) in
	if let user = user {
		Purchasely.userLogin(with: user.uid)
	} else {
		Purchasely.userLogout()
	}
}
```

### Update the UI when the user state changes

If you want to unlock a content or feature when the subscription is active, you need to listen to subscription changes.
You can do that with the following code inside your controller.

```
var snapshotListener: ListenerRegistration?

...

snapshotListener?.remove()
snapshotListener = Firestore.firestore().collection("PurchaselySubscriptions").whereField("user.vendor_id", isEqualTo: uid).whereField("is_subscribed", isEqualTo: true).addSnapshotListener() { [weak self](snapshot, error) in
	guard error == nil else { return }

	// Get the subscriptions (if you want to have the details)
	guard let subscriptions = snapshot?.documents.compactMap({ doc in try? doc.data(as: Subscription.self) }).filter({ $0.isSubscribed }) else {
		// Handle the error
		return
	}

	self?.statusLabel.text = subscriptions.isEmpty ? "Unsubscribed" : "Subscribed"
	self?.subscribedPlanLabel.text = subscriptions.map({ $0.plan.vendorId }).joined(separator: "\n")
}
```
