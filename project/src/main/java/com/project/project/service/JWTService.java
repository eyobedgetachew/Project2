package com.project.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.project.model.MyUser;

import jakarta.annotation.PostConstruct;

@Service
public class JWTService {
    
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";
    private static final String EMAIL_KEY = "EMAIL";

    @PostConstruct
    public void PostConstruct(){
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWt(MyUser user){
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("USERNAME", user.getUsername())
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + expiryInSeconds * 1000))
                .sign(algorithm);

    }
    public String generateVerificationJWT(MyUser user) {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim(EMAIL_KEY, user.getEmail())
                .withExpiresAt(new java.util.Date(System.currentTimeMillis() + expiryInSeconds * 1000))
                .sign(algorithm);
    }
    public  String getUsername(String token) {
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();}
}
