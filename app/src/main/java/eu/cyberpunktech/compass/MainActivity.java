package eu.cyberpunktech.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorMagneticField, sensorGravity;
    private double xA, yA, zA, xM, yM, zM;
    private Button btnHeading;
    private TextView tvHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHeading = findViewById(R.id.btnHeading);
        tvHeading = findViewById(R.id.tvHeading);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if(sensorGravity == null)
        {
            MainActivity activity = this;
            Toast.makeText(activity, "Gravity sensor is not available.", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensorMagneticField == null)
        {
            MainActivity activity = this;
            Toast.makeText(activity, "Magnetic sensor is not available.", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            xA = event.values[0];
            yA = event.values[1];
            zA = event.values[2];
        }

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            xM = event.values[0];
            yM = event.values[1];
            zM = event.values[2];
        }

        double pitch = Math.atan2(-xA,Math.sqrt(yA*yA+zA*zA));
        double roll = Math.atan2(yA,zA);

        double cos_roll = Math.cos(roll);
        double sin_roll = Math.sin(roll);

        double cos_pitch = Math.cos(pitch);
        double sin_pitch = Math.sin(pitch);

        double yHeading = (yM*cos_roll) - ((float)zM*sin_roll);
        double xHeading = (xM*cos_pitch) + ((float)yM*sin_roll*sin_pitch) + ((float)zM*cos_roll*sin_pitch);
        int heading = (int) (Math.atan2(yHeading,xHeading)*57.29577951) - 90;

        while(heading>=360) heading -= 360;
        while(heading<0) heading += 360;
        heading = 360-heading;
        if(heading==360) heading = 0;

        btnHeading.setRotation(heading);
        tvHeading.setText("Heading: " + heading);
    }
}
