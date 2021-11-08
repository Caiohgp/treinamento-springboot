package projeto.projetospringboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.projetospringboot2.domain.Anime;
import projeto.projetospringboot2.domain.ProjectUser;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository <ProjectUser, Long> {
    ProjectUser findByUsername(String username);
}
