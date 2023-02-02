package net.itanchi.addeep.server.repository.contact

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepo : CoroutineCrudRepository<ContactModel, Long>