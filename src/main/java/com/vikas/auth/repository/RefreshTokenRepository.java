package com.vikas.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vikas.auth.entity.RefreshTokenEntity;

/**
 * Class      : RefreshTokenRepository
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
	/**
	 * 
	 * @param token
	 * @return
	 */
    Optional<RefreshTokenEntity> findByToken(String token);
}