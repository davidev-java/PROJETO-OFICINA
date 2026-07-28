package com.novanest.projetooficina.security;

import com.novanest.projetooficina.entity.Usuario;
import com.novanest.projetooficina.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioService usuarioService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        Usuario usuario = usuarioService.buscarOuCriarPorEmail(email, nome, avatarUrl);

        return new AuthenticatedOAuth2User(oAuth2User, usuario);
    }
}