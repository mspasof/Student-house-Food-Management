package nl.tudelft.sem.authentication.repositories;

import java.util.Optional;
import nl.tudelft.sem.authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User as u set u.password = :newPassword where u.email = :email")
    void changePassword(@Param("newPassword") String newPassword, @Param("email") String email);
}
