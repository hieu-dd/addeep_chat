package net.itanchi.addeep.server.repository.gif

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GifRepo : CoroutineCrudRepository<GifModel, Long> {
    @Query("SELECT * FROM gifs WHERE MATCH(description) AGAINST(:filter) ORDER BY id DESC LIMIT :offset,:limit")
    suspend fun ftsFindAllByDescription(
        filter: String,
        limit: Int,
        offset: Int,
    ): List<GifModel>

    @Query("SELECT COUNT(id) FROM gifs WHERE MATCH(description) AGAINST(:filter)")
    suspend fun ftsCountAllByDescription(
        filter: String,
    ): Int

    @Query("SELECT COUNT(id) FROM gifs WHERE LOWER(description) LIKE LOWER(:filter)")
    suspend fun countAllByDescriptionLike(
        filter: String
    ): Int

    @Query("SELECT * FROM gifs WHERE LOWER(description) LIKE LOWER(:filter) ORDER BY id DESC LIMIT :offset,:limit")
    suspend fun findAllByDescriptionLike(
        filter: String,
        limit: Int,
        offset: Int,
    ): List<GifModel>
}