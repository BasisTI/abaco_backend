package br.com.basis.abaco.security;

import br.com.basis.abaco.domain.Perfil;
import br.com.basis.abaco.domain.Permissao;
import br.com.basis.abaco.domain.User;
import br.com.basis.abaco.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        Optional<User> userFromDatabase = userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin);

        return userFromDatabase.map(user -> {
            if (!user.isActivated()) {
                throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
            }

            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            for(Perfil perfil : user.getPerfils()){
                for(Permissao permissao : perfil.getPermissaos()){
                    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ABACO_" + permissao.getFuncionalidadeAbaco().getSigla() + "_"+ permissao.getAcao().getSigla()));
                }
            }
            return new UserDetailsCustom(lowercaseLogin,
                user.getPassword(),
                grantedAuthorities,user);
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
            "database"));
    }
}
