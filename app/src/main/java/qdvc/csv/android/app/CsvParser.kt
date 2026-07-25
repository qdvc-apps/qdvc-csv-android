package qdvc.csv.android.app

/**
 * A small, dependency-free CSV parser that follows the essentials of RFC 4180:
 *  - Fields may be quoted with double quotes.
 *  - Quoted fields may contain commas, CR, LF and escaped quotes ("").
 *  - Both \n and \r\n line endings are supported.
 *
 * The delimiter is auto-detected from the first non-empty line among a small
 * candidate set (comma, semicolon, tab, pipe).
 */
object CsvParser {

    private val CANDIDATE_DELIMITERS = listOf(',', ';', '\t', '|')

    data class Result(
        val rows: List<List<String>>,
        val delimiter: Char,
    )

    fun parse(text: String): Result {
        val delimiter = detectDelimiter(text)
        val rows = parseWith(text, delimiter)
        return Result(rows, delimiter)
    }

    private fun detectDelimiter(text: String): Char {
        // Inspect the first line that is not inside quotes.
        val firstLine = firstLogicalLine(text)
        var best = ','
        var bestCount = -1
        for (d in CANDIDATE_DELIMITERS) {
            val count = countUnquoted(firstLine, d)
            if (count > bestCount) {
                bestCount = count
                best = d
            }
        }
        return best
    }

    private fun firstLogicalLine(text: String): String {
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' -> {
                    inQuotes = !inQuotes
                    sb.append(c)
                }
                (c == '\n' || c == '\r') && !inQuotes -> return sb.toString()
                else -> sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    private fun countUnquoted(line: String, delimiter: Char): Int {
        var count = 0
        var inQuotes = false
        for (c in line) {
            when (c) {
                '"' -> inQuotes = !inQuotes
                delimiter -> if (!inQuotes) count++
            }
        }
        return count
    }

    private fun parseWith(text: String, delimiter: Char): List<List<String>> {
        val rows = ArrayList<List<String>>()
        var field = StringBuilder()
        var record = ArrayList<String>()
        var inQuotes = false
        var i = 0
        val n = text.length
        var sawAnyChar = false

        fun endField() {
            record.add(field.toString())
            field = StringBuilder()
        }

        fun endRecord() {
            endField()
            rows.add(record)
            record = ArrayList()
            sawAnyChar = false
        }

        while (i < n) {
            val c = text[i]
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < n && text[i + 1] == '"') {
                        field.append('"')
                        i++
                    } else {
                        inQuotes = false
                    }
                } else {
                    field.append(c)
                }
            } else {
                when (c) {
                    '"' -> {
                        inQuotes = true
                        sawAnyChar = true
                    }
                    delimiter -> {
                        endField()
                        sawAnyChar = true
                    }
                    '\r' -> {
                        // swallow, handle on \n or as standalone CR
                        if (i + 1 < n && text[i + 1] == '\n') {
                            i++
                        }
                        endRecord()
                    }
                    '\n' -> endRecord()
                    else -> {
                        field.append(c)
                        sawAnyChar = true
                    }
                }
            }
            i++
        }

        // Flush trailing field/record if the file doesn't end with a newline
        // or has content buffered.
        if (sawAnyChar || field.isNotEmpty() || record.isNotEmpty()) {
            endRecord()
        }

        return rows
    }
}
