package com.embot.testingcourse.core.testing

object UiTestTag {

    // TOP APP BAR
    const val SETTINGS_TOP_APP_BAR_BACK = "SETTINGS_TOP_APP_BAR_BACK"
    const val TOP_APP_BAR_BADGE = "TOP_APP_BAR_BADGE"
    const val TOP_APP_BAR_BADGE_FILTER = "TOP_APP_BAR_BADGE_FILTER"
    const val TOP_APP_BAR_SETTINGS = "TOP_APP_BAR_SSTTINGS_ICON"
    const val TOP_APP_BAR_CART = "TOP_APP_BAR_CART_ICON"
    const val FILTER_VIEW = "FILTER_VIEW"

    //SETTINGS
    const val SETTINGS_SCREEN_CONTENT = "SETTINGS_SCREEN_CONTENT"
    const val SETTINGS_IN_STOCK_SWITCH = "SETTINGS_IN_STOCK_SWITCH"
    const val SETTINGS_TAX_SWITCH = "SETTINGS_TAX_SWITCH"

    fun settingsThemeOption(themeModeName: String) = "settings_theme_${themeModeName.lowercase()}"

    //PRODUCT LIST
    const val PRODUCT_LIST_LOADING = "PRODUCT_LIST_LOADING"
    const val PRODUCT_LIST_LIST = "PRODUCT_LIST_LIST"

    fun productListItem(productId: String) = "product_list_item_$productId"
    fun productListCategory(category: String?) = "product_list_category_${category ?: "add"}"
}