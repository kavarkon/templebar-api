{
  description = "templebar api";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-26.05";
    flake-utils.url = "github:numtide/flake-utils";
    gradle2nix = {
      url = "github:tadfisher/gradle2nix/v2";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = {
    self,
    nixpkgs,
    gradle2nix,
    flake-utils,
  }:
    flake-utils.lib.eachDefaultSystem (
      system: let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [];
        };
        javaVersion = 21;
        jdk = pkgs.jdk21;
      in {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            gradle2nix.packages."${system}".default
            jdk
            gradle
            spring-boot-cli
            jdt-language-server
            lombok
          ];

          JWT_SECRET = "🐗 (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits 🐗";

          shellHook = ''
            export JAVA_HOME=${jdk}
            export PATH="${jdk}/bin:$PATH"
            export GRADLE_OPTS="-Dorg.gradle.java.home=${jdk}"
            export DB_URL="jdbc:postgresql://localhost:5432/templebar"
            export DB_USERNAME="postgres"
            export DB_PASSWORD="password"
            export JDTLS_JVM_ARGS="-javaagent:${pkgs.lombok}/share/java/lombok.jar"

            echo "Spring Boot Resume Builder development environment"
            echo "JDK version: ${toString javaVersion}"
            echo "JDK path: $JAVA_HOME"
            java --version
            gradle --version

            # Ensure Gradle uses the correct JDK
            mkdir -p .gradle
            echo "org.gradle.java.home=${jdk}" >> .gradle/gradle.properties
          '';
        };

        packages.default = gradle2nix.builders.x86_64-linux.buildGradlePackage {
          pname = "spring-templebar-api";
          version = "0.1.0";
          lockFile = ./gradle.lock;
          src = ./.;

          gradleBuildFlags = [ "build" "-x" "test" ];

          installPhase = ''
            mkdir -p $out/java
            cp build/libs/*.jar $out/java/
          '';
        };
      }
    );
}
