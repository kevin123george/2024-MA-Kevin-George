# Use an official Tomcat image with Java 17
FROM tomcat:10.1-jdk17-corretto

# Remove the default ROOT webapp from Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy the WAR file to the Tomcat webapps directory
COPY build/libs/cattleDB-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose the default Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
