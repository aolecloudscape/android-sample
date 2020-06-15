#!/bin/bash

export APPIUM_SERVER="${APPIUM_SERVER:-http://127.0.0.1:4723}"

export SCREENSHOT_DIR="${SCREENSHOT_DIR:-/screenshots}"

# Start adb server
adb start-server

# Start appium 
appium &

# Start emulator
xvfb-run emulator64-x86 -avd nexus -netdelay none -netspeed full > /dev/null 2>&1 &

# Don't exit until emulator is loaded
output=''
while [[ ${output:0:7} != 'stopped' ]]; do
  output=`adb shell getprop init.svc.bootanim`
  sleep 1
done

# close System UI is not available error
adb shell input keyevent 4

# Install test cases app
adb install /exercise/test_case_select.apk

# Copy dependencies
cp -R /exercise/.m2 /root/

# Make writeable directory for compiling test
mkdir /exercise-run/
cp -R /exercise/* /exercise-run/
 
touch /feedback/out
touch /feedback/err

# Run tests
echo "<h3>Test Results</h3>" >> /feedback/out
mvn -o -q -f /exercise-run/VisionTest/pom.xml test -P VisionExerciseTest >> /test-output

# Send mvn [ERROR] lines to err file
ERROR=`awk '/ERROR/{print NR;exit}' /test-output`

if [ -n "$ERROR" ]
then
   pre head -n `expr $ERROR - 1` /test-output >> /feedback/out
   pre tail -n +${ERROR} /test-output >> /feedback/err
else
   pre cat /test-output >> /feedback/out
fi

# Compress and send screenshots
echo "<h3>Screenshot</h3>" >> /feedback/out
for d in `ls ${SCREENSHOT_DIR}`; do
   echo "<h5>Test " ${d%.*} "</h5>" >> /feedback/out
      convert -format "jpg" -quality 40 -resize 288x480  ${SCREENSHOT_DIR}/${d} ${SCREENSHOT_DIR}/${d%.*}.jpg
      echo "<img src=\"data:image/jpeg;base64,"$(base64 -w 0 ${SCREENSHOT_DIR}/${d%.*}.jpg)"\" />" >> /feedback/out
done;

# Get package name
#package=$(./build-tools/25.0.3/aapt dump badging /submission/user/application.apk | grep package:\ name | grep -oP  "name='\K([^\s]*)" | sed 's/.$//')

# Send app logs
echo "<h3>Android Logcat Output</h3>" >> /feedback/out
pre adb logcat -s MCC -d >> /feedback/out

# Print exceptions
echo "<pre>" >>/feedback/out
adb logcat '*:E' -d | awk '/beginning of crash/,0' | tail -n +2 | grep AndroidRuntime>> /feedback/out
echo "</pre>" >>/feedback/out

#pre adb logcat -d >> /feedback/out

#sed -i 's/TotalPoints: .*/TotalPoints: 0\/6/'  /feedback/out

# Send results back to A+
grade
