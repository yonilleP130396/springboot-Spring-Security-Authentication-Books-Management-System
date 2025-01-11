package javaman.testApp.Repository;

import javaman.testApp.Model.myUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MyUserRepository extends JpaRepository<myUser,Long> {

    Optional<myUser> findByUsername(String username);
}
