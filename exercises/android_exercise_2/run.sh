#!/bin/bash

export APPIUM_SERVER="${APPIUM_SERVER:-http://127.0.0.1:4723}"

export SCREENSHOT_DIR="${SCREENSHOT_DIR:-/screenshots}"

# Create a virtual display
Xvfb :5 &
export DISPLAY=:5.0

# Start adb server
adb start-server

# Start appium 
appium &

# Start emulator
emulator64-x86 -avd nexus -netdelay none -netspeed full > /dev/null 2>&1 &

# Don't exit until emulator is loaded
output=''
while [[ ${output:0:7} != 'stopped' ]]; do
  output=`adb shell getprop init.svc.bootanim`
  sleep 1
done


# Copy dependencies
cp -R /exercise/.m2 /root/

# Make writeable directory for compiling test
mkdir /exercise-run/
cp -R /exercise/* /exercise-run/
 
touch /feedback/out
touch /feedback/err

# Run tests
echo "<h3>Test Results</h3>" >> /feedback/out
mvn -o -q -f /exercise-run/QRCodeTest/pom.xml test -P QRCodeTest >> /test-output

ERROR=`awk '/ERROR/{print NR;exit}' /test-output`

# Send mvn [ERROR] lines to err file
if [ -n "$ERROR" ]
then
   pre head -n `expr $ERROR - 1` /test-output >> /feedback/out
   pre tail -n +${ERROR} /test-output >> /feedback/err
else
   pre cat /test-output >> /feedback/out
fi

# Send screenshot
convert -format "jpg" -quality 40 -resize 288x480  /screenshots/step.png /screenshots/step.jpg
echo "<h3>Screenshot</h3>" >> /feedback/out
echo "<img src=\"data:image/jpeg;base64,"$(base64 -w 0 /screenshots/step.jpg)"\" />" >> /feedback/out

echo "<h3>Android Logcat Output</h3>" >> /feedback/out
pre adb logcat -s MCC -d >> /feedback/out

# Send results back to A+
grade
