# ---------- Stage 1: Build JAR ----------
    FROM maven:3.9-eclipse-temurin-21 AS builder

    WORKDIR /app
    
    # คัดลอกไฟล์ Maven
    COPY pom.xml .
    COPY src ./src
    
    # สร้าง jar (ข้าม test ให้ build เร็วขึ้น)
    RUN mvn -q -DskipTests package
    
    
    # ---------- Stage 2: Run JAR ----------
    FROM eclipse-temurin:21-jre
    
    WORKDIR /app
    
    # คัดลอก jar จาก stage แรกมาเป็น app.jar
    COPY --from=builder /app/target/*.jar app.jar
    
    # Render จะเซ็ต PORT ให้ เรา default 8080 เผื่อรัน local ใน container
    ENV PORT=8080
    EXPOSE 8080
    
    ENTRYPOINT ["java", "-jar", "app.jar"]    