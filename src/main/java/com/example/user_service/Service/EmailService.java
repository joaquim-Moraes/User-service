package com.example.user_service.Service;


import org.springframework.stereotype.Service;

import com.sendgrid.Request;
import com.sendgrid.Method;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.sendgrid.Response;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
@Service
public class EmailService {
    
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.template.id}")
    private String templateId;

    public void sendVerificationEmail(String toEmail, String token){
        Mail mail = new Mail();
        mail.setFrom(new Email(fromEmail));
        mail.setTemplateId(templateId);
        
        Personalization personalization = new Personalization();
        personalization.addTo(new Email(toEmail));
        

        personalization.addDynamicTemplateData("codigo", token);
        
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try{
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

                    System.out.println("E-mail enviado para " + toEmail + ". Status Code: " + response.getStatusCode());

            // Se o SendGrid retornar um erro (ex: 401, 403), lança uma exceção
            if (response.getStatusCode() >= 400) {
                System.err.println("Erro do SendGrid: " + response.getBody());
                throw new RuntimeException("Erro ao enviar e-mail: " + response.getBody());
            }
        

        } catch (IOException ex) {
            // Erro de rede/conexão
            System.err.println("Erro de IO ao enviar e-mail: " + ex.getMessage());
            throw new RuntimeException("Erro de conexão ao enviar e-mail", ex);
        }
    }
    }

