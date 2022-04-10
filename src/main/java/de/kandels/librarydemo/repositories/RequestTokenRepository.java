package de.kandels.librarydemo.repositories;

import de.kandels.librarydemo.entities.RequestToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestTokenRepository extends JpaRepository<RequestToken, Long> {
    Optional<RequestToken> findOneByRequestToken(String requestToken);
}