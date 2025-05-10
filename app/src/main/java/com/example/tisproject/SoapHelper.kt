package com.example.tisproject

import okhttp3.*
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

object SoapHelper {
    private const val SOAP_ACTION = "getTicketsByRoute"
    private const val NAMESPACE = "http://example.com/ticketservice"
    private const val METHOD_NAME = "getTicketsByRoute"
    private const val URL = "http://10.0.2.2:8000/wsdl?wsdl"

    fun cariTiket(asal: String, tujuan: String): List<Ticket> {
        val requestBody = """
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tik="$NAMESPACE">
           <soapenv:Header/>
           <soapenv:Body>
              <tik:$METHOD_NAME>
                 <tujuan>$tujuan</tujuan>
              </tik:$METHOD_NAME>
           </soapenv:Body>
        </soapenv:Envelope>
    """.trimIndent()

        val body = RequestBody.create(MediaType.parse("text/xml"), requestBody)

        val request = Request.Builder()
            .url(URL)
            .post(body)
            .addHeader("Content-Type", "text/xml;charset=UTF-8")
            .addHeader("SOAPAction", SOAP_ACTION)
            .build()

        val client = OkHttpClient()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body()?.string() ?: return emptyList()

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(responseBody))

            var ticketsJson: String? = null
            var eventType = parser.eventType
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                if (eventType == org.xmlpull.v1.XmlPullParser.START_TAG && parser.name == "tickets") {
                    ticketsJson = parser.nextText()
                    break
                }
                eventType = parser.next()
            }

            val result = mutableListOf<Ticket>()
            if (!ticketsJson.isNullOrEmpty()) {
                val array = org.json.JSONArray(ticketsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    result.add(Ticket.fromJson(obj.toString()))
                }
            }

            result
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}