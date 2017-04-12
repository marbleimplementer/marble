package com.example.marbleimplementer.marble;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.artoolkit.ar.base.rendering.ARRenderer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import android.view.ViewGroup.LayoutParams;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.KalmanFilter;
import org.w3c.dom.Text;

import static org.opencv.core.Core.norm;
import static org.opencv.core.Core.normalize;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imdecode;
import static org.opencv.imgproc.Imgproc.resize;



// TODO: 2. make the sphere with face image mapping
// TODO: 3. make the hand with rods


import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class MainActivity extends ARActivity implements SensorEventListener {

    Core.MinMaxLocResult minmax;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;

    private static int[][] fingerprintDatabase = new int[][]{
            {-74, -74, -75, -77, -83, -69, -78, -78},
            {-74, -74, -74, -76, -82, -72, -78, -76},
            {-68, -77, -74, -77, -84, -76, -84, -77},
            {-69, -74, -75, -85, -85, -72, -74, -77},
            {-68, -70, -74, -86, -79, -72, -77, -84},
            {-68, -72, -77, -88, -79, -78, -83, -85},
            {-71, -82, -77, -85, -82, -75, -81, -81},
            {-66, -80, -76, -83, -83, -78, -79, -81},
            {-68, -77, -76, -80, -83, -77, -75, -80},
            {-75, -75, -81, -79, -80, -78, -77, -80},
            {-81, -75, -79, -82, -79, -81, -78, -80},
            {-81, -75, -79, -82, -79, -81, -78, -80},
            {-73, -70, -80, -80, -86, -77, -77, -82},
            {-73, -70, -81, -74, -86, -74, -77, -82},
            {-72, -71, -79, -83, -85, -78, -75, -83},
            {-69, -70, -79, -80, -85, -76, -78, -75},
            {-69, -72, -80, -80, -85, -76, -77, -76},
            {-71, -73, -82, -80, -85, -76, -76, -75},
            {-74, -77, -78, -81, -81, -69, -80, -77},
            {-74, -73, -77, -81, -81, -68, -77, -77},
            {-72, -69, -76, -80, -81, -67, -75, -76},
            {-73, -75, -76, -82, -81, -80, -75, -78},
            {-72, -74, -76, -82, -81, -78, -75, -78},
            {-72, -72, -76, -81, -80, -74, -77, -78},
            {-77, -75, -79, -83, -80, -82, -77, -80},
            {-77, -77, -78, -83, -80, -78, -78, -80},
            {-78, -81, -77, -84, -80, -74, -79, -81},
            {-79, -71, -79, -81, -88, -77, -76, -81},
            {-77, -68, -81, -80, -86, -79, -77, -80},
            {-76, -71, -81, -81, -86, -81, -77, -81},
            {-70, -75, -77, -78, -79, -80, -77, -81},
            {-71, -76, -77, -76, -80, -81, -77, -81},
            {-71, -76, -76, -75, -80, -79, -79, -81},
            {-70, -76, -81, -81, -81, -75, -81, -77},
            {-70, -76, -80, -82, -81, -77, -80, -75},
            {-69, -76, -78, -83, -81, -78, -80, -74},
            {-72, -72, -78, -82, -79, -78, -80, -80},
            {-70, -71, -80, -81, -77, -74, -78, -80},
            {-70, -71, -81, -81, -77, -73, -77, -80},
            {-69, -78, -79, -82, -81, -74, -81, -79},
            {-73, -84, -77, -78, -81, -72, -81, -81},
            {-73, -84, -76, -78, -78, -71, -81, -81},
            {-74, -69, -78, -76, -80, -69, -79, -79},
            {-76, -72, -76, -79, -79, -76, -80, -79},
            {-76, -73, -76, -80, -79, -77, -80, -83},
            {-72, -74, -78, -77, -85, -74, -79, -80},
            {-75, -70, -78, -84, -86, -70, -76, -86},
            {-75, -71, -78, -84, -86, -69, -76, -86},
            {-71, -73, -76, -85, -82, -81, -85, -83},
            {-69, -76, -76, -82, -80, -81, -86, -82},
            {-69, -78, -75, -79, -79, -80, -86, -82},
            {-64, -71, -73, -83, -81, -80, -83, -76},
            {-64, -71, -73, -84, -81, -79, -82, -76},
            {-64, -70, -72, -84, -84, -79, -83, -75},
            {-67, -76, -72, -81, -81, -78, -78, -80},
            {-70, -76, -72, -81, -81, -78, -77, -80},
            {-71, -75, -72, -81, -83, -82, -75, -80},
            {-75, -75, -77, -81, -83, -72, -77, -80},
            {-77, -76, -76, -85, -83, -72, -77, -79},
            {-81, -76, -77, -85, -81, -73, -83, -79},
            {-76, -77, -78, -82, -78, -78, -79, -81},
            {-75, -75, -76, -79, -78, -76, -80, -81},
            {-75, -70, -73, -77, -77, -75, -76, -81},
            {-67, -80, -79, -83, -79, -73, -87, -81},
            {-67, -79, -78, -83, -79, -72, -87, -81},
            {-67, -79, -77, -83, -79, -71, -87, -84},
            {-70, -75, -78, -79, -75, -74, -78, -80},
            {-70, -73, -73, -76, -87, -75, -83, -82},
            {-71, -73, -73, -75, -87, -76, -84, -82},
            {-69, -72, -74, -82, -84, -70, -81, -79},
            {-73, -72, -76, -85, -85, -69, -78, -83},
            {-75, -70, -80, -87, -86, -75, -76, -83},
            {-74, -82, -81, -83, -85, -76, -81, -86},
            {-74, -86, -82, -83, -86, -78, -80, -85},
            {-72, -79, -80, -84, -84, -75, -78, -83},
            {-75, -76, -77, -77, -79, -79, -82, -80},
            {-77, -76, -79, -76, -77, -79, -84, -82},
            {-77, -76, -79, -76, -77, -77, -85, -82},
            {-76, -76, -73, -76, -75, -75, -82, -83},
            {-74, -76, -75, -77, -74, -73, -80, -81},
            {-74, -76, -77, -78, -74, -71, -79, -81},
            {-73, -78, -79, -75, -78, -74, -85, -82},
            {-73, -82, -76, -74, -80, -75, -86, -83},
            {-72, -79, -76, -75, -79, -77, -86, -86},
            {-77, -76, -79, -77, -77, -76, -81, -84},
            {-74, -74, -79, -86, -76, -74, -83, -83},
            {-73, -72, -82, -85, -76, -75, -80, -82},
            {-74, -78, -81, -82, -85, -72, -85, -83},
            {-75, -78, -78, -82, -85, -72, -83, -81},
            {-74, -78, -75, -81, -79, -73, -82, -79},
            {-74, -68, -74, -77, -77, -67, -88, -85},
            {-74, -69, -73, -76, -77, -67, -86, -85},
            {-69, -76, -73, -82, -77, -74, -81, -85},
            {-67, -76, -77, -76, -86, -77, -77, -89},
            {-69, -73, -78, -76, -80, -80, -80, -76},
            {-69, -73, -76, -77, -79, -83, -79, -76},
            {-79, -73, -75, -84, -78, -77, -79, -81},
            {-79, -71, -76, -81, -78, -78, -78, -81},
            {-78, -70, -76, -80, -78, -80, -79, -81},
            {-70, -73, -76, -81, -78, -79, -83, -83},
            {-69, -71, -75, -78, -79, -75, -85, -81},
            {-69, -72, -76, -74, -80, -75, -85, -81},
            {-70, -75, -84, -78, -77, -75, -83, -83},
            {-70, -75, -85, -80, -80, -76, -87, -83},
            {-69, -80, -81, -84, -80, -77, -88, -83},
            {-72, -80, -76, -80, -77, -72, -79, -86},
            {-73, -81, -74, -81, -76, -72, -78, -85},
            {-75, -81, -73, -82, -76, -71, -75, -85},
            {-73, -78, -76, -81, -82, -77, -82, -80},
            {-71, -73, -81, -83, -84, -74, -75, -81},
            {-72, -74, -81, -84, -84, -72, -75, -79},
            {-69, -72, -79, -87, -81, -72, -81, -83},
            {-69, -72, -77, -86, -81, -76, -81, -80},
            {-72, -73, -76, -84, -81, -79, -81, -79},
            {-73, -77, -76, -87, -85, -74, -76, -80},
            {-71, -76, -75, -84, -85, -75, -76, -80},
            {-70, -76, -75, -82, -85, -75, -76, -80},
            {-76, -79, -78, -82, -82, -78, -82, -81},
            {-76, -78, -76, -80, -82, -78, -82, -84},
            {-77, -78, -75, -78, -82, -78, -81, -84},
            {-82, -73, -79, -76, -83, -73, -82, -75},
            {-82, -74, -79, -80, -85, -74, -83, -75},
            {-82, -75, -79, -84, -85, -74, -87, -78},
            {-78, -73, -79, -80, -88, -74, -82, -78},
            {-78, -74, -80, -76, -87, -73, -81, -78},
            {-78, -74, -80, -76, -86, -73, -82, -78},
            {-74, -74, -79, -79, -80, -79, -84, -81},
            {-76, -73, -79, -78, -81, -81, -84, -81},
            {-76, -73, -81, -77, -81, -83, -83, -81},
            {-69, -75, -82, -82, -80, -73, -81, -76},
            {-67, -73, -85, -80, -82, -74, -80, -76},
            {-66, -72, -85, -80, -82, -75, -76, -74},
            {-65, -78, -81, -80, -83, -74, -76, -82},
            {-66, -80, -81, -80, -83, -75, -77, -82},
            {-66, -78, -77, -81, -84, -75, -78, -81},
            {-68, -81, -74, -79, -85, -69, -85, -82},
            {-68, -82, -77, -78, -85, -68, -86, -81},
            {-66, -84, -78, -78, -86, -68, -89, -81},
            {-70, -79, -78, -79, -85, -78, -80, -81},
            {-69, -80, -80, -79, -87, -79, -77, -81},
            {-74, -80, -80, -79, -87, -77, -77, -82},
            {-70, -78, -77, -83, -88, -81, -86, -82},
            {-69, -80, -78, -84, -88, -85, -86, -82},
            {-68, -80, -78, -87, -88, -87, -88, -82},
            {-66, -69, -75, -89, -81, -76, -77, -80},
            {-65, -66, -75, -90, -79, -73, -80, -79},
            {-64, -64, -74, -87, -77, -71, -83, -81},
            {-68, -69, -79, -82, -81, -76, -86, -78},
            {-70, -67, -79, -82, -81, -75, -88, -78},
            {-71, -65, -80, -84, -81, -74, -88, -78},
            {-67, -64, -80, -79, -87, -76, -82, -73},
            {-66, -68, -80, -81, -88, -76, -81, -73},
            {-67, -73, -81, -82, -86, -74, -83, -75},
            {-70, -74, -78, -82, -85, -78, -83, -76},
            {-71, -73, -76, -83, -84, -76, -83, -76},
            {-77, -71, -73, -84, -84, -74, -85, -76},
            {-76, -81, -78, -87, -86, -77, -83, -85},
            {-76, -80, -78, -87, -86, -78, -83, -86},
            {-76, -81, -78, -88, -86, -79, -82, -86},
            {-73, -77, -79, -80, -88, -74, -85, -86},
            {-72, -77, -80, -82, -88, -74, -85, -86},
            {-72, -77, -81, -82, -88, -74, -87, -86},
            {-69, -77, -72, -86, -89, -73, -85, -87},
            {-69, -77, -72, -86, -89, -73, -85, -87},
            {-67, -71, -73, -81, -86, -71, -80, -82},
            {-66, -71, -70, -87, -89, -77, -82, -80},
            {-64, -70, -70, -87, -88, -78, -84, -80},
            {-64, -71, -71, -86, -88, -78, -85, -80},
            {-68, -74, -76, -83, -79, -76, -82, -81},
            {-68, -74, -77, -82, -79, -76, -83, -81},
            {-68, -74, -79, -84, -79, -77, -83, -81},
            {-65, -81, -79, -80, -84, -83, -84, -86},
            {-66, -81, -76, -81, -84, -81, -84, -86},
            {-66, -81, -76, -83, -84, -80, -84, -86},
            {-68, -77, -77, -83, -78, -79, -80, -80},
            {-74, -76, -78, -81, -79, -80, -80, -80},
            {-74, -76, -79, -78, -81, -83, -82, -80},
            {-75, -79, -82, -76, -82, -75, -83, -80},
            {-73, -79, -80, -78, -82, -75, -83, -80},
            {-73, -78, -80, -78, -82, -73, -82, -83},
            {-76, -76, -77, -78, -82, -79, -81, -85},
            {-74, -78, -70, -81, -88, -73, -80, -82},
            {-76, -77, -69, -80, -88, -74, -81, -82},
            {-67, -73, -76, -81, -88, -78, -83, -83},
            {-67, -73, -76, -81, -88, -78, -83, -83},
            {-67, -72, -75, -79, -85, -75, -85, -80},
            {-66, -69, -75, -78, -82, -73, -79, -76},
            {-64, -69, -75, -77, -81, -72, -78, -76},
            {-64, -68, -75, -77, -81, -72, -78, -76},
            {-69, -76, -80, -82, -83, -76, -82, -81},
            {-69, -76, -80, -82, -83, -76, -82, -81},
            {-69, -76, -80, -83, -82, -75, -82, -81},
            {-69, -78, -76, -74, -84, -76, -82, -81},
            {-69, -77, -76, -75, -84, -75, -85, -79},
            {-67, -78, -76, -76, -87, -72, -86, -79},
            {-74, -77, -73, -82, -87, -75, -77, -78},
            {-74, -77, -73, -82, -87, -75, -77, -78},
            {-75, -76, -70, -84, -87, -74, -76, -78},
            {-72, -76, -75, -84, -89, -72, -77, -82},
            {-72, -77, -76, -84, -84, -72, -76, -80},
            {-72, -80, -72, -85, -85, -80, -80, -83},
            {-70, -75, -74, -82, -84, -80, -82, -80},
            {-71, -74, -75, -82, -84, -80, -82, -80},
            {-72, -74, -76, -83, -84, -80, -81, -78},
            {-65, -76, -75, -81, -80, -76, -78, -81},
            {-65, -79, -75, -82, -82, -76, -78, -83},
            {-65, -80, -75, -82, -82, -75, -80, -84},
            {-64, -80, -77, -80, -83, -76, -77, -81},
            {-64, -79, -76, -77, -83, -77, -76, -81},
            {-63, -80, -76, -77, -81, -80, -78, -79},
            {-68, -72, -77, -86, -87, -80, -85, -82},
            {-68, -72, -77, -82, -87, -80, -85, -82},
            {-70, -75, -76, -85, -86, -80, -85, -82}
    };
    private static String[] locations = new String[]{"00", "01", "02", "03", "04", "05", "06",
            "10", "11", "12", "13", "14", "15",
            "20", "21", "22", "23", "24", "25",
            "30", "31", "32", "33", "34", "35",
            "40", "41", "42", "43", "44", "45",
            "50", "51", "52", "53", "54", "55", "56",
            "60", "61", "62", "63", "64", "65", "66",
            "70", "71", "72", "73", "74", "75", "76",
            "80", "81", "82", "83", "84", "85", "86",
            "90", "91", "92", "93", "94", "95",
            "a0", "a1", "a2", "a3", "a4", "a5"};

    private BeaconManager beaconManager;
    private Region region0;
    private Region region1;
    private Region region2;
    private Region region3;
    private Region region4;
    private Region region5;
    private Region region6;
    private Region region7;

    TextView beacon0;
    TextView beacon1;
    TextView beacon2;
    TextView beacon3;
    TextView beacon4;
    TextView beacon5;
    TextView beacon6;
    TextView beacon7;

    private String closestUUID = "no";
    private Map<String, Integer> beaconDistance = new HashMap<>();
    private SimpleRenderer simpleRenderer = new SimpleRenderer();
    Map<String, Integer> beaconAngle = new HashMap<String, Integer>();
    Map<String, String> beaconUUID = new HashMap<>();

    // IMU related variables
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor, mAccelerationSensor, mGravitySensor, mMagSensor;
    // new
    private float[] gravityValues = null;
    private float[] magneticValues = null;

    private final float[] mRotationMatrix = new float[16];
    private volatile float[] mAccelerometerMatrix = new float[4];

    private float rotationX = 0;
    private float rotationY = 0;
    private float rotationZ = 0;

    private TextView rotationMsg;

    public String getClosestUUID() {
        return closestUUID;
    }

    CaptureCameraPreview cam;
    Button button;
    byte[] myframe;

    private MyCaptureCameraPreview preview;
    FrameLayout mainLayout2;
    ImageView imageView;

    Mat image;
    Mat mask;
    FeatureDetector detector;

    // control translation
    Button xup;
    Button xdown;
    Button yup;
    Button ydown;
    Button zup;
    Button zdown;

    // control rotation
    Button r1up;
    Button r1down;
    Button r2up;
    Button r2down;
    Button r3up;
    Button r3down;

    // record fingerprint
    private String filename = "fingerprint.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;
    Button record;
    Spinner spinner;
    Button read;

    // camera pose estimation
    private Mat currCameraPoseFromCam;
    private Mat currCameraPoseFromBeacon;
    private Mat currCameraPostFromIMU; //TODO: update the unit

    // match fingerprint
    Button match;

    // display current status
    TextView beaconLocation;
    TextView imuAngle;
    TextView camLocationAngle;

    // test
    Button test;

    // control variable
    boolean useIMU;
    Button imuon;
    Button reset;
    float[] prevR = null;
    private int currselection;

    // translation IMU
    static final float NS2S = 1.0f / 1000000000.0f;
    float[] last_values = null;
    float[] velocity = null;
    float[] position = null;
    float[] camtrans = null;
    long last_timestamp = 0;

    // UI
    int currspeed;
    int currfreq;
    Button speed;
    Button freq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleRenderer.setActivity(this);

        // texture
        Bitmap bitmap = BitmapFactory.decodeResource(this.getApplicationContext().getResources(),
                R.drawable.bumpy_bricks_public_domain);
        simpleRenderer.setBitmap(bitmap);

        mainLayout = (FrameLayout) this.findViewById(R.id.mainLayout);
        mainLayout2 = (FrameLayout) this.findViewById(R.id.mainLayout2);
        imageView = new ImageView(this);
        mainLayout2.addView(imageView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        beacon0 = (TextView) this.findViewById(R.id.beacon0);
        beacon1 = (TextView) this.findViewById(R.id.beacon1);
        beacon2 = (TextView) this.findViewById(R.id.beacon2);
        beacon3 = (TextView) this.findViewById(R.id.beacon3);
        beacon4 = (TextView) this.findViewById(R.id.beacon4);
        beacon5 = (TextView) this.findViewById(R.id.beacon5);
        beacon6 = (TextView) this.findViewById(R.id.beacon6);
        beacon7 = (TextView) this.findViewById(R.id.beacon7);

        beaconManager = new BeaconManager(this);

        region0 = new Region("ranged region",
                UUID.fromString("a75fa152-a904-4502-8ea8-192f8fcfee6a"), 37491, 43355); //1
        region1 = new Region("ranged region",
                UUID.fromString("9795a656-a244-47f5-b8ab-a24cf9728976"), 12703, 41115); //15
        region2 = new Region("ranged region",
                UUID.fromString("58deb431-0387-4aff-b04d-bf773f2409cc"), 918, 44776); //2
        region3 = new Region("ranged region",
                UUID.fromString("e14f37ee-cd9c-41a1-b145-134570f9a8e8"), 41600, 6645); //3
        region4 = new Region("ranged region",
                UUID.fromString("b6fc3980-846c-4f48-bfc5-2b2e0ca2d702"), 2143, 20816); //4
        region5 = new Region("ranged region",
                UUID.fromString("9aa9bca6-b207-41f5-a076-d294c9b374db"), 49523, 36217); //5
        region6 = new Region("ranged region",
                UUID.fromString("21f158a6-a083-4020-9b40-8cd34380ffc3"), 16355, 40884); //6
        region7 = new Region("ranged region",
                UUID.fromString("7691f1bd-284f-439d-8b1a-d223f0249b9b"), 30448, 19478); //7

        beaconAngle.put("a75fa152-a904-4502-8ea8-192f8fcfee6a", 0);
        beaconAngle.put("9795a656-a244-47f5-b8ab-a24cf9728976", 45);
        beaconAngle.put("58deb431-0387-4aff-b04d-bf773f2409cc", 90);
        beaconAngle.put("e14f37ee-cd9c-41a1-b145-134570f9a8e8", 135);
        beaconAngle.put("b6fc3980-846c-4f48-bfc5-2b2e0ca2d702", 180);
        beaconAngle.put("9aa9bca6-b207-41f5-a076-d294c9b374db", 225);
        beaconAngle.put("21f158a6-a083-4020-9b40-8cd34380ffc3", 270);
        beaconAngle.put("7691f1bd-284f-439d-8b1a-d223f0249b9b", 315);


        beaconUUID.put("0", "a75fa152-a904-4502-8ea8-192f8fcfee6a");
        beaconUUID.put("1", "9795a656-a244-47f5-b8ab-a24cf9728976");
        beaconUUID.put("2", "58deb431-0387-4aff-b04d-bf773f2409cc");
        beaconUUID.put("3", "e14f37ee-cd9c-41a1-b145-134570f9a8e8");
        beaconUUID.put("4", "b6fc3980-846c-4f48-bfc5-2b2e0ca2d702");
        beaconUUID.put("5", "9aa9bca6-b207-41f5-a076-d294c9b374db");
        beaconUUID.put("6", "21f158a6-a083-4020-9b40-8cd34380ffc3");
        beaconUUID.put("7", "7691f1bd-284f-439d-8b1a-d223f0249b9b");

        beaconDistance.put("0", -100);
        beaconDistance.put("1", -100);
        beaconDistance.put("2", -100);
        beaconDistance.put("3", -100);
        beaconDistance.put("4", -100);
        beaconDistance.put("5", -100);
        beaconDistance.put("6", -100);
        beaconDistance.put("7", -100);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    for (Beacon beacon : list) {
                        if (beacon.getProximityUUID().equals(UUID.fromString("a75fa152-a904-4502-8ea8-192f8fcfee6a"))) { // 1
                            Log.d("Airport", "Beacon 0 dist: " + String.valueOf(beacon.getRssi()));
                            beacon0.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("0", beacon.getRssi());
                        }
                        if (beacon.getProximityUUID().equals(UUID.fromString("9795a656-a244-47f5-b8ab-a24cf9728976"))) { // 15
                            Log.d("Airport", "Beacon 1 dist: " + String.valueOf(beacon.getRssi()));
                            beacon1.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("1", beacon.getRssi());
                        }
                        if (beacon.getProximityUUID().equals(UUID.fromString("58deb431-0387-4aff-b04d-bf773f2409cc"))) { // 2
                            Log.d("Airport", "Beacon 2 dist: " + String.valueOf(beacon.getRssi()));
                            beacon2.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("2", beacon.getRssi());
                        }
                        if (beacon.getProximityUUID().equals(UUID.fromString("e14f37ee-cd9c-41a1-b145-134570f9a8e8"))) { // 3
                            Log.d("Airport", "Beacon 3 dist: " + String.valueOf(beacon.getRssi()));
                            beacon3.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("3", beacon.getRssi());
                        }
                        if (beacon.getProximityUUID().equals(UUID.fromString("b6fc3980-846c-4f48-bfc5-2b2e0ca2d702"))) { // 4
                            Log.d("Airport", "Beacon 4 dist: " + String.valueOf(beacon.getRssi()));
                            beacon4.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("4", beacon.getRssi());
                        }
                        if (beacon.getProximityUUID().equals(UUID.fromString("9aa9bca6-b207-41f5-a076-d294c9b374db"))) { // 5
                            Log.d("Airport", "Beacon 5 dist: " + String.valueOf(beacon.getRssi()));
                            beacon5.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("5", beacon.getRssi());
                        }
                        if (beacon.getProximityUUID().equals(UUID.fromString("21f158a6-a083-4020-9b40-8cd34380ffc3"))) { // 6
                            Log.d("Airport", "Beacon 6 dist: " + String.valueOf(beacon.getRssi()));
                            beacon6.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("6", beacon.getRssi());
                        }
                        if (beacon.getProximityUUID().equals(UUID.fromString("7691f1bd-284f-439d-8b1a-d223f0249b9b"))) { // 7
                            Log.d("Airport", "Beacon 7 dist: " + String.valueOf(beacon.getRssi()));
                            beacon7.setText(String.valueOf(beacon.getRssi()) + "/" + String.valueOf(beacon.getMeasuredPower()));
                            beaconDistance.put("7", beacon.getRssi());
                        }

                    }
                }
                long startTime = System.currentTimeMillis();
                updateARObjDirection();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                Log.d("T", "BLE time:" + String.valueOf(elapsedTime));
            }
        });

        if (!checkCameraPermission()) {
            //if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) { //ASK EVERY TIME - it's essential!
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        // When the screen is tapped, inform the renderer and vibrate the phone
        mainLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                simpleRenderer.click();
                Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vib.vibrate(40);
            }
        });

        //IMU related stuff
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        mAccelerationSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_LINEAR_ACCELERATION);

        mGravitySensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_GRAVITY);
        mMagSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
        mSensorManager.registerListener(this, mAccelerationSensor, 5000);

        mSensorManager.registerListener(this, mGravitySensor, 10000);
        mSensorManager.registerListener(this, mMagSensor, 10000);

        rotationMsg = (TextView) this.findViewById(R.id.rotationMsg);

        //camera stuff
        cam = this.getCameraPreview();

        button = (Button) this.findViewById(R.id.button);
        currselection = 0;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cam = MainActivity.this.getCameraPreview();
                if (cam != null) {
                    Bitmap bitmap1 = MainActivity.this.preview.getBitmap();

                    //   Bitmap bitmap1g = toGrayscale(bitmap1);
                    //         Bitmap bitmap1f = locateFeaturePoint(bitmap1);

                    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.sample3);
                    //        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test0);
                    //       Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.box);
                    Bitmap bitmap2g = toGrayscale(bitmap2);
                    long startTime = System.currentTimeMillis();
                    Bitmap combinedBitmap = estimatePose(bitmap1, bitmap2g);

                    long stopTime = System.currentTimeMillis();
                    long elapsedTime = stopTime - startTime;
                    Log.d("T", "CAM time:" + String.valueOf(elapsedTime));

                    if (combinedBitmap != null) {
                        imageView.setImageBitmap(combinedBitmap);
                    } else {
                        Log.d("T", "bm null!" + String.valueOf(MainActivity.this.myframe.length));
                    }
                    Log.d("T", "cam not null");
                } else {
                    Log.d("T", "cam null");
                }
            }
        });

        // control translation
        xup = (Button) this.findViewById(R.id.button2);
        xdown = (Button) this.findViewById(R.id.button4);
        yup = (Button) this.findViewById(R.id.button5);
        ydown = (Button) this.findViewById(R.id.button6);
        zup = (Button) this.findViewById(R.id.button7);
        zdown = (Button) this.findViewById(R.id.button8);

        xup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.xup();
            }
        });
        xdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.xdown();
            }
        });
        yup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.yup();
            }
        });
        ydown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.ydown();
            }
        });
        zup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.zup();
            }
        });
        zdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.zdown();
            }
        });

        // control rotation
        r1up = (Button) this.findViewById(R.id.button9);
        r1down = (Button) this.findViewById(R.id.button12);
        r2up = (Button) this.findViewById(R.id.button10);
        r2down = (Button) this.findViewById(R.id.button13);
        r3up = (Button) this.findViewById(R.id.button11);
        r3down = (Button) this.findViewById(R.id.button14);
        r1up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.rotate1(5);
            }
        });
        r1down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.rotate1(-5);
            }
        });
        r2up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.rotate2(5);
            }
        });
        r2down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.rotate2(-5);
            }
        });
        r3up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.rotate3(5);
            }
        });
        r3down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.rotate3(-5);
            }
        });

        // record fingerprint
        record = (Button) this.findViewById(R.id.record);
        record.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream fos = new FileOutputStream(myExternalFile, true);
                    float[] angles = MainActivity.this.simpleRenderer.getCameraAngles();
                    //     float[] translation = MainActivity.this.simpleRenderer.getCameraTranslation();
                    //    ArrayList<Integer> fingerprint = MainActivity.this.getCurrentFingerPrint();
                    //    String entry = MainActivity.this.generateEntry(fingerprint);
                    String entry = MainActivity.this.generateCameraEntry(angles, camtrans);
                    fos.write(entry.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            record.setEnabled(false);
        } else {
            myExternalFile = new File(getExternalFilesDir(filepath), filename);
        }
        spinner = (Spinner) this.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fingerprints_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        read = (Button) this.findViewById(R.id.read);
        read.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "database: " + MainActivity.this.readFingerPrintDatabase());
            }
        }));

        // match fingerprint
        match = (Button) this.findViewById(R.id.match);
        match.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "match:");
                ArrayList<Integer> fingerprint = MainActivity.this.getCurrentFingerPrint();
                String location = MainActivity.this.matchFingerprint2(fingerprint);
                MainActivity.this.beaconLocation.setText("location: " + location);
            }
        }));

        // display current status
        beaconLocation = (TextView) this.findViewById(R.id.beaconPosition);
        imuAngle = (TextView) this.findViewById(R.id.imuAngle);
        camLocationAngle = (TextView) this.findViewById(R.id.camAnglePosition);

        // test
        test = (Button) this.findViewById(R.id.test);
        test.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.testAnglesToM();
            }
        }));

        // control stuff
        useIMU = true;
        imuon = (Button) this.findViewById(R.id.imuon);
        reset = (Button) this.findViewById(R.id.reset);
        imuon.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.this.useIMU) {
                    MainActivity.this.useIMU = false;
                } else {
                    MainActivity.this.useIMU = true;
                }
            }
        }));
        reset.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.reset();
            }
        }));

        camtrans = new float[]{0.0f, 0.0f, 0.0f};

        currspeed = 0;
        currfreq = 3;
        speed = (Button)this.findViewById(R.id.speed);
        freq = (Button)this.findViewById(R.id.freq);
        speed.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.this.simpleRenderer.resetPeople();;
