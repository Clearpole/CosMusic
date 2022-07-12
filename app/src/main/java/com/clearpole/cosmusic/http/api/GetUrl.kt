package com.clearpole.cosmusic.http.api

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class GetUrl {
    companion object {
        private val client = OkHttpClient()

        @Throws(IOException::class)
        fun run(url: String?): String {
            val request: Request = Request.Builder()
                .url(url!!)
                .build()
            client.newCall(request).execute()
                .use { response -> return response.body!!.string() }
        }
    }
}