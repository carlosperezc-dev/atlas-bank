package com.atlas.bank.atlas_bank.archtest;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "com.atlas.bank.atlas_bank",
        importOptions = ImportOption.DoNotIncludeTests.class
)

public class NamingConventionTest {
    //Los controllers deben vivir en adapter.in.rest
    @ArchTest
    static final ArchRule controller_should_reside_in_rest_adapter =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..infrastructure.adapter.in..")
                    .because("Los controller son adaptadores de entrada HTTP" +
                            "- su lugares infrastructure/adapter/in/rest");

    //Regla para que los Usecase esté en application.port.in
    @ArchTest
    static final ArchRule use_case_should_reside_in_application_port_in =
            classes()
                    .that().haveSimpleNameEndingWith("UseCase")
                    .should().resideInAPackage("..application.port.in..")
                    .because("Los use cases son puertos de entrada - " +
                            "viven en application/port/int");

    @ArchTest
    static final ArchRule port_in_should_be_interfaces =
            classes()
                    .that().resideInAPackage("..application.port.in..")
                    .should().beInterfaces()
                    .because("Un puerto es un contrato - siempre una interfaz, " +
                            "nunca una implementación");

    @ArchTest
    static final ArchRule port_out_should_be_interfaces =
            classes()
                    .that().resideInAPackage("..application.port.out..")
                    .should().beInterfaces()
                    .because("Los puertos de salida definen qué necesita el dominio- " +
                            "la implementación va en infraestructura");

}