//
//                if (currspeed == 0) {
//                  currspeed = 1;
//              } else {
//                  currspeed = 0;
//              }
                MainActivity.this.simpleRenderer.reset();
            }
        }));
        freq.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.simpleRenderer.resetPeople();;
                if (currfreq == 15) {
                    currfreq = 3;
                } else {
                    currfreq = currfreq + 3;
                }
            }
        }));
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    image = new Mat();
                    mask = new Mat();
                    detector = FeatureDetector.create(FeatureDetector.HARRIS);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private static Mat readInputStreamIntoMat(InputStream inputStream) throws IOException {
        // Read into byte-array
        byte[] temporaryImageInMemory = readStream(inputStream);
        // Decode into mat. Use any IMREAD_ option that describes your image appropriately
        Mat outputImage = imdecode(new MatOfByte(temporaryImageInMemory), IMREAD_GRAYSCALE);

        return outputImage;
    }

    private static byte[] readStream(InputStream stream) throws IOException {
        // Copy content of the image to byte-array
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] temporaryImageInMemory = buffer.toByteArray();
        buffer.close();
        stream.close();
        return temporaryImageInMemory;
    }

    public Bitmap locateFeaturePoint(Bitmap input) {
        Utils.bitmapToMat(input, image);
        Mat imagei = new Mat();
        Imgproc.cvtColor(image, imagei, Imgproc.COLOR_RGBA2GRAY);

        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        detector.detect(imagei, keypoints);

        Log.d("T", "image width:" + String.valueOf(imagei.width()) + " " + String.valueOf(imagei.height()));
        Log.d("T", "keypoints size: " + String.valueOf(keypoints.size()));

        Mat outputImage = new Mat();
        Scalar color = new Scalar(0, 0, 255); // BGR
        int flags = Features2d.DRAW_RICH_KEYPOINTS; // For each keypoint, the circle around keypoint with keypoint size and orientation will be drawn.
        Features2d.drawKeypoints(imagei, keypoints, outputImage, color, flags);
        Utils.matToBitmap(outputImage, input);

        return input;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private void updateARObjDirection() {
        // Construct a map between beacon and angle
        int largeRssi = -100;
        String largeId = "-1";
        for (int i = 0; i < 8; i++) {
            if (beaconDistance.get(String.valueOf(i)) > largeRssi) {
                largeId = String.valueOf(i);
                largeRssi = beaconDistance.get(String.valueOf(i));
            }
        }
        //   this.closestUUID = beaconUUID.get(largeId);
        this.closestUUID = largeId;
        return;
    }

    @Override
    public void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region0);
                beaconManager.startRanging(region1);
                beaconManager.startRanging(region2);
                beaconManager.startRanging(region3);
                beaconManager.startRanging(region4);
                beaconManager.startRanging(region5);
                beaconManager.startRanging(region6);
                beaconManager.startRanging(region7);
            }
        });

        preview = new MyCaptureCameraPreview(this, this);
        mainLayout.addView(preview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        // Load OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region0);
        beaconManager.stopRanging(region1);
        beaconManager.stopRanging(region2);
        beaconManager.stopRanging(region3);
        beaconManager.stopRanging(region4);
        beaconManager.stopRanging(region5);
        beaconManager.stopRanging(region6);
        beaconManager.stopRanging(region7);
        super.onPause();
        mainLayout.removeView(preview);
    }

