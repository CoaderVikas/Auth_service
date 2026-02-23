package com.vikas.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.entity.UserOtpEntity;

import jakarta.transaction.Transactional;
/**
 * 
 * Class      : UserOtpRepository
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 21, 2026
 * Version    : 1.0
 */
@Repository
public interface UserOtpRepository extends JpaRepository<UserOtpEntity, Long> {
	/**
	 * 
	 * @param user
	 * @param purpose
	 * @return
	 */
    Optional<UserOtpEntity> 
    findTopByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            UserEntity user,
            String purpose
    );

    /**
     * 
     * @param user
     * @return
     */
    List<UserOtpEntity> 
    findByUserAndUsedFalse(UserEntity user);

   
   /**
    * 
    * @param now
    */
    @Transactional
    @Modifying
    @Query("DELETE FROM UserOtpEntity o WHERE o.expiryTime < :now")
    void deleteExpiredOtps(LocalDateTime now);
    
   
    @Modifying
    @Transactional
    @Query("DELETE FROM UserOtpEntity u WHERE u.expiryTime < :expiryTime OR u.used = true")
    void deleteOldOtps(@Param("expiryTime") LocalDateTime expiryTime);
}