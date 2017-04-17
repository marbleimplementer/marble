#import packages
#from pyimagesearch.tempimage import TempImage
from picamera.array import PiRGBArray
from picamera import PiCamera
import argparse
import warnings
import datetime
import imutils
import json
import time
import cv2

#construct argument parser and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-c", "--conf", required=True, help="path to json configuration file")
args = vars(ap.parse_args())

#filter warnings and load configuration
warnings.filterwarnings("ignore")
conf = json.load(open(args["conf"]))

#initialize the camera and grab a reference to the raw camera capture
camera = PiCamera()
camera.resolution = tuple(conf["resolution"])
camera.framerate = conf["fps"]
rawCapture = PiRGBArray(camera, size=tuple(conf["resolution"]))

#allow the camera to warmup, then iniitalize the average frame, last upload timestamp, and the frame motion counter
print ("[INFO] warming up...")
time.sleep(conf["camera_warmup_time"])
avg = None
lastUploaded = datetime.datetime.now()
motionCounter = 0

#capture frames from the camera
for f in camera.capture_continuous(rawCapture, format="bgr", use_video_port=True):
    #grab the raw Numpy array representing the image and initialoize tyhe timestamp and occupied/uoccupied
    frame = f.array
    timestamp = datetime.datetime.now()
    text = "Unoccupied"

    #rezsize, convert to grayscale and blur the frame
    frame = imutils.resize(frame, width=500)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (21,21), 0)

    #if the average frame is None, initalize it
    if avg is None:
        print ("[INFO] starting background model...")
        avg = gray.copy().astype("float")
        rawCapture.truncate(0)
        continue

    #accumulate the weighted average between the current and previous frames, then compute the difference between the current frame and running averages
    cv2.accumulateWeighted(gray, avg, 0.5)
    frameDelta = cv2.absdiff(gray, cv2.convertScaleAbs(avg))

    #threshold the delta image, dilate the threshold image to fill in holes, then fiond contours on thresholded image
    thresh = cv2.threshold(frameDelta, conf["delta_thresh"], 255, cv2.THRESH_BINARY)[1]
    thresh = cv2.dilate(thresh, None, iterations=2)
    (img, cnts, hrcy) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    #loop over the contours
    for c in cnts:
        #if the contour is small,. ignore
        area = cv2.contourArea(c)
        if area < conf["min_area"]:
            continue

        #compute the bounding box for the contour. draw it on the frame and update the text
        (x, y, w, h) = cv2.boundingRect(c)
        cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 0, 255), 2)
        text = "Occupied"

    #draw the text and timestamp on the frame
    ts = timestamp.strftime( "%A %d %B %Y %I:%M:%S:%p")
    cv2.putText(frame, "Room Status: {}".format(text), (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
    cv2.putText(frame, ts, (10, frame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)

    #check to see if the frames should be displayed to screen
    if conf["show_video"]:
        #display the secuirity feed
        cv2.imshow("Secuirity Feed", frame)
        key = cv2.waitKey(1) & 0xFF

        #if the 'q' key is pressed, break the loop
        if key == ord("q"):
            break

    #clear stream in preperation of the next frame
    rawCapture.truncate(0)
    


