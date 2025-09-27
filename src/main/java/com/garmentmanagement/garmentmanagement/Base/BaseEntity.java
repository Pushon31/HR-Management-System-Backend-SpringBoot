package com.garmentmanagement.garmentmanagement.Base;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class BaseEntity extends BaseAuditEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp  // save এর সময় অটো সেট হবে
    @Column(updatable = false) //update kora jabe na
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;  // update এর সময় অটো আপডেট হবে





}
