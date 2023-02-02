package net.itanchi.addeep.server.repository.gif

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OptimizedGifRepo(
    private val gifRepo: GifRepo,
) {
    companion object {
        const val ftsMinWordLength = 3
        private val logger = LoggerFactory.getLogger(OptimizedGifRepo::class.java)
    }

    suspend fun searchByDescription(
        filter: String,
        limit: Int,
        offset: Int,
    ) = try {
        if (filter.length < ftsMinWordLength)
            gifRepo.findAllByDescriptionLike(
                filter = "$filter%",
                limit = limit,
                offset = offset,
            )
        else gifRepo.ftsFindAllByDescription(
            filter = filter,
            limit = limit,
            offset = offset,
        )
    } catch (e: Exception) {
        logger.error("Failed to find gifs by description: ${e.message}")
        emptyList()
    }

    suspend fun countByDescription(
        filter: String
    ) = try {
        if (filter.length < ftsMinWordLength)
            gifRepo.countAllByDescriptionLike("$filter%")
        else gifRepo.ftsCountAllByDescription(filter)
    } catch (e: Exception) {
        logger.error("Failed to count gifs by description: ${e.message}")
        0
    }
}



