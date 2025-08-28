package com.klifora.shop.models.orderDetailModel

data class ExtensionAttributes(
    val applied_taxes: List<AppliedTaxe>,
    val converting_from_quote: Boolean,
    val item_applied_taxes: List<ItemAppliedTaxe>,
    val payment_additional_info: List<PaymentAdditionalInfo>,
    val shipping_assignments: List<ShippingAssignment>
)