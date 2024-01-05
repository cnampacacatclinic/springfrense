package fr.educentre.demo.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtUserService extends UserService{
    String generateJwtForUser(UserDetails user);
    UserDetails getUserFromJwt(String jwt);
}
//Remarque : Permet de découpler l'implémentation de la vérification utilisateur avec le JWT, peut-etre mocker dans
//les tests unitaires