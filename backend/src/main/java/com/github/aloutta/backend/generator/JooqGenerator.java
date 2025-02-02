package com.github.aloutta.backend.generator;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.extensions.ddl.DDLDatabase;
import org.jooq.meta.jaxb.*;

class JooqGenerator {
  static void generate(String outputDir) throws Exception {
    Generator generator =
        new Generator()
            .withTarget(target(outputDir))
            .withDatabase(database())
            .withGenerate(generate());
    GenerationTool.generate(new Configuration().withGenerator(generator));
  }

  private static Target target(String directory) {
    return new Target()
        .withPackageName("com.github.aloutta.backend.jooq")
        .withDirectory(directory + "/jooq");
  }

  private static Database database() {
    Property scripts =
        new Property().withKey("scripts").withValue("src/main/resources/db/migration/*.sql");
    Property sort = new Property().withKey("sort").withValue("semantic");
    return new Database().withName(DDLDatabase.class.getName()).withProperties(scripts, sort);
  }

  private static Generate generate() {
    return new Generate().withDaos(true).withSpringDao(true).withSpringAnnotations(true);
  }
}
