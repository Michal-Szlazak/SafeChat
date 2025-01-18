package com.szlazakm.safechat.client.presentation.components.chat

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.szlazakm.safechat.utils.auth.utils.Decoder
import com.szlazakm.safechat.utils.auth.utils.Encoder
import java.security.MessageDigest

@Composable
fun ChatVerificationScreen(
    chatViewModel: ChatViewModel,
    onScanClicked: () -> Unit
) {

    val fingerprint = chatViewModel.state.collectAsState().value.selectedContact?.securityCode ?: ""
    val qrBitmap = remember { generateQrCode(fingerprintToHex( fingerprint)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verify Chat",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // HEX Code (Fingerprint)
        Text(
            text = "Your Security Code:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        SelectionContainer {
            Text(
                text = fingerprintToHex(fingerprint),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 24.sp,
                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

fun generateQrCode(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun hashFingerprint(fingerprint: String): ByteArray {
    val decoded = Decoder.decode(fingerprint)
    val messageDigest = MessageDigest.getInstance("SHA-256")
    return messageDigest.digest(decoded)
}

fun fingerprintToHex(fingerprint: String): String {

    if(fingerprint == "Send first message to generate security code") {
        return fingerprint
    }

    val hash = hashFingerprint(fingerprint)

    val hex = hash.joinToString("") {
        Integer.toUnsignedString(java.lang.Byte.toUnsignedInt(it), 16).padStart(2, '0')
    }

    val hexWithSpaces = hex.chunked(4).joinToString(" ")
//    hexWithSpaces = hexWithSpaces.chunked(24).joinToString("\n")

    val charArray = hexWithSpaces.toCharArray()

    charArray[19] = '\n'
    charArray[39] = '\n'
    charArray[59] = '\n'

    val base64 = Encoder.encode(hash)
    Log.d("ChatVerificationScreen", "Fingerprint: ${hash.size}")
    Log.d("ChatVerificationScreen", "Base64: $base64")
    Log.d("ChatVerificationScreen", "Fingerprint: $hexWithSpaces")
    Log.d("ChatVerificationScreen", "Fingerprint: ${String(charArray)}")

    return String(charArray)
}
