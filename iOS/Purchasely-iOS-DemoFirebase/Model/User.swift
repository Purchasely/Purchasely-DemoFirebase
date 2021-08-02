//
//  User.swift
//  Purchasely-iOS-DemoFirebase
//
//  Created by Jean-François GRANG on 29/06/2021.
//

import Foundation

struct User: Decodable {
	let vendorId: String

	enum CodingKeys: String, CodingKey {
		case vendorId = "vendor_id"
	}
}
