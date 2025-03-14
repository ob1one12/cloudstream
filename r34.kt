package com.example.rule34video

import android.util.Log
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.ExtractorLink
import org.jsoup.Jsoup

class Rule34Video : MainAPI() {
    override var name = "Rule34Video"
    override val mainUrl = "https://rule34video.com"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.NSFW)

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search/videos?search_text=$query"
        val doc = Jsoup.connect(url).get()
        val results = mutableListOf<SearchResponse>()

        doc.select(".video-block").forEach { element ->
            val title = element.select("h5").text()
            val link = element.select("a").attr("href")
            val poster = element.select("img").attr("src")

            results.add(
                SearchResponse(
                    title,
                    "$mainUrl$link",
                    TvType.NSFW,
                    poster
                )
            )
        }

        return results
    }

    override suspend fun load(url: String): List<ExtractorLink> {
        val doc = Jsoup.connect(url).get()
        val videoUrl = doc.select("video source").attr("src")

        return listOf(
            ExtractorLink(
                name,
                name,
                videoUrl,
                referer = mainUrl
            )
        )
    }
}
