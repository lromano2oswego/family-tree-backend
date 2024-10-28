package com.family_tree.familytree;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import com.family_tree.familytree.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {
    //Check if user already exists by email address
    Optional<User> findByEmail(String email);
}
