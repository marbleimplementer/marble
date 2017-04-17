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

HAAR_CASCADE_PATH = "haarcascade_frontalface_alt.xml"

#construct argument parser and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-c", "--conf", required=True, help="path to json configuration file")
args = vars(ap.parse_args())

#filter warnings and load configuration
warnings.filterwarnings("ignore")
conf = json.load(open(args["conf"]))

#define  boundaries of green
greenLower = (29, 86, 6)
greenUpper = (64, 255, 255)

#define  boundaries of blue
blackLower = (0, 0, 0)
blackUpper = (180, 255, 30)

#define  boundaries of blue
blueLower = (110, 50, 50)
blueUpper = (130, 255, 255)

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
    faceFrame = gray
    gray = cv2.GaussianBlur(gray, (21,21), 0)

    #convert frame to hsv color space for color detection
    hsv  = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    #construct a mask for the color green then perform a series of dilations and erosions to remoce any small blob left in the mask
    mask = cv2.inRange(hsv, greenLower, greenUpper)
    mask = cv2.erode(mask, None, iterations=2)
    mask = cv2.dilate(mask, None, iterations=2)

    mask1 = cv2.inRange(hsv, blueLower, blueUpper)
    mask1 = cv2.erode(mask1, None, iterations=2)
    mask1 = cv2.dilate(mask1, None, iterations=2)
    
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

    #find contours in the mask and initialize the current center of the object
    counts= cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[-2]
    counts1= cv2.findContours(mask1.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[-2]


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

    #loop over the contours for the color object
    if len(counts) > 0:
        cc = max(counts, key=cv2.contourArea)
        ((xc,yc), radius) = cv2.minEnclosingCircle(cc)

        #only proceed if radius meets a minimum size
        if radius > 20:
            #draw the cicle and centroid on the frame and then update the list of tracked points
            cv2.circle(frame, (int(xc), int(yc)), int(radius), (0, 255, 255), 2)
    
    if len(counts1) > 0:
        cc1= max(counts1, key=cv2.contourArea)
        ((xc1,yc1), radius1) = cv2.minEnclosingCircle(cc1)
        

        #only proceed if radius meets a minimum size
        if radius1 > 20:
            #draw the cicle and centroid on the frame and then update the list of tracked points
            cv2.circle(frame, (int(xc1), int(yc1)), int(radius1), (255, 0, 255), 2)
            

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
    


