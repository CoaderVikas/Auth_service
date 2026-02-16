package com.vikas.auth.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
* Class      : UserEntity
* Description: [Add brief description here]
* Author     : Vikas Yadav
* Created On : Feb 17, 2026
* Version    : 1.0
*/


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
@ToString
public abstract class AuditableEntity {
	
	@CreatedDate
    @Column(name = "CREATED_DATE", updatable = false)
	@CreationTimestamp
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE",insertable = false)
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "UPDATED_BY",insertable = false)
    private String updatedBy;
}