package com.alexandrekpr.picpay_simplified.services;

import com.alexandrekpr.picpay_simplified.domain.user.User;
import com.alexandrekpr.picpay_simplified.dtos.AuthResponse;
import com.alexandrekpr.picpay_simplified.exceptions.ExternalApiException;
import com.alexandrekpr.picpay_simplified.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class AuthenticateService {

    @Autowired
    RestTemplate restTemplate;


    public boolean authenticateTransaction(User sender, BigDecimal amount) throws ExternalApiException {
        ResponseEntity<AuthResponse> response = restTemplate.getForEntity(
                "https://util.devi.tools/api/v2/authorize",
                AuthResponse.class
        );

        System.err.println("Resposta da API de autenticação: " + response);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String status = response.getBody().status();
            boolean isAuthorized = response.getBody().data().authorization();

            if ("fail".equalsIgnoreCase(status) || !isAuthorized) {
                throw new ForbiddenException();
            }

            return true;
        }

        return false;
    }

}
