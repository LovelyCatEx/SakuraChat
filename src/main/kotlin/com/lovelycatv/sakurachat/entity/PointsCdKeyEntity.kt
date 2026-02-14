/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "points_cdkeys")
@SQLDelete(sql = "UPDATE points_cdkeys SET deleted_time = ROUND(UNIX_TIMESTAMP(CURTIME(3)) * 1000) WHERE id = ?")
@SQLRestriction(BaseEntity.SOFT_NON_DELETED_RESTRICTION)
class PointsCdKeyEntity(
    override val id: Long = 0,
    @Column(name = "cdkey", length = 64, nullable = false)
    var cdKey: String = "",
    @Column(name = "points", nullable = false)
    var points: Long = 0,
    @Column(name = "generated_by", nullable = false)
    var generatedBy: Long = 0,
    @Column(name = "used_by", nullable = true)
    var usedBy: Long? = null,
    override val createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
): BaseEntity()