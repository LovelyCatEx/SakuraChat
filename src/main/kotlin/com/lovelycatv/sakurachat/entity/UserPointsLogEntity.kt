/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.sakurachat.types.DatabaseTableType
import com.lovelycatv.sakurachat.types.PointsChangesReason
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "user_points_logs", schema = "sakurachat")
class UserPointsLogEntity(
    override var id: Long = 0,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(name = "delta_points", nullable = false)
    var deltaPoints: Long = 0,
    @Column(name = "reason_type", nullable = false)
    var reasonType: Int = 0,
    @Column(name = "related_table_type_1")
    var relatedTableType1: Int? = null,
    @Column(name = "related_table_id_1")
    var relatedTableId1: Long? = null,
    @Column(name = "related_table_type_2")
    var relatedTableType2: Int? = null,
    @Column(name = "related_table_id_2")
    var relatedTableId2: Long? = null,
    @Column(name = "related_table_type_3")
    var relatedTableType3: Int? = null,
    @Column(name = "related_table_id_3")
    var relatedTableId3: Long? = null,
    @Column(name = "related_table_type_4")
    var relatedTableType4: Int? = null,
    @Column(name = "related_table_id_4")
    var relatedTableId4: Long? = null,
    override var createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
) : BaseEntity() {
    @JsonIgnore
    fun getRealReasonType(): PointsChangesReason {
        return PointsChangesReason.getByReasonId(this.reasonType)
            ?: throw IllegalArgumentException("Unknown points changes reason type $this")
    }


    @JsonIgnore
    fun getRelatedRecord(index: Int): Record {
        val (tableType, recordId) = when (index) {
            1 -> this.relatedTableType1 to this.relatedTableId1
            2 -> this.relatedTableType2 to this.relatedTableId2
            3 -> this.relatedTableType3 to this.relatedTableId3
            4 -> this.relatedTableType4 to this.relatedTableId4
            else -> throw IllegalArgumentException("Invalid index: $index")
        }

        if (tableType == null || recordId == null) {
            throw IllegalStateException("Related table type or id cannot be null")
        }

        return Record(
            type = DatabaseTableType.getById(tableType)
                ?: throw IllegalStateException("Table type $tableType not found"),
            id = recordId,
        )
    }

    data class Record(
        val type: DatabaseTableType,
        val id: Long
    )
}