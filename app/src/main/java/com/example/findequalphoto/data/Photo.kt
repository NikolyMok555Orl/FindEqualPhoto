package com.example.findequalphoto.data

import android.net.Uri

// Container for information about each video.
data class Photo(val uri: Uri,
                 val name: String,
                 val size: Int
)