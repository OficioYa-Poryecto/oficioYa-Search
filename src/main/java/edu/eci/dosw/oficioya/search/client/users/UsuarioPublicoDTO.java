package edu.eci.dosw.oficioya.search.client.users;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Datos públicos de una cuenta tal como los entrega Users en {@code GET /api/v1/users?ids=} (RF-10).
 * Search solo usa el nombre; el resto de campos se ignora y nunca se exponen correo ni teléfono.
 *
 * @param id   id (UUID) de la cuenta, el mismo {@code userId} que Worker guarda en cada trabajador
 * @param name nombre completo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioPublicoDTO(UUID id, String name) {
}
