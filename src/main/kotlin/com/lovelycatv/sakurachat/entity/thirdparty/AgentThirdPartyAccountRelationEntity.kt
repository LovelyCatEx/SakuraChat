/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.thirdparty

import jakarta.persistence.*

@Entity
@Table(name = "agent_third_party_account_relations")
data class AgentThirdPartyAccountRelationEntity(
    @EmbeddedId
    val primaryKey: PrimaryKey
) {
    @Embeddable
    data class PrimaryKey(
        @Column(name = "agent_id", nullable = false)
        val agentId: Long,
        @Column(name = "third_party_account_id", nullable = false)
        val thirdPartyAccountId: Long
    )
}