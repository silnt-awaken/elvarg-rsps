# Makefile for simple commands needed for rsps
# run, clean, build

run_client:
	cd ElvargClient && ./gradlew run

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

reset:
	git reset --hard origin/master