//
//  Subscription.swift
//  Purchasely-iOS-DemoFirebase
//
//  Created by Jean-François GRANG on 29/06/2021.
//

import Foundation

struct Subscription: Decodable {
	let id: String
	let isSubscribed: Bool
	let user: User
	let product: Product
	let plan: Plan

	init(from decoder: Decoder) throws {
		let container = try decoder.container(keyedBy: CodingKeys.self)
		self.id = try container.decode(String.self, forKey: .id)
		self.isSubscribed = try container.decode(Bool.self, forKey: .isSubscribed)
		self.user = try container.decode(User.self, forKey: .user)

		let propertiesContainer = try container.nestedContainer(keyedBy: PropertiesCodingKeys.self, forKey: .properties)
		self.product = try propertiesContainer.decode(Product.self, forKey: .product)

		let productContainer = try propertiesContainer.nestedContainer(keyedBy: ProductCodingKeys.self, forKey: .product)
		self.plan = try productContainer.decode(Plan.self, forKey: .plan)
	}

	enum CodingKeys: String, CodingKey {
		case id
		case isSubscribed = "is_subscribed"
		case user
		case properties
	}

	enum PropertiesCodingKeys: String, CodingKey {
		case product
	}

	enum ProductCodingKeys: String, CodingKey {
		case plan
	}
}
