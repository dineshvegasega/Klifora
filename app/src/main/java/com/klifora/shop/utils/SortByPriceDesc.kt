package com.klifora.shop.utils

import com.klifora.shop.models.products.ItemProduct

class SortByPriceDesc : Comparator<ItemProduct> {
    override fun compare(o1: ItemProduct?, o2: ItemProduct?): Int {
//        return (o1!!.price - o2!!.price).toInt()
//        if(o1!!.price==o2!!.price)
//            return 0;
//        else if(o1!!.price>o2!!.price)
//            return 1;
//        else
//            return -1;


//        return if (o1!!.price > o2!!.price) 1 else (if (o1!!.price < o2!!.price) -1 else 0)
        return if (o1!!.price > o2!!.price) -1 else (if (o1!!.price < o2!!.price) 1 else 0)
    }

}