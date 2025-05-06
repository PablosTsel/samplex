package es.uc3m.android.samplex.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PdfTextExtractor {
    private var isInitialized = false
    
    private fun initializePdfBox(context: Context) {
        if (!isInitialized) {
            PDFBoxResourceLoader.init(context.applicationContext)
            isInitialized = true
        }
    }
    
    suspend fun extractTextFromPdf(context: Context, contentUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("PdfTextExtractor", "Extrayendo texto del PDF: $contentUri")
                initializePdfBox(context)
                
                val inputStream = context.contentResolver.openInputStream(contentUri)
                inputStream?.use { stream ->
                    PDDocument.load(stream).use { document ->
                        val pdfStripper = PDFTextStripper()
                        pdfStripper.sortByPosition = true
                        val text = pdfStripper.getText(document)
                        Log.d("PdfTextExtractor", "Texto extraído exitosamente (${text.length} caracteres)")
                        return@withContext text
                    }
                } ?: ""
            } catch (e: Exception) {
                Log.e("PdfTextExtractor", "Error al extraer texto: ${e.message}", e)
                return@withContext ""
            }
        }
    }
}