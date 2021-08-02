//
//  LoginViewController.swift
//  Purchasely-iOS-DemoFirebase
//
//  Created by Jean-François GRANG on 28/06/2021.
//

import UIKit
import Firebase

class LoginViewController: UIViewController {

	@IBOutlet weak var emailLabel: UITextField!
	@IBOutlet weak var passwordLabel: UITextField!
	@IBOutlet weak var messageLabel: UILabel!

	override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

	// ****************************************************************************
	// MARK: - UI Actions

	@IBAction func login(_ sender: Any) {
		guard let (email, password) = checkForm() else { return }

		Auth.auth().signIn(withEmail: email, password: password) { [weak self](result, error) in
			guard error == nil else {
				self?.messageLabel.text = error?.localizedDescription
				return
			}
			self?.dismiss(animated: true, completion: nil)
		}
	}

	@IBAction func register(_ sender: Any) {
		guard let (email, password) = checkForm() else { return }

		Auth.auth().createUser(withEmail: email, password: password) { [weak self](result, error) in
			guard error == nil else {
				self?.messageLabel.text = error?.localizedDescription
				return
			}
			self?.dismiss(animated: true, completion: nil)
		}
	}

	// ****************************************************************************
	// MARK: - Form management

	func checkForm() -> (String, String)? {
		guard let email = emailLabel.text, !email.isEmpty,
			  let password = passwordLabel.text, !password.isEmpty else {
			messageLabel.text = "Email and password must be filled"
			return nil
		}
		messageLabel.text = nil
		return (email, password)
	}
}
