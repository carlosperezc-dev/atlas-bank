package com.atlas.bank.atlas_bank.archtest;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.atlas.bank.atlas_bank",
        importOptions = ImportOption.DoNotIncludeTests.class
)
public class SecurityIssolationTest {

    //el dominio no puede depender de spring security
    @ArchTest
    static final ArchRule domain_should_not_depend_on_spring_security =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("org.springframework.security..")
                    .because("La seguridad es un adapter de infraestructura - el dominoio no sabe que keycloak existe");

    @ArchTest
    static final ArchRule application_should_not_depend_on_spring_security =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("org.springframework.security..")
                    .because("La autorización se resuelve en el adapter antes de invocar el use case");


}
