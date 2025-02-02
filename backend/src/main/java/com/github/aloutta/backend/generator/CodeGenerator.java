package com.github.aloutta.backend.generator;

public class CodeGenerator {
  public static void main(String[] args) throws Exception {
    String resourceDir = args[0];
    String outputDir = args[1];
    OpenApiGenerator.generate(resourceDir, outputDir);
    JooqGenerator.generate(outputDir);
  }
}
