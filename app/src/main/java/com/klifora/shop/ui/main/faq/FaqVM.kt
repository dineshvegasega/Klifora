package com.klifora.shop.ui.main.faq

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemFaqBinding
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.ItemFaq
import com.klifora.shop.networking.Repository
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaqVM @Inject constructor(private val repository: Repository) : ViewModel() {

    var item1: ArrayList<ItemFaq> = ArrayList()

    init {
        item1.add(
            ItemFaq(
                que = "After I choose a franchise model. Do I have to make 100% payment in advance?",
                ans = "No. 50% advance and balance 50% to be paid against delivery."
            )
        )
        item1.add(
            ItemFaq(
                que = "Do The goods have to be sold on MRP?",
                ans = "Ideally yes. But if you want to give a discount from your profit. You can."
            )
        )
        item1.add(
            ItemFaq(
                que = "What is the ROI?",
                ans = "On a sale of 10 lacs. You earn lacs. This means you earn a profit of 30% margin. The higher the sales. The higher the margins."
            )
        )
        item1.add(
            ItemFaq(
                que = "Will we be given material to display the jewellery?",
                ans = "Yes. You will be given a specially designed briefcase to store n display the jewellery.In addition to that. You will be provided a tablet to show case the designs."
            )
        )
        item1.add(
            ItemFaq(
                que = "Will we be allowed to choose the designs?",
                ans = "Yes. It will be a joint effort between the Klifora team and you catering to the local taste."
            )
        )
        item1.add(
            ItemFaq(
                que = "The goods which are unsold . What will happen to them?",
                ans = "We will help you sell them at a discount through exhibitions. Promotions etc."
            )
        )
        item1.add(
            ItemFaq(
                que = "The goods will be in hall marked gold?",
                ans = "Yes. You can choose whether you want stock in 18 carat or 14 carat according to your market."
            )
        )
        item1.add(
            ItemFaq(
                que = "Will the break up of each piece be transparent?",
                ans = "Yes. Gold. Labour n diamond value will be transparent on the app."
            )
        )
        item1.add(
            ItemFaq(
                que = "How will I reorder and order the goods?",
                ans = "You will be provided an app through which you can order. Track your order. Reorder. Track previous bought goods. Highest selling etc."
            )
        )
        item1.add(
            ItemFaq(
                que = "How much is the franchise fee?",
                ans = "The franchise fee is 1.5 lacs."
            )
        )
        item1.add(
            ItemFaq(
                que = "After my margin of 30%. Will I be competitive?",
                ans = "Yes. You can compare the prices online of goods being sold to the customers. After you make your 30% margin. You will still be more competitive."
            )
        )
        item1.add(
            ItemFaq(
                que = "Can I give guarantee to the customer for the product and what is the return policy?",
                ans = "You can give cash back guarantee to the customer after deducting 20% on the current value."
            )
        )
        item1.add(
            ItemFaq(
                que = "Apart from the min investment mentioned. What is the criteria of getting Klifora franchisee?",
                ans = "You have to be socially well connected and if you are not but can invest. You must join hands with a person who is well connected and give us a list of minimum 100 people that you might know including family ,friends. ,colleagues and neighbours."
            )
        )
        item1.add(
            ItemFaq(
                que = "How do I trust you to give 50% advance payment?",
                ans = "You can visit us in our office in south ex delhi. You can run a background check on all the 3 promoters and then take a decision."
            )
        )
        item1.add(
            ItemFaq(
                que = "In how many days will I get the goods after paying?",
                ans = "You will get the goods in 20-25 days. Meanwhile Klifora team will be training you in the product, customer management. Lab grown diamonds. Difference between natural n lab grown diamond. Quality of the diamond and calculation of pricing in terms of diamond n gold and Marketing ideas etc."
            )
        )
        item1.add(
            ItemFaq(
                que = "Incase I want to discontinue. How do I do that?",
                ans = "There will be a lock period of 1 year. However with mutual discussion.We can resolve to mutual benefit."
            )
        )
        item1.add(
            ItemFaq(
                que = "If a customer wants to place order of her design?",
                ans = "No problem. You have to upload the design on the app and We will place the order to our factory in Surat."
            )
        )
        item1.add(
            ItemFaq(
                que = "Can I extend credit to my customer?",
                ans = "With 2 decades of experience behind us. Don’t ever do that as the customer will wear the jewellery. Show to other jewellers and ultimately not pay you. All the big brands in India never offer credit."
            )
        )
        item1.add(
            ItemFaq(
                que = "Will the purchase be through the bill from franchisee to Klifora?",
                ans = "Yes."
            )
        )
        item1.add(
            ItemFaq(
                que = "Do I require to register a company?",
                ans = "Yes. It can be in any form but a company with a gst number is required and we will help you register."
            )
        )
    }


    fun getCartCount(callBack: Int.() -> Unit) {
        viewModelScope.launch {
            val userList: List<CartModel>? = db?.cartDao()?.getAll()
            var countBadge = 0
            userList?.forEach {
                countBadge += it.quantity
            }
            callBack(countBadge)
        }
    }


    var selectedPosition = -1
    val faqAdapter = object : GenericAdapter<ItemFaqBinding, ItemFaq>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int,
        ) = ItemFaqBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemFaqBinding,
            dataClass: ItemFaq,
            position: Int,
        ) {
            binding.apply {
//                textDesc.visibility =
//                    if (dataClass.isCollapse == true) View.VISIBLE else View.GONE
//                ivHideShow.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        root.context,
//                        if (dataClass.isCollapse == true) R.drawable.arrow_down else R.drawable.arrow_right
//                    )
//                )

                textDesc.visibility =
                    if (selectedPosition == position) View.VISIBLE else View.GONE
                ivHideShow.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        if (selectedPosition == position) R.drawable.arrow_down else R.drawable.arrow_right
                    )
                )

                textItem.text = dataClass.que
                textDesc.text = dataClass.ans

                root.singleClick {
                    if (selectedPosition == position) {
                        if (textDesc.isVisible == true) {
                            selectedPosition = -1
                        }
                        if (textDesc.isVisible == false) {
                            selectedPosition = position
                        }
                    } else {
                        selectedPosition = position
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }


}