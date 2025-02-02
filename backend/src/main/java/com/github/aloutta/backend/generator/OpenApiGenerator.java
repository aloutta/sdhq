package com.github.aloutta.backend.generator;

import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

class OpenApiGenerator {
  static void generate(String resourceDir, String outputDir) {
    CodegenConfigurator codegenConfigurator =
        new CodegenConfigurator()
            .setGeneratorName("spring")
            .setInputSpec(resourceDir + "/openapi.yml")
            .setOutputDir(outputDir + "/openapi")
            .setApiPackage("com.github.aloutta.backend.api")
            .setModelPackage("com.github.aloutta.backend.model")
            .setIgnoreFileOverride(resourceDir + "/.openapi-generator-ignore")
            .addAdditionalProperty("useSpringBoot3", true)
            .addAdditionalProperty("delegatePattern", true);
    new DefaultGenerator().opts(codegenConfigurator.toClientOptInput()).generate();
  }
}
