package com.gaiaspace.security

import com.gaiaspace.domain.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret}")
    private val jwtSecret: String,
    
    @Value("\${app.jwt.expiration}")
    private val jwtExpirationInMs: Long
) {
    private val key: Key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    }
    
    fun generateToken(user: User): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)
        
        return Jwts.builder()
            .setSubject(user.id)
            .claim("username", user.username)
            .claim("roles", user.roles)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: Exception) {
            return false
        }
    }
    
    fun getUserIdFromJWT(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        
        return claims.subject
    }
    
    fun getRolesFromJWT(token: String): List<String> {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        
        @Suppress("UNCHECKED_CAST")
        return claims["roles"] as? List<String> ?: emptyList()
    }
}