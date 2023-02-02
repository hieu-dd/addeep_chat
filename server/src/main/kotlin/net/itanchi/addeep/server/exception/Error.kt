package net.itanchi.addeep.server.exception

sealed class Error(
    open val code: Int,
    override val message: String,
    override val cause: Throwable?,
) : Throwable(message, cause) {
    companion object {
        const val UNEXPECTED_ERROR_STATUS_CODE = 500_000
        const val NOT_FOUND_ERROR_STATUS_CODE = 404_000
        const val UNAUTHORIZED_ERROR_STATUS_CODE = 401_000
        const val SERIALIZATION_ERROR_STATUS_CODE = 400_000
        const val INVALID_USER_AGENT = 400_051
        const val INVALID_DEVICE_TYPE = 400_052
        const val INVALID_USER = 400_053
        const val USER_NOT_FOUND = 404_054
    }

    /**
     * Error code from 001-010
     */
    sealed class CreateUserError(
        override val code: Int,
        override val message: String,
        override val cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val DUPLICATE_USER_INFO = 400_001
        }

        object DuplicateUserInfo : CreateUserError(
            code = DUPLICATE_USER_INFO,
            message = "phone or email already already exists",
            cause = null
        )
    }

    /**
     * Error code from 011-020
     */
    sealed class LoginError(
        override val code: Int,
        override val message: String,
        override val cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val INVALID_EMAIL_OR_PASSWORD = 401_011
        }

        object InvalidEmailOrPassword : CreateUserError(
            code = INVALID_EMAIL_OR_PASSWORD,
            message = "Invalid email or password",
            cause = null
        )
    }

    object UnauthorizedError : Error(
        code = UNAUTHORIZED_ERROR_STATUS_CODE,
        message = "Unauthorized",
        cause = null,
    )

    class UnexpectedError(
        cause: Throwable?,
    ) : Error(
        code = UNEXPECTED_ERROR_STATUS_CODE,
        message = "An unexpected error occurred",
        cause = cause,
    )

    class SerializationError(
        message: String,
        cause: Throwable?,
    ) : Error(
        code = SERIALIZATION_ERROR_STATUS_CODE,
        message = message,
        cause = cause,
    )

    class NotFoundError(
        cause: Throwable?,
    ) : Error(
        code = NOT_FOUND_ERROR_STATUS_CODE,
        message = "Not found",
        cause = cause,
    )

    sealed class ValidationDataError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {

        companion object {
            const val FIELD_EXCEED_MAX_LENGTH_STATUS_CODE = 400_101
            const val FIELD_EXCEED_MAX_VALUE_STATUS_CODE = 400_102
            const val FIELD_NUMBER_OUT_OF_RANGE = 400_103
            const val FIELD_IS_INVALID = 400_104
            const val FIELD_LENGTH_ERROR_STATUS_CODE = 400_105
            const val FIELD_IS_REQUIRED = 400_106
            const val INVALID_EMAIL_STATUS_CODE = 400_107
            const val INVALID_TELEPHONE_STATUS_CODE = 400_108
            const val INVALID_ADDEEP_ID_STATUS_CODE = 400_109
        }

        /** In case field is exceeding of max length */
        class FieldExceedingMaxLength(
            fieldName: String,
        ) : ValidationDataError(
            FIELD_EXCEED_MAX_LENGTH_STATUS_CODE,
            "Field $fieldName is exceeding the max length",
            null,
        )

        /** In case field is exceeding of max value */
        class FieldExceedingMaxValue(
            fieldName: String,
        ) : ValidationDataError(
            FIELD_EXCEED_MAX_VALUE_STATUS_CODE,
            "Field $fieldName is exceeding the max value",
            null,
        )

        /** In case field is not valid */
        class FieldNumberOutOfRange(
            fieldName: String,
            minimum: String,
            maximum: String,
        ) : ValidationDataError(
            FIELD_NUMBER_OUT_OF_RANGE,
            "Field $fieldName is out of range [$minimum, $maximum]",
            null,
        )

        /** In case field is null or empty while it should not */
        class FieldIsInvalid(
            fieldName: String,
        ) : ValidationDataError(
            FIELD_IS_INVALID,
            "Field $fieldName is invalid (wrong format or null/empty while it should not)",
            null,
        )

        /** In case string field's length is out of Range */
        class FieldLengthOutOfRange(
            fieldName: String,
            minLength: String,
            maxLength: String,
        ) : ValidationDataError(
            FIELD_LENGTH_ERROR_STATUS_CODE,
            "Field ${fieldName}'s length is out of range [$minLength, $maxLength]",
            null,
        )

        class FieldRequired(
            fieldName: String,
        ) : ValidationDataError(
            FIELD_IS_REQUIRED,
            "Field $fieldName is required",
            null,
        )

        /** In case the email is invalid */
        object InvalidEmail : ValidationDataError(
            INVALID_EMAIL_STATUS_CODE,
            "Email is invalid",
            null,
        )

        /** In case the telephone is invalid */
        object InvalidTelephone : ValidationDataError(
            INVALID_TELEPHONE_STATUS_CODE,
            "Telephone is invalid",
            null,
        )

        /** In case the addeep is invalid */
        object InvalidAddeepId : ValidationDataError(
            INVALID_ADDEEP_ID_STATUS_CODE,
            "addeepId is invalid",
            null,
        )
    }

    /**
     * error code from 021-030
     */
    sealed class GetConversationDetailError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val CONVERSATION_NOT_FOUND = 400_021
            const val CONVERSATION_ID_INVALID = 400_022
        }

        object ConversationNotfound : GetConversationDetailError(
            CONVERSATION_NOT_FOUND,
            "Conversation not found",
            null,
        )

        object InvalidConversationId : GetConversationDetailError(
            CONVERSATION_ID_INVALID,
            "Invalid conversation id",
            null,
        )
    }

    /**
     * error code from 031-040
     */
    sealed class CreateConversationError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val PARTICIPANT_NOT_FOUND = 400_031
        }

        class ParticipantsNotfound(ids: List<Long>) : CreateConversationError(
            PARTICIPANT_NOT_FOUND,
            "Participants not found $ids",
            null,
        )
    }

    /**
     * error code from 041-050
     */
    sealed class ChatError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val SENDER_NOT_FOUND = 400_041
            const val CONVERSATION_NOT_FOUND = 400_042
            const val INVALID_CHAT_DATA = 400_043
            const val EXCEED_MAX_ATTACHMENT = 400_044
        }

        class SenderNotfound(id: Long) : ChatError(
            SENDER_NOT_FOUND,
            "Sender not found $id",
            null,
        )

        class ConversationNotFound(id: Long) : ChatError(
            CONVERSATION_NOT_FOUND,
            "Conversation not found $id",
            null,
        )

        object InvalidChatData : ChatError(
            INVALID_CHAT_DATA,
            "Invalid chat data",
            null,
        )

        object ExceedMaxAttachment : ChatError(
            EXCEED_MAX_ATTACHMENT,
            "Exceed max attachment",
            null,
        )
    }

    /**
     * error code from 051-060
     */
    sealed class UploadFileError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val INVALID_FILE = 400_051
            const val EXCEED_MAX_SIZE = 400_052
        }

        object InvalidFile : UploadFileError(
            INVALID_FILE,
            "Invalid file",
            null,
        )

        object ExceedMaxSize : UploadFileError(
            EXCEED_MAX_SIZE,
            "File exceed max size",
            null,
        )
    }

    /**
     * error code from 061-070
     */
    sealed class DownloadMessageContentError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val INVALID_MESSAGE_CONTENT = 400_061
            const val INVALID_CONVERSATION = 400_062
            const val INVALID_MESSAGE = 400_063

        }

        object InvalidContent : UploadFileError(
            INVALID_MESSAGE_CONTENT,
            "Invalid message content",
            null,
        )

        object InvalidConversation : UploadFileError(
            INVALID_CONVERSATION,
            "Invalid conversation",
            null,
        )

        object InvalidMessage : UploadFileError(
            INVALID_MESSAGE,
            "Invalid message",
            null,
        )
    }

    /**
     * error code from 070-081
     */
    sealed class AddContactError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val ALREADY_IN_CONTACTS = 400_070
        }

        object AlreadyInContacts : AddContactError(
            ALREADY_IN_CONTACTS,
            "Requested user already in contacts",
            null,
        )
    }

    object FileNotFound : Error(
        NOT_FOUND_ERROR_STATUS_CODE,
        "File not found",
        null,
    )

    object InvalidUserAgent : Error(
        INVALID_USER_AGENT,
        "Invalid User-Agent header",
        null,
    )

    object InvalidDeviceType : Error(
        INVALID_DEVICE_TYPE,
        "Invalid device type",
        null,
    )

    object InvalidUser : Error(
        INVALID_USER,
        "Invalid user",
        null,
    )

    object UserNotFound : Error(
        USER_NOT_FOUND,
        "User not found",
        null,
    )

    sealed class GiphyError(
        code: Int,
        message: String,
        cause: Throwable?,
    ) : Error(code, message, cause) {
        companion object {
            const val GET_GIFS_GIPHY_FAILED = 400_070
            const val CONNECT_GIPHY_FAILED = 500_070
        }

        object GetGifsGiphyFailed : Error(
            GET_GIFS_GIPHY_FAILED,
            "Failed to get gifs from Giphy",
            null,
        )

        object ConnectGiphyFailed : Error(
            CONNECT_GIPHY_FAILED,
            "Failed to connect to Giphy",
            null,
        )
    }
}
