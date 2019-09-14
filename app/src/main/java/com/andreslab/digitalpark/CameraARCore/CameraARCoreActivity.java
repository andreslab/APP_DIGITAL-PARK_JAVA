package com.andreslab.digitalpark.CameraARCore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreslab.digitalpark.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class CameraARCoreActivity extends AppCompatActivity {

    ArFragment arFragment;
    private ModelRenderable animalRenderable;
    ImageView imgAnimal;
    TextView titleAnimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_arcore);

        setupModel();

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);
        imgAnimal = (ImageView)findViewById(R.id.imgAnimal);
        titleAnimal = (TextView) findViewById(R.id.titleAnimal);

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                createModel(anchorNode);
            }
        });

    }

    private void createModel (AnchorNode anchorNode){
        TransformableNode animal = new TransformableNode(arFragment.getTransformationSystem());
        animal.setParent(anchorNode);
        animal.setRenderable(animalRenderable);
        animal.select();


    }

    private void setupModel(){
        ModelRenderable.builder()
                .setSource(this, R.raw.sphere3)
                .build().thenAccept(renderable -> animalRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "No se pudo cargar el modelo", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
    }
}
