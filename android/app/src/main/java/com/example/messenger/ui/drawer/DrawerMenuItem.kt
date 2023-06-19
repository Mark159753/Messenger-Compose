package com.example.messenger.ui.drawer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.messenger.R
import com.example.messenger.Screen

data class DrawerMenuItem(
    @StringRes
    val title:Int,
    @DrawableRes
    val icon:Int,
    @StringRes
    val descriptionId:Int,
    val destination:DrawerDestination
)

data class DrawerHeaderData(
    val name:String,
    val avatar:String,
    val email:String
)

sealed interface DrawerDestination{
    object OnLogoutDialog:DrawerDestination
    data class NavScreen(val screen: Screen):DrawerDestination
}

object DrawerParams {
    val drawerButtons = arrayListOf(
        DrawerMenuItem(
            title = R.string.drawer_menu_contacts,
            icon = R.drawable.contacts_icon,
            descriptionId = R.string.drawer_menu_contacts,
            destination = DrawerDestination.NavScreen(Screen.Home)
        ),
        DrawerMenuItem(
            title = R.string.drawer_menu_people_nearby,
            icon = R.drawable.people_nearby_icon,
            descriptionId = R.string.drawer_menu_people_nearby,
            destination = DrawerDestination.NavScreen(Screen.Home)
        ),
        DrawerMenuItem(
            title = R.string.drawer_menu_settings,
            icon = R.drawable.settings_icon,
            descriptionId = R.string.drawer_menu_settings,
            destination = DrawerDestination.NavScreen(Screen.Settings)
        ),
        DrawerMenuItem(
            title = R.string.drawer_menu_logout,
            icon = R.drawable.baseline_logout_24,
            descriptionId = R.string.drawer_menu_logout,
            destination = DrawerDestination.OnLogoutDialog
        )
    )
}
