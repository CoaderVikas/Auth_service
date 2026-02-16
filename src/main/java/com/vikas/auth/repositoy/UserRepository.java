package com.vikas.auth.repositoy;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vikas.auth.entity.UserEntity;

/**
 * Class      : UserRepository
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * 
     * @param username
     * @return
     */
	Optional<UserEntity> findByUsername(String username);
	
	/**
	 * 
	 * @param username
	 * @return
	 */
    boolean existsByUsername(String username);
}