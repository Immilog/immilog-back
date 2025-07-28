package com.backend.immilog.image.exception

import com.backend.immilog.shared.exception.CustomException
import com.backend.immilog.shared.exception.ErrorCode

class ImageException(e: ErrorCode) : CustomException(e)
