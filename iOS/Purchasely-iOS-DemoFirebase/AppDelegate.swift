//
//  AppDelegate.swift
//  Purchasely-iOS-DemoFirebase
//
//  Created by Jean-François GRANG on 28/06/2021.
//

import UIKit
import Firebase
import Purchasely

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

	func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
		// Override point for customization after application launch.

		// Use Firebase library to configure APIs
		FirebaseApp.configure()
		Purchasely.start(withAPIKey: "YOUR_API_KEY")

		Auth.auth().addStateDidChangeListener { (auth, user) in
			if let user = user {
				Purchasely.userLogin(with: user.uid)
			} else {
				Purchasely.userLogout()
			}
		}

		return true
	}

	// MARK: UISceneSession Lifecycle

	func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
		// Called when a new scene session is being created.
		// Use this method to select a configuration to create the new scene with.
		return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
	}

	func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
		// Called when the user discards a scene session.
		// If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
		// Use this method to release any resources that were specific to the discarded scenes, as they will not return.
	}


}

