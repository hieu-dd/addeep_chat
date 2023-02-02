package net.itanchi.addeep.server.service.gif

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.itanchi.addeep.server.config.service.Services
import net.itanchi.addeep.server.repository.gif.OptimizedGifRepo
import net.itanchi.addeep.server.rest.media.dto.GifDTO
import net.itanchi.addeep.server.service.storage.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import net.itanchi.addeep.server.config.service.Services.ServiceProperty.ServiceID.*
import net.itanchi.addeep.server.rest.media.MediaRouter
import net.itanchi.addeep.server.service.storage.ResourceInfo

@Component
class GifService(
    private val giphyApi: GiphyApi,
    private val optimizedGifRepo: OptimizedGifRepo,
    private val storageService: StorageService,
    services: Services,
) {
    private val logger = LoggerFactory.getLogger(GifService::class.java)
    private val ADDEEP_BASE_URL = services.properties.find { it.id == ADDEEP }?.baseUrl.orEmpty()

    companion object {
        const val GIPHY_DATA_SOURCE = "giphy"
        const val ADDEEP_DATA_SOURCE = "addeep"
        const val GIFS_FOLDER_NAME = "gifs"
    }

    suspend fun getGifs(
        page: Int,
        pageSize: Int,
        filter: String,
    ): List<GifDTO> = coroutineScope {
        val dbTotalRecords = optimizedGifRepo.countByDescription(filter)
        val offset = (page - 1) * pageSize
        val repoGifsAsync = async {
            if (offset < dbTotalRecords) optimizedGifRepo.searchByDescription(
                offset = offset,
                limit = pageSize,
                filter = filter,
            ).map {
                GifDTO(
                    url = "$ADDEEP_BASE_URL/${MediaRouter.API_ROUTE}/$GIFS_FOLDER_NAME/${it.name}",
                    dataSource = ADDEEP_DATA_SOURCE,
                )
            }
            else emptyList()
        }
        val giphyGifsAsync = async {
            if (page * pageSize > dbTotalRecords) {
                try {
                    val nextOffset = (offset - dbTotalRecords).takeIf { it > 0 } ?: 0
                    val nextLimit = pageSize.takeIf { nextOffset != 0 } ?: (page * pageSize - dbTotalRecords)
                    val giphyResponse = if (filter.isNotEmpty()) giphyApi.getGifs(
                        offset = nextOffset,
                        limit = nextLimit,
                        filter = filter,
                    ) else giphyApi.getTrendingGifs(
                        offset = nextOffset,
                        limit = nextLimit,
                    )
                    giphyResponse.data.map {
                        GifDTO(
                            url = it.images.original.webp,
                            dataSource = GIPHY_DATA_SOURCE,
                        )
                    }
                } catch (e: Exception) {
                    logger.error("Failed to get gifs from Giphy: ${e.message}")
                    emptyList()
                }
            } else emptyList()
        }
        awaitAll(repoGifsAsync, giphyGifsAsync).flatten()
    }

    suspend fun downloadGif(gifName: String): ResourceInfo {
        return storageService.getResource("$GIFS_FOLDER_NAME/$gifName")
    }
}