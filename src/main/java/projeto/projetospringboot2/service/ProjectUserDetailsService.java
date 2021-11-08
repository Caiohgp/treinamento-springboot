package projeto.projetospringboot2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projeto.projetospringboot2.repository.ProjectUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectUserDetailsService implements UserDetailsService {
    private final ProjectUserRepository projectUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username){
        return Optional.ofNullable(projectUserRepository.findByUsername(username))
                .orElseThrow(()-> new UsernameNotFoundException("User not Found"));
    }
}
