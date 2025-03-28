package com.cmoigo.knativeweb3k

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform