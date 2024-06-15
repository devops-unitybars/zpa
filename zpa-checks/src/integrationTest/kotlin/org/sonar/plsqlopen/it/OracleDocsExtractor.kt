/**
 * Z PL/SQL Analyzer
 * Copyright (C) 2015-2024 Felipe Zorzo
 * mailto:felipe AT felipezorzo DOT com DOT br
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plsqlopen.it

import org.jsoup.Jsoup
import java.io.File
import java.util.zip.ZipFile

fun main() {
    OracleDocsExtractor().extract()
}

class OracleDocsExtractor {

    fun extract() {
        // you need to get the file from https://docs.oracle.com/en/database/oracle/oracle-database/23/zip/oracle-database_23.zip
        val zipFile = ZipFile(System.getProperty("oracleDocs"))
        val outputDir = File("src/integrationTest/resources/sources/oracle-database_23")

        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()

        val entries = zipFile.entries()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (!entry.isDirectory && File(entry.name).parent.endsWith("sqlrf")) {
                zipFile.getInputStream(entry).use { stream ->
                    Jsoup.parse(stream, Charsets.UTF_8.name(), "").run {
                        select("pre.oac_no_warn").forEachIndexed { index, element ->
                            var text = element.text()

                            val lines = text.lines()
                            val line = lines.indexOfFirst { it.contains("---") }
                            try {
                                if (line != -1) {
                                    text = text.lines().take(line - 2).joinToString(separator = "\n")
                                }

                                if (text.isNotEmpty()) {
                                    val name = "${File(entry.name).nameWithoutExtension}-$index.sql"
                                    val path = entry.name.substring(entry.name.indexOf("sqlrf"))
                                    text = "-- https://docs.oracle.com/en/database/oracle/oracle-database/23/$path\n$text"
                                    File(outputDir.absolutePath, name).writeText(text, Charsets.UTF_8)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
            }
        }
    }

}
