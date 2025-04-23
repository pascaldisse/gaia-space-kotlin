#!/bin/bash

# Define colors for log output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
RESET='\033[0m'

# Define log file
LOG_FILE="gaia-space-debug.log"

echo -e "${BLUE}========== GAIA SPACE KOTLIN - DEBUG RESTART ==========${RESET}"
echo -e "${YELLOW}Starting with extensive logging enabled${RESET}"
echo -e "${YELLOW}Logs will be saved to ${LOG_FILE}${RESET}"

# Kill any running instance on port 8081
echo -e "${YELLOW}Checking for processes on port 8081...${RESET}"
PID=$(lsof -ti:8081)
if [ -n "$PID" ]; then
  echo -e "${RED}Killing process $PID running on port 8081${RESET}"
  kill -9 $PID
fi

# Clean build
echo -e "${YELLOW}Cleaning previous build...${RESET}"
./gradlew clean

# Run with extensive logging
echo -e "${GREEN}Starting application with debug logging...${RESET}"
./gradlew bootRun --args='--spring.profiles.active=dev --server.port=8081 --logging.level.org.springframework=DEBUG --logging.level.com.gaiaspace=TRACE --logging.level.org.hibernate.SQL=DEBUG --logging.level.org.hibernate.type.descriptor.sql=TRACE --debug' | tee -a $LOG_FILE &

# Wait for the application to start
echo -e "${YELLOW}Waiting for application to start up...${RESET}"
ATTEMPTS=0
MAX_ATTEMPTS=30

while [ $ATTEMPTS -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8081/actuator/health 2>&1 | grep -q "UP"; then
        echo -e "${GREEN}Application is up and running!${RESET}"
        break
    fi
    
    ATTEMPTS=$((ATTEMPTS+1))
    echo -e "${YELLOW}Waiting for startup... ($ATTEMPTS/$MAX_ATTEMPTS)${RESET}"
    sleep 2
done

if [ $ATTEMPTS -eq $MAX_ATTEMPTS ]; then
    echo -e "${RED}Application failed to start in the expected time.${RESET}"
    echo -e "${YELLOW}Check the log file at $LOG_FILE for details.${RESET}"
    exit 1
fi

# Display application URLs
echo -e "${GREEN}==================================================${RESET}"
echo -e "${GREEN}Gaia Space is running with the following URLs:${RESET}"
echo -e "${BLUE}Main Application:     ${GREEN}http://localhost:8081/${RESET}"
echo -e "${BLUE}Dashboard:            ${GREEN}http://localhost:8081/dashboard${RESET}"
echo -e "${BLUE}GaiaScript Editor:    ${GREEN}http://localhost:8081/gaiascript${RESET}"
echo -e "${BLUE}H2 Database Console:  ${GREEN}http://localhost:8081/h2-console${RESET}"
echo -e "${BLUE}API Documentation:    ${GREEN}http://localhost:8081/swagger-ui.html${RESET}"
echo -e "${BLUE}Actuator Endpoints:   ${GREEN}http://localhost:8081/actuator${RESET}"
echo -e "${GREEN}==================================================${RESET}"
echo -e "${YELLOW}Log file: $LOG_FILE${RESET}"
echo -e "${YELLOW}Press Ctrl+C to stop the application${RESET}"

# Keep script running until user terminates
wait