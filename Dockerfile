FROM maven:latest


# Set the working directory in the container
WORKDIR /usr/src/mymaven

# Copy the Maven project files into the container
COPY . /usr/src/mymaven

RUN mvn dependency:go-offline

# Specify the default command to run when the container starts
CMD ["mvn", "mvn clean surefire-report:report"]