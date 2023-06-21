package com.unyt.unytbankapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Unyt Bank Application",
                description = "Backend rest API for unyt bank app",
                version = "v1.0",
                contact = @Contact(
                        name = "Stephen Oluboyo",
                        email = "stevol2015@gmail.com",
                        url = "https://github.com/iamstevol/unyt_bank_app"
                ),
                license = @License(
                        name = "Unyt Bank App",
                        url = "https://github.com/iamstevol/unyt_bank_app"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Unyt bank App Documentation",
                url = "https://github.com/iamstevol/unyt_bank_app"
        )
)
public class UnytBankAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnytBankAppApplication.class, args);
    }

}
