//
//  ViewController.swift
//  Purchasely-iOS-DemoFirebase
//
//  Created by Jean-François GRANG on 28/06/2021.
//

import UIKit
import Purchasely
import FirebaseAuth
import FirebaseFirestore
import FirebaseFirestoreSwift

class ViewController: UIViewController {

	@IBOutlet weak var loggedInLabel: UILabel!
	@IBOutlet weak var statusLabel: UILabel!
	@IBOutlet weak var subscribedPlanLabel: UILabel!
	@IBOutlet weak var connectButton: UIButton!
	@IBOutlet weak var logoutButton: UIButton!

	var subscriptionsListener: ListenerRegistration?

	override func viewDidLoad() {
		super.viewDidLoad()
		// Do any additional setup after loading the view.

		updateUI(connected: false)

		Auth.auth().addStateDidChangeListener { [weak self](auth, user) in
			if let user = user {
				Purchasely.userLogin(with: user.uid)
				self?.loggedInLabel.text = user.email
				self?.statusLabel.text = nil
				self?.subscribedPlanLabel.text = nil
				self?.listenToSubscriptionStateChange(user.uid)
			} else {
				Purchasely.userLogout()
			}
			self?.updateUI(connected: user != nil)
		}
	}

	@IBAction func logout(_ sender: Any) {
		try? Auth.auth().signOut()
	}

	@IBAction func purchase(_ sender: Any) {
		let ctrl = Purchasely.presentationController()
		present(ctrl, animated: true, completion: nil)
	}

	func updateUI(connected: Bool) {
		connectButton.isHidden = connected
		loggedInLabel.isHidden = !connected
		logoutButton.isHidden = !connected
		statusLabel.isHidden = !connected
		subscribedPlanLabel.isHidden = !connected
	}

	func listenToSubscriptionStateChange(_ uid: String) {
		subscriptionsListener?.remove()
		subscriptionsListener = Firestore.firestore().collection("PurchaselySubscriptions").whereField("user.vendor_id", isEqualTo: uid).whereField("is_subscribed", isEqualTo: true).addSnapshotListener() { [weak self](snapshot, error) in
			guard error == nil else { return }

			// Mapping firestor
			guard let subscriptions = snapshot?.documents.compactMap({ doc in try? doc.data(as: Subscription.self) }).filter({ $0.isSubscribed }) else {
				// Handle error
				return
			}

			self?.statusLabel.text = subscriptions.isEmpty ? "Unsubscribed" : "Subscribed"
			self?.subscribedPlanLabel.text = subscriptions.map({ $0.plan.vendorId }).joined(separator: "\n")
		}
	}
}
