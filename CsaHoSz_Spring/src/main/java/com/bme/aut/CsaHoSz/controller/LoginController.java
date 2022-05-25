package com.bme.aut.CsaHoSz.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @GetMapping("token_expiration_check")
    public ResponseEntity<Boolean> is_valid(@RequestParam(name="token") String token){
        if(token != null){
            try {
                Date currentTime = new Date(System.currentTimeMillis());
                JWTVerifier verifier = JWT.require(Algorithm.HMAC256("CsaHoSz".getBytes())).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }
            catch (Exception e){
                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
            }
        }
        else
            return new ResponseEntity<Boolean>(HttpStatus.NOT_ACCEPTABLE);
    }

}
