package com.embot.testingcourse.core.mothers

import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.productList.domain.model.Product

object ProductMother {

    fun bread(stock: Int = 8): Product = product {
        withId("id-bread")
        withName("Pan")
        withDescription("Hot")
        withCategory("bread")
        withPrice(2.50)
        withStock(stock)
    }

    fun milk(stock: Int = 3): Product = product {
        withId("id-milk")
        withName("Milk")
        withDescription("Whole")
        withCategory("dairy")
        withPrice(1.50)
        withStock(stock)
    }

    fun coffee(stock: Int = 2): Product = product {
        withId("id-coffee")
        withName("Coffee")
        withDescription("American")
        withCategory("drinks")
        withPrice(4.50)
        withStock(stock)
    }

}