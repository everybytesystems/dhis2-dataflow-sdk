package com.everybytesystems.ebscore.auth.jwt

import com.everybytesystems.ebscore.auth.utils.Base64Utils
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

/**
 * Production-ready JWT Token validator for multiplatform support
 */
class JwtValidator(
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    
    /**
     * Validate JWT token structure and expiration
     */
    fun validateToken(token: String): JwtValidationResult {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                return JwtValidationResult.Invalid("Invalid JWT format: expected 3 parts, got ${parts.size}")
            }
            
            val header = decodeJwtPart(parts[0])
            val payload = decodeJwtPart(parts[1])
            
            val headerJson = json.parseToJsonElement(header) as JsonObject
            val payloadJson = json.parseToJsonElement(payload) as JsonObject
            
            // Validate header
            val algorithm = headerJson["alg"]?.jsonPrimitive?.content
            if (algorithm.isNullOrBlank()) {
                return JwtValidationResult.Invalid("Missing or invalid algorithm in JWT header")
            }
            
            // Extract claims
            val claims = JwtClaims(
                issuer = payloadJson["iss"]?.jsonPrimitive?.content,
                subject = payloadJson["sub"]?.jsonPrimitive?.content,
                audience = payloadJson["aud"]?.jsonPrimitive?.content,
                expirationTime = payloadJson["exp"]?.jsonPrimitive?.long,
                notBefore = payloadJson["nbf"]?.jsonPrimitive?.long,
                issuedAt = payloadJson["iat"]?.jsonPrimitive?.long,
                jwtId = payloadJson["jti"]?.jsonPrimitive?.content,
                algorithm = algorithm,
                customClaims = payloadJson.filterKeys { 
                    it !in setOf("iss", "sub", "aud", "exp", "nbf", "iat", "jti") 
                }
            )
            
            // Validate timing claims
            val now = Clock.System.now().epochSeconds
            
            // Check expiration
            claims.expirationTime?.let { exp ->
                if (now >= exp) {
                    return JwtValidationResult.Expired("Token expired at ${Instant.fromEpochSeconds(exp)}")
                }
            }
            
            // Check not before
            claims.notBefore?.let { nbf ->
                if (now < nbf) {
                    return JwtValidationResult.Invalid("Token not valid before ${Instant.fromEpochSeconds(nbf)}")
                }
            }
            
            // Check issued at (should not be in the future)
            claims.issuedAt?.let { iat ->
                if (now < iat - 60) { // Allow 60 seconds clock skew
                    return JwtValidationResult.Invalid("Token issued in the future: ${Instant.fromEpochSeconds(iat)}")
                }
            }
            
            JwtValidationResult.Valid(claims)
            
        } catch (e: Exception) {
            JwtValidationResult.Invalid("Failed to parse JWT: ${e.message}")
        }
    }
    
    /**
     * Extract claims from JWT token without validation
     */
    fun extractClaims(token: String): JwtClaims? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            
            val header = decodeJwtPart(parts[0])
            val payload = decodeJwtPart(parts[1])
            
            val headerJson = json.parseToJsonElement(header) as JsonObject
            val payloadJson = json.parseToJsonElement(payload) as JsonObject
            
            JwtClaims(
                issuer = payloadJson["iss"]?.jsonPrimitive?.content,
                subject = payloadJson["sub"]?.jsonPrimitive?.content,
                audience = payloadJson["aud"]?.jsonPrimitive?.content,
                expirationTime = payloadJson["exp"]?.jsonPrimitive?.long,
                notBefore = payloadJson["nbf"]?.jsonPrimitive?.long,
                issuedAt = payloadJson["iat"]?.jsonPrimitive?.long,
                jwtId = payloadJson["jti"]?.jsonPrimitive?.content,
                algorithm = headerJson["alg"]?.jsonPrimitive?.content,
                customClaims = payloadJson.filterKeys { 
                    it !in setOf("iss", "sub", "aud", "exp", "nbf", "iat", "jti") 
                }
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if token is expired
     */
    fun isTokenExpired(token: String, bufferSeconds: Long = 300): Boolean {
        val claims = extractClaims(token) ?: return true
        val now = Clock.System.now().epochSeconds
        return claims.expirationTime?.let { exp -> now >= (exp - bufferSeconds) } ?: false
    }
    
    /**
     * Get token expiration time
     */
    fun getTokenExpiration(token: String): Instant? {
        val claims = extractClaims(token) ?: return null
        return claims.expirationTime?.let { Instant.fromEpochSeconds(it) }
    }
    
    /**
     * Get time until token expires (in seconds)
     */
    fun getTimeUntilExpiration(token: String): Long? {
        val claims = extractClaims(token) ?: return null
        val now = Clock.System.now().epochSeconds
        return claims.expirationTime?.let { exp -> maxOf(0, exp - now) }
    }
    
    /**
     * Check if token needs refresh based on buffer time
     */
    fun needsRefresh(token: String, bufferSeconds: Long = 300): Boolean {
        return isTokenExpired(token, bufferSeconds)
    }
    
    private fun decodeJwtPart(part: String): String {
        // Add padding if needed for base64 decoding
        val paddedPart = when (part.length % 4) {
            2 -> part + "=="
            3 -> part + "="
            else -> part
        }
        
        return try {
            decodeBase64Url(paddedPart)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid base64 encoding in JWT part: ${e.message}", e)
        }
    }
    
    private fun decodeBase64Url(input: String): String {
        // Convert base64url to base64
        val base64 = input.replace('-', '+').replace('_', '/')
        
        // Platform-specific base64 decoding
        return decodeBase64(base64)
    }
    
    private fun decodeBase64(input: String): String {
        // This will be implemented platform-specifically
        return Base64Utils.decodeUrlSafe(input)
    }
}



/**
 * JWT validation result
 */
sealed class JwtValidationResult {
    data class Valid(val claims: JwtClaims) : JwtValidationResult()
    data class Invalid(val reason: String) : JwtValidationResult()
    data class Expired(val reason: String) : JwtValidationResult()
}

/**
 * JWT Claims with comprehensive support
 */
@Serializable
data class JwtClaims(
    val issuer: String? = null,           // iss - Issuer
    val subject: String? = null,          // sub - Subject
    val audience: String? = null,         // aud - Audience
    val expirationTime: Long? = null,     // exp - Expiration Time
    val notBefore: Long? = null,          // nbf - Not Before
    val issuedAt: Long? = null,           // iat - Issued At
    val jwtId: String? = null,            // jti - JWT ID
    val algorithm: String? = null,        // alg - Algorithm from header
    val customClaims: Map<String, JsonElement> = emptyMap()
) {
    /**
     * Get custom claim as string
     */
    fun getCustomClaim(key: String): String? {
        return customClaims[key]?.jsonPrimitive?.content
    }
    
    /**
     * Get custom claim as long
     */
    fun getCustomClaimAsLong(key: String): Long? {
        return try {
            customClaims[key]?.jsonPrimitive?.long
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if token is expired
     */
    fun isExpired(bufferSeconds: Long = 0): Boolean {
        val now = Clock.System.now().epochSeconds
        return expirationTime?.let { exp -> now >= (exp - bufferSeconds) } ?: false
    }
    
    /**
     * Check if token is valid now (considering nbf and exp)
     */
    fun isValidNow(): Boolean {
        val now = Clock.System.now().epochSeconds
        
        // Check not before
        notBefore?.let { nbf ->
            if (now < nbf) return false
        }
        
        // Check expiration
        expirationTime?.let { exp ->
            if (now >= exp) return false
        }
        
        return true
    }
}