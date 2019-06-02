package com.minimalist.todo.utils

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.Security
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class Gmail(private val user: String, private val password: String): javax.mail.Authenticator() {

    private val session: Session
    private val multipart = MimeMultipart()

    init {
        Security.addProvider(JSSEProvider())
        val props = Properties().apply {
            setProperty("mail.transport.protocol", "smtp")
            setProperty("mail.host", "smtp.gmail.com")
            setProperty("mail.smtp.quitwait", "false")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.socketFactory.fallback", "false")
        }
        session = Session.getDefaultInstance(props, this)
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(mailSubject: String, body: String,
                 mailSender: String, recipients: String) {
        try {
            val handler = DataHandler(ByteArrayDataSource(body.toByteArray(), "text/plain"))
            val messageBodyPart = MimeBodyPart().apply { setText(body) }
            multipart.addBodyPart(messageBodyPart)
            val message = MimeMessage(session).apply {
                subject = mailSubject
                dataHandler = handler
                setFrom(InternetAddress(mailSender, mailSender))
                setContent(multipart)
                if (recipients.indexOf(',') > 0) {
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients))
                } else {
                    setRecipient(Message.RecipientType.TO, InternetAddress(recipients))
                }
            }
            Transport.send(message)
        } catch (e: Exception) {
        }
    }


    @Throws(Exception::class)
    fun addAttachment(filename: String) {
        val source = FileDataSource(filename)
        val messageBodyPart = MimeBodyPart().apply { dataHandler = DataHandler(source); this.fileName = fileName }
        multipart.addBodyPart(messageBodyPart)
    }


    inner class ByteArrayDataSource(val data: ByteArray, val type: String?) : DataSource {

        override fun getName(): String {
            return "ByteArrayDataSource"
        }

        override fun getOutputStream(): OutputStream {
            throw IOException("Not Supported")
        }

        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        override fun getContentType(): String {
            return type ?: "application/octet-stream"
        }

    }

}