package fr.educentre.demo.services.impl;
import java.util.Date;
import fr.educentre.demo.domain.Owner;
import fr.educentre.demo.exceptions.AccountExistsException;
import fr.educentre.demo.repositories.OwnerRepository;
import fr.educentre.demo.services.JwtUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class JwtUserServiceImpl implements JwtUserService {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final String signingKey;
    public JwtUserServiceImpl(@Value("${jwt.signing.key}") String signingKey) {
        this.signingKey = signingKey;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws
            UsernameNotFoundException {
        Owner owner = ownerRepository.findByLogin(username);
        if (owner == null) {
            throw new UsernameNotFoundException("The owner could not be found");
        }
        return owner;
    }

    // USED FOR REGISTRATION

    @Override
    public Authentication authenticate(String username, String password) throws Exception {
        Authentication authentication = new
                UsernamePasswordAuthenticationToken(username, password);
        return authenticationConfiguration.getAuthenticationManager().authenticate(authentication);
    }


    @Override
    public UserDetails save(String username, String password) throws AccountExistsException {
        UserDetails existingUser = ownerRepository.findByLogin(username);
        if (existingUser != null) {
            throw new AccountExistsException();
        }
        Owner owner = new Owner();
        owner.setLogin(username);
        owner.setPassword(passwordEncoder.encode(password));
        ownerRepository.save(owner);
        return owner;

    }

    //USED FOR AUTHENTIFICATION

    @Override
    public UserDetails getUserFromJwt(String jwt) {
        String username = getUsernameFromToken(jwt);
        return loadUserByUsername(username);
    }

    private String getUsernameFromToken(String token) {
        System.out.println(signingKey);
        Claims claims =
                Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


    @Override
    public String generateJwtForUser(UserDetails user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600 * 1000);
        return
                Jwts.builder().setSubject(user.getUsername()).setIssuedAt(now).setExpiration(expiryDate)
                        .signWith(SignatureAlgorithm.HS512, signingKey)
                        .compact();
    }

}
