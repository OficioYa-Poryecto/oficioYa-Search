package edu.eci.dosw.oficioya.search.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

class JwtDecoderTest {

    private static final byte[] CLAVE = "clave-de-prueba-para-tokens-de-identity-de-48-bytes-o-mas".getBytes();
    private static final String SECRETO = Base64.getEncoder().encodeToString(CLAVE);

    private final JwtDecoder decoder = new SecurityConfig().jwtDecoder(SECRETO);

    private static String firmar(JWSAlgorithm algoritmo, byte[] clave, String sub) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(sub)
                .claim("roles", List.of("SISTEMA"))
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 60_000))
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(algoritmo), claims);
        jwt.sign(new MACSigner(clave));
        return jwt.serialize();
    }

    @Test
    void aceptaTokensDeIdentityFirmadosConHs384() throws Exception {
        String sub = UUID.randomUUID().toString();
        Jwt jwt = decoder.decode(firmar(JWSAlgorithm.HS384, CLAVE, sub));
        assertThat(jwt.getSubject()).isEqualTo(sub);
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("SISTEMA");
    }

    @Test
    void sigueAceptandoTokensFirmadosConHs256() throws Exception {
        String sub = UUID.randomUUID().toString();
        assertThat(decoder.decode(firmar(JWSAlgorithm.HS256, CLAVE, sub)).getSubject()).isEqualTo(sub);
    }

    @Test
    void rechazaTokensFirmadosConOtraClave() throws Exception {
        String token = firmar(JWSAlgorithm.HS384, "otra-clave-distinta-que-tambien-mide-mas-de-48-bytes!!".getBytes(), "x");
        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }
}
