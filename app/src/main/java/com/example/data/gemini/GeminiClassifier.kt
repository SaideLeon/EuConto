package com.example.data.gemini

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ContaPGC
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClassifier {
    private const val TAG = "GeminiClassifier"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun getStoredApiKey(context: Context): String? {
        val prefs = context.applicationContext.getSharedPreferences("Accounting_API_Settings", Context.MODE_PRIVATE)
        return prefs.getString("gemini_api_key", null)
    }

    fun saveApiKey(context: Context, key: String) {
        val prefs = context.applicationContext.getSharedPreferences("Accounting_API_Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
    }

    fun clearApiKey(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences("Accounting_API_Settings", Context.MODE_PRIVATE)
        prefs.edit().remove("gemini_api_key").apply()
    }

    suspend fun classifyElement(
        context: Context,
        descricao: String,
        allContas: List<ContaPGC>
    ): ClassificationResult? {
        val storedKey = getStoredApiKey(context)
        val apiKey = if (!storedKey.isNullOrBlank()) {
            storedKey
        } else {
            BuildConfig.GEMINI_API_KEY
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured or is placeholder.")
            return ClassificationResult(
                error = "Chave da API do Gemini não configurada. Por favor, clique na engrenagem de configuração azul com ícone de engrenagem/chave para definir sua chave de API nos ajustes da aplicação."
            )
        }

        try {
            // Build the system instructions and user prompt
            val systemInstruction = """
                Você é um especialista em contabilidade moçambicana e no plano geral de contas (PGC-NIRF de Moçambique).
                Sua tarefa é analisar a descrição de um elemento patrimonial informada pelo utilizador e identificar a classificação contábil correta (código da conta/subconta do PGC-NIRF).
                
                Retorne OBRIGATORIAMENTE um objeto JSON válido, sem qualquer texto fora dele, no seguinte formato:
                {
                  "codigo": "código numérico da conta/subconta ex. '1.1', '1.2.1', '3.2.4', '4.1.1'",
                  "justificativa": "Breve justificativa em português moçambicano da classificação"
                }

                Instruções cruciais de classificação baseada nas classes PGC-NIRF Moçambique:
                - Classe 1: Meios Financeiros (1.1 Caixa, 1.2 Bancos como depósito à ordem 1.2.1)
                - Classe 2: Inventários e ativos biológicos (2.2 Mercadorias, 2.6 Matérias-primas, etc.)
                - Classe 3: Investimentos de Capital / Ativos Tangíveis (3.2 Ativos tangíveis - 3.2.1 Construções/Edifícios, 3.2.2 Equipamento básico, 3.2.3 Mobiliário/Equipamento administrativo, 3.2.4 Veículos/Equipamento de transporte, 3.2.6 Ferramentas)
                - Classe 4: Contas a Receber e Pagar (4.1 Clientes, 4.2 Fornecedores, 4.3 Empréstimos obtidos, 4.5 Outros devedores, 4.6 Outros credores)
                - Classe 5: Capital Próprio (5.1 Capital Social, etc.)
                
                Seja extremamente preciso ao indicar a conta ou subconta adequada para a descrição fornecida.
            """.trimIndent()

            // Construct Gemini request payload using org.json
            val root = JSONObject()
            
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            contentObj.put("role", "user")
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", "Elemento Patrimonial: $descricao")
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            root.put("contents", contentsArray)

            // Add system instruction
            val sysInstructionObj = JSONObject()
            val sysPartsArray = JSONArray()
            val sysPartObj = JSONObject()
            sysPartObj.put("text", systemInstruction)
            sysPartsArray.put(sysPartObj)
            sysInstructionObj.put("parts", sysPartsArray)
            root.put("systemInstruction", sysInstructionObj)

            // Add generation config for JSON response format
            val generationConfig = JSONObject()
            val responseFormat = JSONObject()
            responseFormat.put("type", "application/json")
            generationConfig.put("responseFormat", responseFormat)
            root.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = root.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Request failed: ${response.code} - $errBody")
                    return ClassificationResult(error = "Falha na chamada à API: Código ${response.code}")
                }

                val bodyStr = response.body?.string() ?: return ClassificationResult(error = "Resposta vazia do servidor.")
                Log.d(TAG, "Response: $bodyStr")

                val jsonResponse = JSONObject(bodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return ClassificationResult(error = "Nenhum candidato de resposta gerado pelo modelo.")
                }

                val candidate = candidates.getJSONObject(0)
                val responseContent = candidate.optJSONObject("content") ?: return ClassificationResult(error = "Nenhum conteúdo no candidato.")
                val responseParts = responseContent.optJSONArray("parts") ?: return ClassificationResult(error = "Nenhuma parte no conteúdo.")
                if (responseParts.length() == 0) {
                    return ClassificationResult(error = "Nenhuma parte de texto retornada.")
                }

                val rawText = responseParts.getJSONObject(0).optString("text")
                if (rawText.isNullOrBlank()) {
                    return ClassificationResult(error = "Campo de texto vazio na resposta.")
                }

                // Parse the inner JSON returned from the model
                val parsedObj = JSONObject(rawText)
                val codigoStr = parsedObj.optString("codigo")
                val justificativa = parsedObj.optString("justificativa")

                if (codigoStr.isNullOrBlank()) {
                    return ClassificationResult(error = "O modelo não retornou um código de conta válido.")
                }

                // Try matching the code in our database
                val matchedConta = matchConta(codigoStr, allContas)

                return ClassificationResult(
                    codigo = codigoStr,
                    justificativa = justificativa,
                    matchedConta = matchedConta
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error performing classification", e)
            return ClassificationResult(error = "Erro de rede ou conexão ao classificar: ${e.localizedMessage}")
        }
    }

    private fun matchConta(codigo: String, allContas: List<ContaPGC>): ContaPGC? {
        // Clean up the code (remove whitespace, dots etc.)
        val cleanCode = codigo.trim()
        
        // 1. Direct match
        val direct = allContas.firstOrNull { it.codigo.equals(cleanCode, ignoreCase = true) }
        if (direct != null) return direct

        // 2. Prefix matching fallback: e.g. "3.2.4.1" -> try "3.2.4", then "3.2", then "3"
        val parts = cleanCode.split(".")
        if (parts.size > 1) {
            for (i in parts.size downTo 1) {
                val subCode = parts.subList(0, i).joinToString(".")
                val match = allContas.firstOrNull { it.codigo.equals(subCode, ignoreCase = true) }
                if (match != null) return match
            }
        }

        // 3. Fallback match
        return null
    }
}

data class ClassificationResult(
    val codigo: String? = null,
    val justificativa: String? = null,
    val matchedConta: ContaPGC? = null,
    val error: String? = null
)
