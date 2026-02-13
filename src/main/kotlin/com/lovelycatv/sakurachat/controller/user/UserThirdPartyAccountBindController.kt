/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.user

import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.service.UserThirdPartyAccountBindService
import com.lovelycatv.sakurachat.types.UserAuthentication
import com.lovelycatv.sakurachat.utils.toPageable
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/third-party-account")
@Validated
class UserThirdPartyAccountBindController(
    private val userThirdPartyAccountBindService: UserThirdPartyAccountBindService,
    private val thirdPartyAccountService: ThirdPartyAccountService
) {
    @GetMapping("/list")
    fun getUserThirdPartyAccounts(
        userAuthentication: UserAuthentication,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        val accountMap = mutableMapOf<Long, ThirdPartyAccountEntity>()
        return ApiResponse.success(
            this.userThirdPartyAccountBindService
                .getRepository()
                .findByUserId(
                    userAuthentication.userId,
                    pageQuery.toPageable()
                )
                .also {
                    val account = thirdPartyAccountService
                        .getByIds(it.toList().map { it.primaryKey.thirdPartyAccountId })

                    account.forEach {
                        accountMap[it.id] = it
                    }
                }
                .toPaginatedResponseData {
                    accountMap[it.primaryKey.thirdPartyAccountId]!!
                }
        )
    }

    @PostMapping("/bind")
    fun bindUserThirdPartyAccount(
        userAuthentication: UserAuthentication,
        code: String
    ): ApiResponse<*> {
        this.userThirdPartyAccountBindService.bind(
            userId = userAuthentication.userId,
            code = code
        )

        return ApiResponse.success(null)
    }

    @PostMapping("/unbind")
    fun bindUserThirdPartyAccount(
        userAuthentication: UserAuthentication,
        @RequestParam("id")
        thirdPartyAccountEntityId: Long
    ): ApiResponse<*> {
        this.userThirdPartyAccountBindService.unbind(
            userId = userAuthentication.userId,
            thirdPartyAccountEntityId = thirdPartyAccountEntityId
        )

        return ApiResponse.success(null)
    }
}