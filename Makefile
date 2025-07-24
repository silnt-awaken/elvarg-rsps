# Makefile for simple commands needed for rsps
# run, clean, build

# Load environment variables from .env file
include .env
export

run_client:
ifeq ($(local),true)
	cd ElvargClient && ./gradlew run -DSERVER_ADDRESS=localhost -DSERVER_PORT=43595 -DPRODUCTION_MODE=false
else
	cd ElvargClient && ./gradlew run -DSERVER_ADDRESS=$(SERVER_ADDRESS) -DSERVER_PORT=$(SERVER_PORT) -DPRODUCTION_MODE=$(PRODUCTION_MODE)
endif

clean_client:
	cd ElvargClient && ./gradlew clean

build_client:
	cd ElvargClient && ./gradlew build

run_server:
	cd ElvargServer && ./gradlew run

clean:
	cd ElvargServer && ./gradlew clean

build:
	cd ElvargServer && ./gradlew build
