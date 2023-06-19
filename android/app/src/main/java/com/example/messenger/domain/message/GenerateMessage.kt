package com.example.messenger.domain.message

import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.UserEntity
import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.network.models.message.dto.MessageDto
import com.example.messenger.domain.date.dateNowInUTC
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class GenerateMessage @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource
) {

    suspend operator fun invoke(dto:MessageDto, images:List<String>): MessageWithAuthor {
        val user = userLocalDataSource.userPreferencesFlow.first()
        return MessageWithAuthor(
                message = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = dto.chatId,
                    created_at = dateNowInUTC(),
                    message = dto.message,
                    updated_at = dateNowInUTC(),
                    authorId = user.id,
                    images = images
                ),
                author = UserEntity(
                    id = user.id,
                    created_at = user.createdAt,
                    email = user.email,
                    first_name = user.firstName,
                    last_name = user.lastName,
                    nick_name = user.nickName,
                    phone = user.phone,
                    updated_at = user.updatedAt,
                    avatar = user.avatar,
                    isOnline = false
                )
        )
    }
}