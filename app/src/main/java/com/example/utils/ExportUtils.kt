package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.database.HistoryEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {

    fun exportHistoryToPdf(context: Context, historyList: List<HistoryEntry>) {
        if (historyList.isEmpty()) {
            Toast.makeText(context, "No history to export", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val pdfDocument = PdfDocument()
            val paint = Paint()
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.BLACK
            }
            val headerPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
                color = Color.DKGRAY
            }
            val textPaint = Paint().apply {
                textSize = 10f
                color = Color.BLACK
            }
            val linePaint = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 1f
            }

            // Define page metrics (A4 size: 595 x 842 points)
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas: Canvas = page.canvas

            var y = 40f
            canvas.drawText("Scientific Calculator History Report", 40f, y, titlePaint)
            y += 10f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 20f

            // Table headers
            canvas.drawText("Date & Time", 40f, y, headerPaint)
            canvas.drawText("Calculation", 180f, y, headerPaint)
            canvas.drawText("Result", 450f, y, headerPaint)
            y += 10f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 15f

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            for (entry in historyList) {
                // Check if we need a new page
                if (y > 800) {
                    pdfDocument.finishPage(page)
                    val nextPageIndex = pdfDocument.pages.size + 1
                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, nextPageIndex).create()
                    page = pdfDocument.startPage(newPageInfo)
                    canvas = page.canvas
                    y = 40f
                    
                    // Table headers on next page
                    canvas.drawText("Date & Time", 40f, y, headerPaint)
                    canvas.drawText("Calculation", 180f, y, headerPaint)
                    canvas.drawText("Result", 450f, y, headerPaint)
                    y += 10f
                    canvas.drawLine(40f, y, 555f, y, linePaint)
                    y += 15f
                }

                val dateStr = sdf.format(Date(entry.timestamp))
                
                // Draw date
                canvas.drawText(dateStr, 40f, y, textPaint)
                
                // Draw expression and result (with basic truncation to prevent overlaps)
                val exprTrunc = if (entry.expression.length > 40) entry.expression.take(37) + "..." else entry.expression
                val resTrunc = if (entry.result.length > 15) entry.result.take(12) + "..." else entry.result
                
                canvas.drawText(exprTrunc, 180f, y, textPaint)
                canvas.drawText(resTrunc, 450f, y, textPaint)
                
                y += 20f
            }

            pdfDocument.finishPage(page)

            // Save file in cache directory so we can easily share it using FileProvider
            val cacheDir = File(context.cacheDir, "reports")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            
            val file = File(cacheDir, "Calculator_History_${System.currentTimeMillis()}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            shareFile(context, file, "application/pdf", "Calculator History PDF")
        } catch (e: Exception) {
            Log.e("ExportUtils", "PDF Export failed", e)
            Toast.makeText(context, "Failed to export PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun exportHistoryToHtml(context: Context, historyList: List<HistoryEntry>) {
        if (historyList.isEmpty()) {
            Toast.makeText(context, "No history to export", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val sb = StringBuilder()
            
            sb.append("<!DOCTYPE html><html><head><title>Calculator History</title>")
            sb.append("<style>")
            sb.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f6f8fa; }")
            sb.append("h2 { color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 10px; }")
            sb.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; background-color: white; box-shadow: 0 1px 3px rgba(0,0,0,0.2); }")
            sb.append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }")
            sb.append("th { background-color: #1976D2; color: white; }")
            sb.append("tr:hover { background-color: #f1f1f1; }")
            sb.append(".fav { color: #ff1744; font-weight: bold; }")
            sb.append("</style></head><body>")
            sb.append("<h2>Scientific Calculator History Report</h2>")
            sb.append("<table><thead><tr><th>Date & Time</th><th>Expression</th><th>Result</th><th>Favorite</th></tr></thead><tbody>")

            for (entry in historyList) {
                val dateStr = sdf.format(Date(entry.timestamp))
                val isFavStr = if (entry.isFavorite) "<span class='fav'>♥ Favorite</span>" else "No"
                sb.append("<tr>")
                sb.append("<td>$dateStr</td>")
                sb.append("<td>${escapeHtml(entry.expression)}</td>")
                sb.append("<td><b>${escapeHtml(entry.result)}</b></td>")
                sb.append("<td>$isFavStr</td>")
                sb.append("</tr>")
            }

            sb.append("</tbody></table></body></html>")

            // Write HTML file
            val cacheDir = File(context.cacheDir, "reports")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            
            val file = File(cacheDir, "Calculator_History_${System.currentTimeMillis()}.html")
            val fos = FileOutputStream(file)
            fos.write(sb.toString().toByteArray())
            fos.close()

            shareFile(context, file, "text/html", "Calculator History HTML")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to export HTML: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun escapeHtml(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val authority = "${context.packageName}.fileprovider"
        val uri: Uri = FileProvider.getUriForFile(context, authority, file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report via"))
    }
}
