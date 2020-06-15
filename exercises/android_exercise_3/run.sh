#!/bin/bash

export APPIUM_SERVER="${APPIUM_SERVER:-http://127.0.0.1:4723}"

export SCREENSHOT_DIR="${SCREENSHOT_DIR:-/screenshots}"

# Start adb server
adb start-server

# Start appium 
appium &

# enable hardware keyboard
sed -i 's/hw.keyboard=no/hw.keyboard=yes/'  /root/.android/avd/nexus.avd/config.ini

# Start emulator
xvfb-run emulator64-x86 -avd nexus -netdelay none -netspeed full > /dev/null 2>&1 &

# Don't exit until emulator is loaded
output=''
while [[ ${output:0:7} != 'stopped' ]]; do
  output=`adb shell getprop init.svc.bootanim`
  sleep 1
done

# press back button in case there is a system error alert on startup (happens sometimes)
adb shell input keyevent 4

# disable on-screen keyboard
adb shell settings put secure show_ime_with_hard_keyboard 0

# Copy dependencies
cp -R /exercise/.m2 /root/

# Make writeable directory for compiling test
mkdir /exercise-run/
cp -R /exercise/* /exercise-run/
 
touch /feedback/out
touch /feedback/err

# Start local web server with JSON images
cd /exercise/images
python -m SimpleHTTPServer 9923 &
export JSON_PHOTOS_LOCATION="${JSON_PHOTOS_LOCATION:-http://10.0.2.2:9923/photos.json}"

# Run tests
echo "<h3>Test Results</h3>" >> /feedback/out
mvn -o -q -f /exercise-run/ImageDownloadTest/pom.xml test -P ImageDownloadTest >> /test-output

# Send mvn [ERROR] lines to err file
ERROR=`awk '/ERROR/{print NR;exit}' /test-output`

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

# Send app logs
echo "<h3>Android Logcat Output</h3>" >> /feedback/out
pre adb logcat -s MCC -d >> /feedback/out
 
# Send results back to A+
grade
