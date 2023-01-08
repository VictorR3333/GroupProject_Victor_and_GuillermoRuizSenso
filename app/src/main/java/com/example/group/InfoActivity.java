package com.example.group;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class InfoActivity extends AppCompatActivity {

    Button btn_add,btn_score;
    EditText txt_name, txt_course, txt_year;
    FirebaseFirestore firebd;

    private SensorManager sm;
    private Sensor s;
    private SensorEventListener evento;
    private int mov=0, count=0;

    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        txt_name = findViewById(R.id.txtName);
        txt_course = findViewById(R.id.txtCourse);
        txt_year = findViewById(R.id.txtYear);
        btn_add = findViewById(R.id.btnAdd);
        btn_score = findViewById(R.id.btnScore);

        firebd = FirebaseFirestore.getInstance();
        studentId = getIntent().getStringExtra("ID");

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txt_name.getText().toString().trim();
                String course = txt_course.getText().toString().trim();
                String year = txt_year.getText().toString().trim();

                if(name.isEmpty() || course.isEmpty() || year.isEmpty()){
                    Toast.makeText(InfoActivity.this, "Introduce all the information", Toast.LENGTH_SHORT).show();
                }else {
                    postInfo(name,course,year,count);
                }
            }
        });
        btn_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//Accede a los sensores
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//represento al accelerometro

        if(s==null){
            finish();
        }

        evento = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //Codigo que se genera por el evento del acelerometro
                if(event.values[0]<-4 && mov==0){
                    mov++;
                }else{
                    if(event.values[0]>4 && mov==1){
                        mov++;
                        count++;
                    }
                }
                if(mov==2){
                    mov=0;
                    System.out.println("Emitir sonido");
                    System.out.println("Recuento: "+count);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sm.registerListener(evento,s,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void postInfo(String name, String course, String year,int count) {

        if(count >= 100){
            Map<String,Object> user = new HashMap<>();
            user.put("name",name);
            user.put("course",course);
            user.put("year",year);
            user.put("accelerometer_data",count);

            firebd.collection("users").document(studentId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InfoActivity.this, "Great", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        }else{
            Map<String,Object> user = new HashMap<>();
            user.put("name",name);
            user.put("course",course);
            user.put("year",year);

            firebd.collection("users").document(studentId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InfoActivity.this, "Great", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        }

    }

}