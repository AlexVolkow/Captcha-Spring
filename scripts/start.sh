#!/usr/bin/env bash
cd ../target
java -Dlog4j.configurationFile=log4j.xml -Dttl=30 -jar captcha.jar