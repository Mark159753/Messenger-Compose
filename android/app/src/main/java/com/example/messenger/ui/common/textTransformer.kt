package com.example.messenger.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberFilter(
    private val prefix: String
    ) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return phonePrefixTransformation(text, prefix)
    }
}

fun phonePrefixTransformation(text: AnnotatedString, prefix: String):TransformedText{
    val mask = "${prefix}xx xxx xx xx"

    val prefixOffset = prefix.length

    val trimmed =
        if (text.text.length >= (10 + prefixOffset)) text.text.substring(0..9 + prefixOffset) else text.text

    val annotatedString = AnnotatedString.Builder().run {
        append(prefix)
        for (i in trimmed.indices) {
            append(trimmed[i])
            if (i == 1 || i == 4 || i == 6)
                append(" ")
        }
        pushStyle(SpanStyle(color = Color.LightGray))
        append(mask.takeLast(mask.length - length))
        toAnnotatedString()
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset + (prefixOffset)
            if (offset <= 4) return offset + (prefixOffset + 1)
            if (offset <= 6) return offset + (prefixOffset + 2)
            if (offset <= 9) return offset + (prefixOffset + 3)
            return mask.length
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset < prefixOffset) return 0
            if (offset <= (prefixOffset + 1)) return (offset - (prefixOffset)).coerceIn(0, trimmed.length)
            if (offset <= (prefixOffset + 4)) return (offset - (prefixOffset + 1)).coerceIn(0, trimmed.length)
            if (offset <= (prefixOffset + 6)) return (offset - (prefixOffset + 2)).coerceIn(0, trimmed.length)
            if (offset <= mask.length) {
                val originalOffset = offset - (prefixOffset + 3)
                return minOf(originalOffset, trimmed.length)
            }
            return trimmed.length
        }
    }

    return TransformedText(annotatedString, numberOffsetTranslator)
}



