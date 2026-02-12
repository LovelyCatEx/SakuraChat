/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.mail

import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.vertex.log.logger
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailServiceImpl(
    private val mailSenderContainer: JavaMailSenderContainer
) : MailService {
    private val logger = logger()

    override fun refreshSettings() {
        mailSenderContainer.refreshSettings()
    }

    override fun sendMail(to: String, subject: String, content: String) {
        try {
            val instance = mailSenderContainer.getInstance()

            val from = mailSenderContainer.getSystemSettings().mail.smtp.fromEmail

            val message: MimeMessage = instance.createMimeMessage()

            MimeMessageHelper(message, true, "UTF-8").apply {
                setFrom(from)
                setTo(to)
                setSubject(subject)
                setText(content, true)
            }

            instance.send(message)
        } catch (e: Exception) {
            logger.error("Send email to $to failed, subject: $subject, content: $content", e)
            throw BusinessException("Send email to $to failed, message: ${e.message}")
        }
    }

    override fun sendRegisterEmail(to: String, emailCode: String) {
        this.sendMail(
            to = to,
            subject = "SakuraChat Registration",
            content = """"
                <!DOCTYPE html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>SakuraChat 验证码</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f7f9; color: #333333;">
                <table border="0" cellpadding="0" cellspacing="0" width="100%" style="table-layout: fixed;">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);">

                                <tr>
                                    <td style="background-color: #007bff; height: 6px;"></td>
                                </tr>

                                <tr>
                                    <td style="padding: 40px 30px;">
                                        <h2 style="margin: 0 0 20px; color: #007bff; font-size: 24px; font-weight: 600;">SakuraChat 账户注册</h2>

                                        <p style="margin: 0 0 15px; font-size: 16px; line-height: 1.6; color: #555555;">
                                            您好！感谢您注册 SakuraChat~
                                        </p>

                                        <p style="margin: 0 0 30px; font-size: 16px; line-height: 1.6; color: #555555;">
                                            请在注册页面输入下方的验证码以完成身份验证。该验证码在 <span style="font-weight: bold; color: #333;">10 分钟</span> 内有效：
                                        </p>

                                        <div style="background-color: #f0f7ff; border: 1px dashed #007bff; border-radius: 4px; padding: 25px; text-align: center; margin-bottom: 30px;">
                                                <span style="font-size: 36px; font-family: 'Courier New', Courier, monospace; font-weight: bold; color: #007bff; letter-spacing: 10px;">
                                                    $emailCode
                                                </span>
                                        </div>

                                        <p style="margin: 0 0 15px; font-size: 14px; line-height: 1.6; color: #888888;">
                                            如果您没有尝试进行注册操作，请忽略此邮件。为了您的账号安全，请勿将验证码转发或透露给他人。
                                        </p>

                                        <div style="border-top: 1px solid #eeeeee; padding-top: 25px; margin-top: 30px;">
                                            <p style="margin: 5px 0 0; font-size: 12px; color: #999999;">
                                                本邮件由系统自动发送，请勿直接回复。
                                            </p>
                                        </div>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="background-color: #f8f9fa; padding: 20px 30px; text-align: center;">
                                        <p style="margin: 0; font-size: 12px; color: #aaaaaa;">
                                            &copy; 2026 LovelyCat 保留所有权利。
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                </body>
                </html>
            """.trimIndent()
        )
    }

}