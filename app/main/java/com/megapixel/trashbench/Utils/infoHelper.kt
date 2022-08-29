package com.megapixel.trashbench.Utils


import com.megapixel.trashbench.Model.User
import io.reactivex.rxjava3.internal.util.NotificationLite.getValue


val user = getValue<User>(User::class.java)