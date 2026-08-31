package com.credit.bladi.unittest.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ArchitectureTest {
    private static final String ROOT = "com.credit.bladi";

    private static JavaClasses classes;

    @BeforeAll
    static void importProductionClasses() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT);
    }

    private static String layer(String name) {
        return ROOT + "." + name + "..";
    }

    @Test
    void domainModelOnlyDependsOnItselfExceptionsAndTheJdk() {
        classes().that().resideInAPackage(layer("model"))
            .should().onlyDependOnClassesThat().resideInAnyPackage(layer("model"), layer("exception"), "java..")
            .check(classes);
    }

    @Test
    void servicesDependOnPortsNeverOnAdapters() {
        noClasses().that().resideInAPackage(layer("service"))
            .should().dependOnClassesThat().resideInAnyPackage(layer("controller"), layer("datasource"),
                layer("infrastructure"), layer("batch"), "org.springframework.batch..", "jakarta.persistence..",
                "org.springframework.web..", "jakarta.servlet..")
            .check(classes);
    }

    @Test
    void repositoriesNeverReachUpIntoServicesOrControllers() {
        noClasses().that().resideInAPackage(layer("repository"))
            .should().dependOnClassesThat().resideInAnyPackage(layer("service"), layer("controller"),
                layer("infrastructure"), layer("batch"))
            .check(classes);
    }

    @Test
    void datasourcesOnlyKnowEntitiesModelsAndSharedTypes() {
        noClasses().that().resideInAPackage(layer("datasource"))
            .should().dependOnClassesThat().resideInAnyPackage(layer("service"), layer("controller"),
                layer("repository"), layer("infrastructure"), layer("batch"))
            .check(classes);
    }

    @Test
    void controllersGoThroughServicesNeverStraightToPersistence() {
        noClasses().that().resideInAPackage(layer("controller"))
            .should().dependOnClassesThat().resideInAnyPackage(layer("repository"), layer("datasource"),
                layer("batch"))
            .check(classes);
    }

    @Test
    void nothingOutsideTheWebLayerDependsOnControllers() {
        noClasses().that().resideOutsideOfPackages(layer("controller"), layer("config"), layer("exception"))
            .should().dependOnClassesThat().resideInAPackage(layer("controller"))
            .check(classes);
    }

    @Test
    void dependenciesAreInjectedThroughConstructors() {
        fields().should().notBeAnnotatedWith(Autowired.class).check(classes);
    }
}
