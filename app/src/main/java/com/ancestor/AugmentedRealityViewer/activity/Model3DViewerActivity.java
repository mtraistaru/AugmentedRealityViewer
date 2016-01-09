package com.ancestor.AugmentedRealityViewer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.ancestor.AugmentedRealityViewer.R;
import com.ancestor.AugmentedRealityViewer.domain.CustomARObject;
import com.ancestor.AugmentedRealityViewer.domain.CustomLightRenderer;
import com.ancestor.AugmentedRealityViewer.domain.Model;
import com.ancestor.AugmentedRealityViewer.util.AssetsUtils;
import com.ancestor.AugmentedRealityViewer.util.FileUtils;
import com.ancestor.AugmentedRealityViewer.util.ModelObjectParser;
import com.ancestor.AugmentedRealityViewer.util.ParseException;
import com.ancestor.AugmentedRealityViewer.util.SDCardFileUtils;
import com.ancestor.AugmentedRealityViewer.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;

public class Model3DViewerActivity extends AndARActivity implements SurfaceHolder.Callback {

    public static final int TYPE_INTERNAL = 0;
    public static final int TYPE_EXTERNAL = 1;

    public static final boolean DEBUG = false;

    private final int SCALE = 0;
    private final int ROTATE = 1;
    private final int TRANSLATE = 2;
    private final int TAKE_SCREENSHOT = 3;
    ARToolkit augmentedRealityToolkit;
    private int mode = SCALE;
    private Model model;
    private CustomARObject customARObject;
    private ProgressDialog progressDialog;
    private Resources res;

    public Model3DViewerActivity() {
        super(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setNonARRenderer(new CustomLightRenderer());
        res = getResources();
        augmentedRealityToolkit = getArtoolkit();
        getSurfaceView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openContextMenu(getSurfaceView());
                return false;
            }
        });
        getSurfaceView().setOnTouchListener(new OnTouchListener() {

            private float previousX = 0;
            private float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (model != null) {
                    switch (event.getAction()) {
                        default:
                        case MotionEvent.ACTION_DOWN:
                            previousX = event.getX();
                            previousY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dX = previousX - event.getX();
                            float dY = previousY - event.getY();
                            previousX = event.getX();
                            previousY = event.getY();
                            switch (mode) {
                                case SCALE:
                                    model.setScaleRatio(dY / 100.0f);
                                    break;
                                case ROTATE:
                                    model.setRotationX(-1 * dX);
                                    model.setRotationY(-1 * dY);
                                    break;
                                case TRANSLATE:
                                    model.setTranslationX(dY / 10f);
                                    model.setTranslationY(dX / 10f);
                                    break;
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            previousX = event.getX();
                            previousY = event.getY();
                            break;
                    }
                }
                return true;
            }
        });
        getSurfaceView().getHolder().addCallback(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        System.out.println("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, TRANSLATE, 0, res.getText(R.string.Translate))
                .setIcon(R.drawable.translate_model);
        menu.add(0, ROTATE, 1, res.getText(R.string.Rotate))
                .setIcon(R.drawable.rotate_model);
        menu.add(0, SCALE, 2, res.getText(R.string.Scale))
                .setIcon(R.drawable.scale_model);
        menu.add(0, TAKE_SCREENSHOT, 3, res.getText(R.string.Screenshot_Take))
                .setIcon(R.drawable.take_screenshot);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SCALE:
                mode = SCALE;
                return true;
            case ROTATE:
                mode = ROTATE;
                return true;
            case TRANSLATE:
                mode = TRANSLATE;
                return true;
            case TAKE_SCREENSHOT:
                new TakeScreenshotAsyncTask().execute();
                return true;
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        if (model == null) {
            progressDialog = ProgressDialog.show(this, "", getResources().getText(R.string.Progress_Dialog_Next), true);
            progressDialog.show();
            new LoadModelAsyncTask().execute();
        }
    }

    private class LoadModelAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            int type = extras.getInt("type");
            String modelFileName = extras.getString("name");
            FileUtils fileUtils = null;
            File file = null;
            switch (type) {
                case TYPE_EXTERNAL:
                    fileUtils = new SDCardFileUtils();
                    file = new File(URI.create(modelFileName));
                    modelFileName = file.getName();
                    fileUtils.setFolder(file.getParentFile().getAbsolutePath());
                    break;
                case TYPE_INTERNAL:
                    fileUtils = new AssetsUtils(getResources().getAssets());
                    fileUtils.setFolder("models/");
                    break;
            }

            if (modelFileName.endsWith(".obj")) {
                ModelObjectParser parser = new ModelObjectParser(fileUtils);
                try {
                    if (type == TYPE_EXTERNAL) {
                        BufferedReader modelFileReader = new BufferedReader(new FileReader(file));
                        String shebang = modelFileReader.readLine();
                        if (!shebang.equals("#trimmed")) {
                            File trimmedFile = new File(file.getAbsolutePath() + ".tmp");
                            BufferedWriter trimmedFileWriter = new BufferedWriter(new FileWriter(trimmedFile));
                            StringUtils.trim(modelFileReader, trimmedFileWriter);
                            if (file.delete()) {
                                trimmedFile.renameTo(file);
                            }
                        }
                    }
                    if (fileUtils != null) {
                        BufferedReader fileReader = fileUtils.getReaderFromName(modelFileName);
                        if (fileReader != null) {
                            model = parser.parseModel("Model", fileReader);
                            customARObject = new CustomARObject(model);
                        }
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            try {
                if (customARObject != null)
                    augmentedRealityToolkit.registerARObject(customARObject);
            } catch (AndARException e) {
                e.printStackTrace();
            }
            startPreview();
        }
    }

    private class TakeScreenshotAsyncTask extends AsyncTask<Void, Void, Void> {

        private String errorMsg = null;

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap bm = takeScreenshot();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream("/sdcard/AugmentedRealityViewer" + new Date().getTime() + ".png");
                bm.compress(CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                errorMsg = e.getMessage();
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (errorMsg == null) {
                Toast.makeText(Model3DViewerActivity.this, getResources().getText(R.string.Screenshot_Saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Model3DViewerActivity.this, getResources().getText(R.string.Screenshot_Failed) + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
