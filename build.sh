# Clean up old build artifacts
rm -rf out/* SecretScanner.jar manifest.txt

# Ensure out directory exists
mkdir out

# Compile all Java files to an "out" directory
javac -d out scanner/*.java

# Create a manifest file with the fully qualified main class
echo -e "Main-Class: scanner.SecretScanner\n" > manifest.txt

# Create the JAR, preserving the package structure
jar cfm SecretScanner.jar manifest.txt -C out .
