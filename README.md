# blog-bytecode

mvn archetype:generate \\n  -DinteractiveMode=false \\n  -DarchetypeGroupId=org.openjdk.jmh \\n  -DarchetypeArtifactId=jmh-java-benchmark-archetype \\n  -DgroupId=org.sample \\n  -DartifactId=test \\n  -Dversion=1.0

mvn clean verify
java -jar target/benchmarks.jar
