package com.example.zing.authen

import java.util.Properties
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Mail (private val email: String, private val password: String){

    private val properties: Properties = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.socketFactory.port", "465")
        put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        put("mail.smtp.auth", "true")
        put("mail.smtp.port", "465")
    }

    private val session: Session = Session.getInstance(properties, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(email, password)
        }
    })
    private var subject: String = ""
    private var body: String = ""
    private var to: Array<String> = emptyArray()

    fun set_subject(sub: String) {
        subject = sub
    }

    fun setBody(content: String) {
        body = content
    }

    fun set_to(recipients: Array<String>) {
        to = recipients
    }

    fun send(): Boolean {
        return try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(email))
                setRecipients(Message.RecipientType.TO, to.map { InternetAddress(it) }.toTypedArray())
                setSubject(subject)
                setText(body)
            }
            Transport.send(message)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}