package com.nicha.furnier.utils.oauthInterceptor

interface TimestampService {
    val timestampInSeconds: String
    val nonce: String
}