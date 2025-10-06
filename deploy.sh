set -e 

echo "🚀 Building backend JAR..."
cd backend
mvn clean package -DskipTests

cd ..
echo "🐳 Building and running Docker containers..."
docker compose up --build
