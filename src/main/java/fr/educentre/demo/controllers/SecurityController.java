package fr.educentre.demo.controllers;

import fr.educentre.demo.dto.AuthRequestDto;
import fr.educentre.demo.dto.AuthResponseDto;
import fr.educentre.demo.exceptions.AccountExistsException;
import fr.educentre.demo.exceptions.UnauthorizedException;
import fr.educentre.demo.services.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SecurityController {
    @Autowired
    private JwtUserService userService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody AuthRequestDto dto) throws AccountExistsException {
        UserDetails user = userService.save(dto.getUsername(),
                dto.getPassword());
        String token = userService.generateJwtForUser(user);
        return ResponseEntity.ok(new AuthResponseDto(user,token));
    }
//Remarque : ajouter un nouvel utilisateur et génère un JWT à la volée

    @PostMapping("/authorize")
    public ResponseEntity<AuthResponseDto> authorize(@RequestBody AuthRequestDto
                                                          requestDto) throws UnauthorizedException {
        Authentication authentication = null;
        try {
            authentication = userService.authenticate(requestDto.getUsername(),
                    requestDto.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);
// Token generation
            UserDetails user = (UserDetails) authentication.getPrincipal();
            String token = userService.generateJwtForUser(user);
            return ResponseEntity.ok(new AuthResponseDto(user, token));
        } catch(AuthenticationException e) {
            throw new UnauthorizedException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//Remarque: authentifie le principal (le user) à partir du JWT.
}


