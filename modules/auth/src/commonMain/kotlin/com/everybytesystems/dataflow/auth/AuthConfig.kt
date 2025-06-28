package com.everybytesystems.dataflow.auth

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

/**
 * Authentication configuration for DHIS2
 */
@Serializable
sealed class AuthConfig {
    
    /**
     * Validate the authentication configuration
     */
    abstract fun validate(): Result<Unit>
    
    /**
     * Basic authentication with username and password
     */
    @Serializable
    data class Basic(
        val username: String,
        val password: String
    ) : AuthConfig() {
        override fun validate(): Result<Unit> {
            return when {
                username.isBlank() -> Result.failure(IllegalArgumentException("Username cannot be blank"))
                password.isBlank() -> Result.failure(IllegalArgumentException("Password cannot be blank"))
                else -> Result.success(Unit)
            }
        }
    }
    
    /**
     * Personal Access Token authentication
     */
    @Serializable
    data class PersonalAccessToken(
        val token: String
    ) : AuthConfig() {
        override fun validate(): Result<Unit> {
            return when {
                token.isBlank() -> Result.failure(IllegalArgumentException("Token cannot be blank"))
                else -> Result.success(Unit)
            }
        }
    }
    
    /**
     * OAuth2/OIDC authentication
     */
    @Serializable
    data class OAuth2(
        val clientId: String,
        val clientSecret: String? = null,
        val redirectUri: String,
        val authorizationUrl: String,
        val tokenUrl: String,
        val scope: String = "openid profile",
        val additionalParameters: Map<String, String> = emptyMap()
    ) : AuthConfig() {
        override fun validate(): Result<Unit> {
            return when {
                clientId.isBlank() -> Result.failure(IllegalArgumentException("Client ID cannot be blank"))
                redirectUri.isBlank() -> Result.failure(IllegalArgumentException("Redirect URI cannot be blank"))
                authorizationUrl.isBlank() -> Result.failure(IllegalArgumentException("Authorization URL cannot be blank"))
                tokenUrl.isBlank() -> Result.failure(IllegalArgumentException("Token URL cannot be blank"))
                !redirectUri.startsWith("http") -> Result.failure(IllegalArgumentException("Redirect URI must be a valid URL"))
                !authorizationUrl.startsWith("http") -> Result.failure(IllegalArgumentException("Authorization URL must be a valid URL"))
                !tokenUrl.startsWith("http") -> Result.failure(IllegalArgumentException("Token URL must be a valid URL"))
                else -> Result.success(Unit)
            }
        }
    }
    
    /**
     * Cookie-based authentication (for web sessions)
     */
    @Serializable
    data class Cookie(
        val sessionCookie: String
    ) : AuthConfig() {
        override fun validate(): Result<Unit> {
            return when {
                sessionCookie.isBlank() -> Result.failure(IllegalArgumentException("Session cookie cannot be blank"))
                else -> Result.success(Unit)
            }
        }
    }
}

/**
 * Authentication state
 */
sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticating : AuthState()
    data class Authenticated(val user: UserInfo) : AuthState()
    data class Error(val message: String, val cause: Throwable? = null) : AuthState()
}

/**
 * User information
 */
@Serializable
data class UserInfo(
    val id: String,
    val username: String,
    val displayName: String? = null,
    val email: String? = null,
    val authorities: List<String> = emptyList(),
    val organisationUnits: List<OrganisationUnit> = emptyList(),
    val dataViewOrganisationUnits: List<OrganisationUnit> = emptyList()
)

/**
 * Organisation unit information
 */
@Serializable
data class OrganisationUnit(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val level: Int? = null,
    val path: String? = null
)

/**
 * OAuth2 token response
 */
@Serializable
data class OAuth2TokenResponse(
    val access_token: String,
    val token_type: String = "Bearer",
    val expires_in: Long? = null,
    val refresh_token: String? = null,
    val scope: String? = null,
    val id_token: String? = null
)

/**
 * Stored authentication credentials
 */
@Serializable
data class StoredCredentials(
    val authConfig: AuthConfig,
    val userInfo: UserInfo? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresAt: Long? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)