package com.novanest.projetooficina.security;

import com.novanest.projetooficina.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

// Junta o OAuth2User (dados do Google) com o Usuario salvo no banco
@Getter
public class AuthenticatedOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final Usuario usuario;

    public AuthenticatedOAuth2User(OAuth2User oAuth2User, Usuario usuario) {
        this.oAuth2User = oAuth2User;
        this.usuario = usuario;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));
    }

    @Override
    public String getName() {
        return usuario.getEmail();
    }
}