//    private List<String> placesNearBeacon(Beacon beacon) {
//        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
//        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
//            return PLACES_BY_BEACONS.get(beaconKey);
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private double computeDistance(Beacon beacon) {
        int txPower = beacon.getMeasuredPower();
        int rssi = beacon.getRssi();
        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }

    @Override
    protected ARRenderer supplyRenderer() {
        if (!checkCameraPermission()) {
            Toast.makeText(this, "No camera permission - restart the app", Toast.LENGTH_LONG).show();
            return null;
        }
        return simpleRenderer;
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return this.mainLayout;
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    // IMU related methods
    public void onSensorChanged(SensorEvent event) {
        //      Log.d("T", "sensor changed!");
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (false && event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION && (gravityValues != null) && (magneticValues != null)) {
            float currAccelX = event.values[0];
            float currAccelY = event.values[1];
            float currAccelZ = event.values[2];

            float[] deviceRelativeAcceleration = new float[4];
            deviceRelativeAcceleration[0] = event.values[0];
            deviceRelativeAcceleration[1] = event.values[1];
            deviceRelativeAcceleration[2] = event.values[2];
            deviceRelativeAcceleration[3] = 0;

            float[] R = new float[16], I = new float[16], earthAcc = new float[16];

            SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues);

            float[] inv = new float[16];

            android.opengl.Matrix.invertM(inv, 0, R, 0);
            android.opengl.Matrix.multiplyMV(earthAcc, 0, inv, 0, deviceRelativeAcceleration, 0);
            if (velocity != null) {
                Log.d("V", "Values: (" + velocity[0] + ", " + velocity[1] + ", " + velocity[2] + ")");
            }
            if (last_values != null) {
                float dt = (event.timestamp - last_timestamp) * NS2S;
                float mean = (velocity[1] + velocity[0] + velocity[2]) / 3;

                for (int index = 0; index < 3; ++index) {
                    velocity[index] += (earthAcc[index] + last_values[index]) / 2 * dt;
                    velocity[index] = velocity[index] - mean;
                    position[index] = velocity[index] * dt;
                }
            } else {
                last_values = new float[3];
                velocity = new float[3];
                position = new float[3];
                velocity[0] = velocity[1] = velocity[2] = 0f;
                position[0] = position[1] = position[2] = 0f;
            }
            System.arraycopy(earthAcc, 0, last_values, 0, 3);
            last_timestamp = event.timestamp;
            // mAccelerometerMatrix[0] = event.values[0];
            // mAccelerometerMatrix[1] = event.values[1];
            // mAccelerometerMatrix[2] = event.values[2];
            // mAccelerometerMatrix[3] = 0;

            if (useIMU) {
                //      MainActivity.this.simpleRenderer.translate1(currAccelX * currAccelX * 0.1f);
                //      MainActivity.this.simpleRenderer.translate2(currAccelY * currAccelY * 0.1f);
                //      MainActivity.this.simpleRenderer.translate3(currAccelZ * currAccelZ * 0.1f);
                //    if (earthAcc[0] > 0) {
                MainActivity.this.simpleRenderer.translate1(position[2] * 100f);
                //    } else {
                //        MainActivity.this.simpleRenderer.translate1(position[0]  * 0.1f);
                //   }
                //     if (earthAcc[1] > 0) {
                MainActivity.this.simpleRenderer.translate3(position[1] * 100f);
                //    } else {
                //        MainActivity.this.simpleRenderer.translate3(position[1]  * -0.1f);
                //    }
                //    if (earthAcc[2] > 0) {
                MainActivity.this.simpleRenderer.translate2(position[0] * 100f);
                //     } else {
                //         MainActivity.this.simpleRenderer.translate2(position[2]  * -0.1f);
                //     }
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityValues = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values;
        }

        // rotation
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && (gravityValues != null) && (magneticValues != null)) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            if (useIMU) {
                long startTime = System.currentTimeMillis();

                float[] R = new float[16], I = new float[16];
                float[] angleChange = null;
                //         SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues);
                SensorManager.getRotationMatrixFromVector(R, event.values);
                if (prevR == null) {
                    prevR = R;
                } else {
                    angleChange = new float[3];
                    SensorManager.getAngleChange(angleChange, R, prevR);
                }

                float currRotationX = event.values[0];
                float currRotationY = event.values[1];
                float currRotationZ = event.values[2];

                float diffRotationX = rotationX - currRotationX;
                float diffRotationY = rotationY - currRotationY;
                float diffRotationZ = rotationZ - currRotationZ;
                if (useIMU) {
//                    MainActivity.this.simpleRenderer.rotate3(-diffRotationX * 180f);
//                    MainActivity.this.simpleRenderer.rotate2(-diffRotationY *180f);
//                    MainActivity.this.simpleRenderer.rotate1(-diffRotationZ *180f);
                    if (angleChange != null) {
                        MainActivity.this.simpleRenderer.rotate3(angleChange[0] * -180.f / (float) Math.PI);
                        MainActivity.this.simpleRenderer.rotate2(angleChange[1] * -180.f / (float) Math.PI);
                        MainActivity.this.simpleRenderer.rotate1(angleChange[2] * -180.f / (float) Math.PI);
                    }
                }

                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                // TODO: chong temp disable this
            //    Log.d("T", "IMU time:" + String.valueOf(elapsedTime));
                float[] angles = MainActivity.this.simpleRenderer.getCameraAngles();
                this.imuAngle.setText(String.valueOf(angles[0]) + " " + String.valueOf(angles[1]) + " " + String.valueOf(angles[2]));

                rotationX = currRotationX;
                rotationY = currRotationY;
                rotationZ = currRotationZ;
                prevR = R;
                rotationMsg.setText("rotation x: " + String.valueOf(rotationX) + "\n y: " +
                        String.valueOf(rotationY) + "\n z: " + String.valueOf(rotationZ));
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

//    @Override
//    public void cameraPreviewFrame(byte[] frame) {
//        super.cameraPreviewFrame(frame);
//     //   Log.d("T", "framelength:" + String.valueOf(frame.length));
//        this.myframe = frame;
//        this.getCameraPreview();
//    }

    @Override
    public CaptureCameraPreview getCameraPreview() {
        return preview;
    }

    // Camera pose related stuff
    public Bitmap estimatePose(Bitmap bitmap1, Bitmap bitmap2) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Utils.bitmapToMat(bitmap1, mat1);
        Size size = new Size(504, 378);//the dst image size,e.g.100x100
        Mat dst = new Mat();//dst image
        resize(mat1, dst, size);//resize image

        Utils.bitmapToMat(bitmap2, mat2);
        resize(mat2, dst, size);//resize image

///        Log.d("T", "bitmap1 size: " + String.valueOf(bitmap1.getHeight()) + " " + String.valueOf(bitmap1.getWidth()) );

        MatOfDMatch matches = new MatOfDMatch();
        MatOfDMatch gm = new MatOfDMatch();

        LinkedList<DMatch> goodMatches = new LinkedList<>();
        LinkedList<Point> objList = new LinkedList<>();
        LinkedList<Point> sceneList = new LinkedList<>();

        MatOfKeyPoint keyPointsObject = new MatOfKeyPoint();
        MatOfKeyPoint keyPointScene = new MatOfKeyPoint();

        Mat descriptorsObject = new Mat();
        Mat descriptorsScene = new Mat();

        MatOfPoint2f obj = new MatOfPoint2f();
        MatOfPoint2f scene = new MatOfPoint2f();

        FeatureDetector fd = FeatureDetector.create(FeatureDetector.ORB);
        fd.detect(mat1, keyPointsObject);
        fd.detect(mat2, keyPointScene); // find orb features

        int con = 3;
        DescriptorExtractor extractor = DescriptorExtractor.create(con);
        extractor.compute(mat1, keyPointsObject, descriptorsObject); // extract descriptor
        extractor.compute(mat2, keyPointScene, descriptorsScene);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matcher.match(descriptorsObject, descriptorsScene, matches); // match descriptor

        Log.d("T", "feature size info: " + String.valueOf(descriptorsObject.height()) + " " + String.valueOf(descriptorsObject.width()) + " " + String.valueOf(descriptorsObject.channels())
                + " " + String.valueOf(descriptorsScene.height()) + " " + String.valueOf(descriptorsScene.width()) + " " + String.valueOf(descriptorsScene.channels())
                + " " + String.valueOf(matches.height()) + " " + String.valueOf(matches.width()) + " " + String.valueOf(matches.channels()));

        double maxDist = 0;
        double minDist = 100;
        List<DMatch> matchesList = matches.toList();
        Log.d("T", "matches List: " + String.valueOf(matchesList.size()) + " " + String.valueOf(matchesList));
        for (int i = 0; i < descriptorsObject.rows(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < minDist) minDist = dist;
            if (dist > maxDist) maxDist = dist;
        }
        for (int i = 0; i < descriptorsObject.rows(); i++) {
            if (matchesList.get(i).distance < 2 * minDist) {
                goodMatches.addLast(matchesList.get(i)); // get good matches
            }
        }

        gm.fromList(goodMatches);

        List<KeyPoint> keyPointsObjectList = keyPointsObject.toList();
        List<KeyPoint> keyPointsSceneList = keyPointScene.toList();
        for (int i = 0; i < goodMatches.size(); i++) {
            objList.addLast(keyPointsObjectList.get(goodMatches.get(i).queryIdx).pt);
            sceneList.addLast(keyPointsSceneList.get(goodMatches.get(i).trainIdx).pt);
        }


        obj.fromList(objList);
        scene.fromList(sceneList);

        Mat outputImage = new Mat();
        Bitmap comboMap = combineImages(bitmap1, bitmap2);
        Utils.bitmapToMat(comboMap, outputImage);

        Mat mat1rgb = new Mat();
        Mat mat2rgb = new Mat();
        Imgproc.cvtColor(mat1, mat1rgb, Imgproc.COLOR_RGBA2RGB, 1);
        Imgproc.cvtColor(mat2, mat2rgb, Imgproc.COLOR_RGBA2RGB, 1);
        Features2d.drawMatches(mat1rgb, keyPointsObject, mat2rgb, keyPointScene, gm, outputImage);

        Bitmap bitmap = Bitmap.createBitmap(outputImage.cols(), outputImage.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(outputImage, bitmap);

        Mat matH = Calib3d.findHomography(obj, scene);
        this.currCameraPoseFromCam = updateCameraPoseEstimation2(matH);

        //     Mat warping = mat1.clone();
        //     org.opencv.core.Size ims = new org.opencv.core.Size(mat1.cols(),mat1.rows());
        //     Imgproc.warpPerspective(mat1, warping , matH, ims);
        return bitmap;
    }

    public Mat updateCameraPoseEstimation2(Mat matH) {
        Mat K = new Mat(3, 3, CvType.CV_64FC1);

        K.put(0, 0, 3.97f);
        K.put(1, 0, 0.0f);
        K.put(2, 0, 0.0f);
        K.put(0, 1, 0.0f);
        K.put(1, 1, 3.97f);
        K.put(2, 1, 0.0f);
        K.put(0, 2, 252.0f);
        K.put(1, 2, 189.0f);
        K.put(2, 2, 1.0f);

        ArrayList<Mat> rotations = new ArrayList<Mat>();
        ArrayList<Mat> translations = new ArrayList<Mat>();
        ArrayList<Mat> normals = new ArrayList<Mat>();

        Calib3d.decomposeHomographyMat(matH, K, rotations, translations, normals);
        Mat pose = Mat.eye(3, 4, CvType.CV_32FC1);
        Mat rotation1 = new Mat();
        Mat translation1 = new Mat();
        boolean success = false;
        for (int i = 0; i < 4; i++) {
            rotation1 = rotations.get(i);
            translation1 = translations.get(i);
            pose.put(0, 0, rotation1.get(0, 0));
            pose.put(1, 0, rotation1.get(1, 0));
            pose.put(2, 0, rotation1.get(2, 0));
            pose.put(0, 1, rotation1.get(0, 1));
            pose.put(1, 1, rotation1.get(1, 1));
            pose.put(2, 1, rotation1.get(2, 1));
            pose.put(0, 2, rotation1.get(0, 2));
            pose.put(1, 2, rotation1.get(1, 2));
            pose.put(2, 2, rotation1.get(2, 2));
            float[] camAngles = this.simpleRenderer.getAnglesFromPoseM(pose);
            boolean thisOK = true;
            for (int j = 0; j < 3; j++) {
                if (Math.abs(camAngles[j]) > 50) {
                    thisOK = false;
                    break;
                }
            }
            if (thisOK) {
                success = true;
                break;
            }
        }

        //  Log.d("T", "number of solutions: " + String.valueOf(rotations.size()));
        //Log.d("T", "rotation / translation size: " + String.valueOf(rotation0.rows()) + "x" + String.valueOf(rotation0.cols()) + " " +
        //        String.valueOf(translation0.rows()) + "x" + String.valueOf(translation0.cols()));
        if (success) {
            pose.put(0, 3, translation1.get(0, 0)[0] / 100f);
            pose.put(1, 3, translation1.get(1, 0)[0] / 100f);
            pose.put(2, 3, translation1.get(2, 0)[0] / 100f);
            float[] camAngles = this.simpleRenderer.getAnglesFromPoseM(pose);
            camtrans = this.simpleRenderer.getTranslationFromPoseM(pose);
            this.camLocationAngle.setText(String.valueOf(camAngles[0]) + " " + String.valueOf(camAngles[1]) + " " + String.valueOf(camAngles[2])
                    + " " + String.valueOf(camtrans[0]) + " " + String.valueOf(camtrans[1]) + " " + String.valueOf(camtrans[2]));
            MainActivity.this.simpleRenderer.rotate3(camAngles[0]);
            MainActivity.this.simpleRenderer.rotate2(camAngles[2]);
            MainActivity.this.simpleRenderer.rotate1(camAngles[1]);

            // this.simpleRenderer.cameraM = poseToCameraM(pose, this.simpleRenderer.cameraM);
            // this.simpleRenderer.updateM(false);
        } else {
            this.camLocationAngle.setText("not success");
        }
        return pose;
    }

    // TODO: try a second way of estimating your matrix
    public Mat updateCameraPoseEstimation(Mat matH) {
        Mat pose = Mat.eye(3, 4, CvType.CV_32FC1);

        //  Log.d("T", "pose matrix: " + pose.dump());
        //  Log.d("T", "mat H: " + matH.dump());
        float norm1 = (float) norm(matH.col(0));
        float norm2 = (float) norm(matH.col(1));
        float tnorm = (norm1 + norm2) / 2.0f;
        //    Log.d("T", "norms:" + String.valueOf(norm1) + " " + String.valueOf(norm2));

        Mat p1 = matH.col(0);
        Mat p2 = pose.col(0);
        normalize(p1, p2);
        pose.put(0, 0, p2.get(0, 0));
        pose.put(1, 0, p2.get(1, 0));
        pose.put(2, 0, p2.get(2, 0));
        //     Log.d("T", "p1, p2, pose: " + p1.dump() + " " + p2.dump() + " " + pose.dump());

        p1 = matH.col(1);
        p2 = pose.col(2);
        normalize(p1, p2);
        pose.put(0, 1, p2.get(0, 0));
        pose.put(1, 1, p2.get(1, 0));
        pose.put(2, 1, p2.get(2, 0));
        //      Log.d("T", "p1, p2, pose again: " + p1.dump() + " " + p2.dump() + " " + pose.dump());

        p1 = pose.col(0);
        p2 = pose.col(1);
        //     Log.d("T", "p1, p2 again again: " + p1.dump() + " " + p2.dump());

        Mat p3 = p1.cross(p2);
        pose.put(0, 2, p3.get(0, 0));
        pose.put(1, 2, p3.get(1, 0));
        pose.put(2, 2, p3.get(2, 0));
        //    Log.d("T", "p3, pose: " + p3.dump() + " " + pose.dump());
        tnorm = (float) norm(matH.col(2));

        Core.divide(matH.col(2), new Scalar(tnorm), p3);

        //     Log.d("T", "p3, c2, matH: " + p3.dump() + " " + matH.dump());
        pose.put(0, 3, p3.get(0, 0));
        pose.put(1, 3, p3.get(1, 0));
        pose.put(2, 3, p3.get(2, 0));
        //     Log.d("T", "c2, p3, pose: " + p3.dump() + " " + pose.dump());
        //     Log.d("T", "null checking: " + String.valueOf(currCameraPoseFromBeacon==null) + " " + String.valueOf(pose == null));
        float[] camAngles = this.simpleRenderer.getAnglesFromPoseM(pose);
        float[] camTrans = this.simpleRenderer.getTranslationFromPoseM(pose);
        this.camLocationAngle.setText(String.valueOf(camAngles[0]) + " " + String.valueOf(camAngles[1]) + " " + String.valueOf(camAngles[2])
                + " " + String.valueOf(camTrans[0]) + " " + String.valueOf(camTrans[1]) + " " + String.valueOf(camTrans[2]));
        this.simpleRenderer.cameraM = poseToCameraM(pose, this.simpleRenderer.cameraM);
        this.simpleRenderer.updateM(false);
        return pose;
    }

    public float[] poseToCameraM(Mat pose, float[] oldcameraM) {
        float[] cameraM = new float[16];
        cameraM[0] = (float) pose.get(0, 0)[0];
        cameraM[1] = (float) pose.get(1, 0)[0];
        cameraM[2] = (float) pose.get(2, 0)[0];
        cameraM[3] = 0.0f;
        cameraM[4] = (float) pose.get(0, 1)[0];
        cameraM[5] = (float) pose.get(1, 1)[0];
        cameraM[6] = (float) pose.get(2, 1)[0];
        cameraM[7] = 0.0f;
        cameraM[8] = (float) pose.get(0, 2)[0];
        cameraM[9] = (float) pose.get(1, 2)[0];
        cameraM[10] = (float) pose.get(2, 2)[0];
        cameraM[11] = 0.0f;
        Log.d("T", "WWWThis cameraM 12 13 14: " + String.valueOf(oldcameraM[12]) + " " + String.valueOf(oldcameraM[13]) + " " + String.valueOf(oldcameraM[14]));
        Log.d("T", "WWWupdate 12 13 14: " + String.valueOf((float) pose.get(0, 3)[0] / 100.f) + " " + String.valueOf((float) pose.get(1, 3)[0] / 100.f) + " " + String.valueOf((float) pose.get(2, 3)[0] / 100.f));

        cameraM[12] = (float) pose.get(0, 3)[0] / 100.f + oldcameraM[12];
        cameraM[13] = (float) pose.get(1, 3)[0] / 100.f + oldcameraM[13];
        cameraM[14] = (float) pose.get(2, 3)[0] / 100.f + oldcameraM[14];
        cameraM[15] = 1.0f;
        return cameraM;
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;
        int width, height = 0;

        if (c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }
        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);
        return cs;
    }


    public String matchFingerprint(ArrayList<Integer> fingerprint) {
        int minSSD = 80000;
        int minSSDid = -1;
        for (int k = 0; k < fingerprintDatabase.length; k++) {
            int[] dbEntry = fingerprintDatabase[k];
            int currSSD = 0;
            for (int j = 0; j < 4; j++) {
                currSSD = currSSD + (int) Math.pow((double) (dbEntry[j * 2] - fingerprint.get(j * 2)), 2.0);
                if (currSSD < minSSD) {
                    minSSD = currSSD;
                    minSSDid = k;
                }
            }
        }
        return locations[minSSDid / 3];
    }

    public String matchFingerprint2(ArrayList<Integer> fingerprint) {
        double maxCorr = 0.0;
        int maxCorrId = -1;
        double[] fingerprintdouble = new double[8];
        for (int i = 0; i < 8; i++) {
            fingerprintdouble[i] = fingerprint.get(i);
        }
        double[] dbentrydouble = new double[8];

        PearsonsCorrelation pc = new PearsonsCorrelation();
        for (int k = 0; k < fingerprintDatabase.length; k++) {
            int[] dbEntry = fingerprintDatabase[k];
            for (int i = 0; i < 8; i++) {
                dbentrydouble[i] = (double) dbEntry[i];
            }
            double currCorr = pc.correlation(dbentrydouble, fingerprintdouble);
            if (currCorr > maxCorr) {
                maxCorr = currCorr;
                maxCorrId = k;
            }
        }
        return locations[maxCorrId / 3];
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private ArrayList<Integer> getCurrentFingerPrint() {
        ArrayList<Integer> currentFingerPrint = new ArrayList<>();
        if (beacon0.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon0.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        if (beacon1.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon1.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        if (beacon2.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon2.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        if (beacon3.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon3.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        if (beacon4.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon4.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        if (beacon5.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon5.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        if (beacon6.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon6.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        if (beacon7.getText().toString().equals("TextView")) {
            currentFingerPrint.add(-100);
        } else {
            Integer rssi = Integer.parseInt(beacon7.getText().toString().split("/")[0]);
            currentFingerPrint.add(rssi);
        }
        return currentFingerPrint;
    }

    public String generateEntry(ArrayList<Integer> fingerprint) {
        String location = this.spinner.getSelectedItem().toString();
        String entry = "L" + location;
        for (Integer item : fingerprint) {
            entry = entry + " " + String.valueOf(item);
        }
        entry = entry + "#";
        return entry;
    }

    public String generateIMUEntry(float[] angles) {
        String location = this.spinner.getSelectedItem().toString();
        String entry = "L" + location;
        for (int i = 0; i < 3; i++) {
            entry = entry + " " + String.valueOf(angles[i]);
        }
        entry = entry + "#";
        return entry;
    }

    public String generateCameraEntry(float[] angles, float[] translations) {
        String location = this.spinner.getSelectedItem().toString();
        String entry = "L" + location;
        for (int i = 0; i < 3; i++) {
            entry = entry + " " + String.valueOf(angles[i]);
        }
        entry = entry + "@";
        for (int i = 0; i < 3; i++) {
            entry = entry + " " + String.valueOf(translations[i]);
        }
        return entry;
    }

    public String readFingerPrintDatabase() {
        String myData = "";
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine;
            }
            in.close();
        } catch (IOException e) {
            Log.e("T", "reading fingerprint data error: " + e.getMessage());
        }
        return myData;
    }

    public void updateUsingKalmanFilter() {
        KalmanFilter kf = new KalmanFilter(18, 6, 0, CvType.CV_32F);
        //  kalman.set
        int nStates = 18;
        int nMeasurements = 6;
        int nInputs = 0;
        double dt = 0.125;

        //kf.set_processNoiseCov(); // TODO
        // kf.set_measurementNoiseCov(); // TODO
        // kf.set_errorCovPost(); // TODO

        Mat transitionMatrix = new Mat(15, nStates, CvType.CV_64FC1);
        transitionMatrix.put(0, 3, dt);
        transitionMatrix.put(1, 4, dt);
        transitionMatrix.put(2, 5, dt);
        transitionMatrix.put(3, 6, dt);
        transitionMatrix.put(4, 7, dt);
        transitionMatrix.put(5, 8, dt);
        transitionMatrix.put(0, 6, 0.5 * Math.pow(dt, 2.0));
        transitionMatrix.put(1, 7, 0.5 * Math.pow(dt, 2.0));
        transitionMatrix.put(2, 8, 0.5 * Math.pow(dt, 2.0));
        // orientation
        transitionMatrix.put(9, 12, dt);
        transitionMatrix.put(10, 13, dt);
        transitionMatrix.put(11, 14, dt);
        transitionMatrix.put(12, 15, dt);
        transitionMatrix.put(13, 16, dt);
        transitionMatrix.put(14, 17, dt);
        transitionMatrix.put(9, 15, 0.5 * Math.pow(dt, 2.0));
        transitionMatrix.put(10, 16, 0.5 * Math.pow(dt, 2.0));
        transitionMatrix.put(11, 17, 0.5 * Math.pow(dt, 2.0));

        Mat measurementMatrix = new Mat(nMeasurements, nStates, CvType.CV_64FC1);
//       /* MEASUREMENT MODEL */
//        //  [1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
//        //  [0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
//        //  [0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
//        //  [0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0]
//        //  [0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0]
//        //  [0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0]
        measurementMatrix.put(0, 0, 1); //x
        measurementMatrix.put(1, 1, 1); //y
        measurementMatrix.put(2, 2, 1); //z
        measurementMatrix.put(3, 9, 1); //roll
        measurementMatrix.put(4, 10, 1); //pitch
        measurementMatrix.put(5, 11, 1); //yaw

        kf.set_transitionMatrix(transitionMatrix);
        kf.set_measurementMatrix(measurementMatrix);

        // GOOD Measurement
        //if( inliers_idx.rows >= minInliersKalman ) {
        Mat translationMeasured = new Mat(3, 1, CvType.CV_64FC1);
        Mat rotationMeasured = new Mat(3, 3, CvType.CV_64FC1);
        // TODO fill measurement vector
        // fillMeasurements(measurements, translation_measured, rotation_measured);
        // }
        Mat measurements = new Mat(6, 1, CvType.CV_64FC1);
        updateKalmanFilter(kf, measurements, translationMeasured, rotationMeasured);

//// Instantiate estimated translation and rotation
//        cv::Mat translation_estimated(3, 1, CV_64F);
//        cv::Mat rotation_estimated(3, 3, CV_64F);
//// update the Kalman filter with good measurements
//        updateKalmanFilter( KF, measurements,
//                translation_estimated, rotation_estimated);

        //TODO: fill measurements
//        void fillMeasurements( cv::Mat &measurements,
//        const cv::Mat &translation_measured, const cv::Mat &rotation_measured)
//        {
//            // Convert rotation matrix to euler angles
//            cv::Mat measured_eulers(3, 1, CV_64F);
//            measured_eulers = rot2euler(rotation_measured);
//            // Set measurement to predict
//            measurements.at<double>(0) = translation_measured.at<double>(0); // x
//            measurements.at<double>(1) = translation_measured.at<double>(1); // y
//            measurements.at<double>(2) = translation_measured.at<double>(2); // z
//            measurements.at<double>(3) = measured_eulers.at<double>(0);      // roll
//            measurements.at<double>(4) = measured_eulers.at<double>(1);      // pitch
//            measurements.at<double>(5) = measured_eulers.at<double>(2);      // yaw
//        }
    }

    public void updateKalmanFilter(KalmanFilter kf, Mat measurement, Mat translationEstimated, Mat rotationEstimated) {
        Mat prediction = kf.predict();
        Mat estimated = kf.correct(measurement);
        translationEstimated.put(0, 0, estimated.get(0, 0));
        translationEstimated.put(1, 0, estimated.get(1, 0));
        translationEstimated.put(2, 0, estimated.get(2, 0));
        float[] rotationEstimatedfloat = this.simpleRenderer.anglesToM(new float[]{(float) estimated.get(3, 0)[0], (float) estimated.get(4, 0)[1], (float) estimated.get(5, 0)[2]});
        // TODO: float[] to Mat
    }

    public int getCurrentLen() {
        return currfreq;
    //    return this.motionLen;
    }

    public int getCurrspeed() {
        return currspeed;
    }

    public int getCurrentGes()  {
      //  return Integer.parseInt(this.spinner.getSelectedItem().toString());
        return 3;
    }
}
