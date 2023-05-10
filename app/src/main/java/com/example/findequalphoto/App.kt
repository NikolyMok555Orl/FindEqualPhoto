package com.example.findequalphoto

import android.app.Application
import android.content.ContentResolver
import android.content.IntentSender
import com.example.findequalphoto.data.PhotoRepoImpl

class App : Application() {


    private var repo: PhotoRepoImpl? = null


    fun getRepo(
        contentResolver: ContentResolver,
        senderAsDelete: (sended: IntentSender) -> Unit
    ): PhotoRepoImpl {
        return repo?.let  {
            it.senderAsDelete=senderAsDelete
            it
        } ?: kotlin.run {
            repo = PhotoRepoImpl(contentResolver, senderAsDelete)
            repo!!
        }
    }


}