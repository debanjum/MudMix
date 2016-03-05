package edu.dartmouth.cs.armudwear.gesture;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

import edu.dartmouth.cs.armudwear.Globals;
import meapsoft.FFT;

public class SensorsService extends Service implements SensorEventListener {

    private static final int mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 2;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int mServiceTaskContext;
    private OnSensorChangedTask mAsyncTask;
    private AttackClassifier mCharContextClassifier;
    private ObjClassifier mObjContextClassifier;
    private InvClassifier mInvContextClassifier;
    private int mThreadFlag = 0;

    private static ArrayBlockingQueue<Double> mAccBuffer;

    @Override
    public void onCreate() {
        super.onCreate();

        mAccBuffer = new ArrayBlockingQueue<Double>(
                Globals.ACCELEROMETER_BUFFER_CAPACITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SensorService", "Started");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);

        mServiceTaskContext = Globals.FOCUS_CONTEXT_CHARACTER;
        mCharContextClassifier = new AttackClassifier();
        mObjContextClassifier = new ObjClassifier();
        mInvContextClassifier = new InvClassifier();
        LocalBroadcastManager.getInstance(this).registerReceiver(mSwitchClassifierReceiver,
                new IntentFilter(Globals.CONTEXT_KEY));


        mAsyncTask = new OnSensorChangedTask();
        mAsyncTask.execute();
        return START_NOT_STICKY;
    }

    private BroadcastReceiver mSwitchClassifierReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("SensorService","Switching Classifier");
            mServiceTaskContext = intent.getIntExtra(Globals.CONTEXT_KEY, Globals.FOCUS_CONTEXT_IDLE);
        }
    };

    @Override
    public void onDestroy() {

        mAsyncTask.cancel(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
        Log.i("", "");
        super.onDestroy();
    }

    private class OnSensorChangedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            if (mThreadFlag == 0) {
                Log.i("Threads", "Started");
                mThreadFlag++;
            }
            int blockSize = 0;
            FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
            double[] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
            double[] re = accBlock;
            double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
            Object[] featVec = new Object[Globals.ACCELEROMETER_BLOCK_CAPACITY + 1];

            double max;

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled() == true) {
                        return null;
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0;

                        // time = System.currentTimeMillis();
                        max = .0;
                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                        }

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i]
                                    * im[i]);
                                featVec[i] = mag;
                            im[i] = .0; // Clear the field
                        }
                        // Append max after frequency component
                        featVec[Globals.ACCELEROMETER_BLOCK_CAPACITY] =  max;

                        int command_index = Globals.NO_COMMAND_DETECTED;
                        switch (mServiceTaskContext) {
                            case Globals.FOCUS_CONTEXT_CHARACTER:
                                command_index = (int) mCharContextClassifier.classify(featVec);
                                break;
                            case Globals.FOCUS_CONTEXT_OBJECT:
                                command_index = (int) mObjContextClassifier.classify(featVec);
                                break;
                            case Globals.FOCUS_CONTEXT_INVENTORY:
                                command_index = (int) mInvContextClassifier.classify(featVec);
                                break;
                            default:
                                Log.d("SensorService","STOPPING SELF");
                                stopSelf();
                        }

                        if (Globals.NO_COMMAND_DETECTED != command_index) {
                            Log.d("SensorsService", "Command detected");
                            sendMessage(command_index);
                        } else {
                            Log.d("SensorsService", "No command detected");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            return;
        }

    }

    public void sendMessage(int label_index) {
        Log.d("CommandUpdate", "Sending");
        Intent intent = new Intent(Globals.COMMAND_UPDATED);
        intent.putExtra("commandNumber", label_index);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2]
                    * event.values[2]);

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.

            try {
                mAccBuffer.add(new Double(m));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(m));
            }
        }
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
