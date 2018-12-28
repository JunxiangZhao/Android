package com.example.storage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    private String FILE_NAME = "myFile";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Button saveButton = (Button)findViewById(R.id.saveBtn);
        Button loadButton = (Button)findViewById(R.id.loadBtn);
        Button clearButton = (Button)findViewById(R.id.clearBtn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);

                try (FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
                    String str = editText.getText().toString();
                    fileOutputStream.write(str.getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Log.i("TAG", "Successfully saved file.");
                    Toast.makeText(getApplicationContext(), "Save successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Log.e("TAG", "Fail to save file.");
                }
            }
        });


        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.editText);

                try (FileInputStream fileInputStream = openFileInput(FILE_NAME)) {
                    byte[] contents = new byte[fileInputStream.available()];
                    StringBuilder stringBuilder = new StringBuilder("");
                    int len = 0;
                    while((len = fileInputStream.read(contents)) > 0){
                        stringBuilder.append(new String(contents,0,len));
                    }
                    fileInputStream.close();
                    editText.setText(stringBuilder.toString());
                    Toast.makeText(getApplicationContext(), "Load successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Log.e("TAG", "Fail to read file.");
                    Toast.makeText(getApplicationContext(), "Fail to load file.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText)findViewById(R.id.editText);
                text.setText("");
            }
        });
    }
}
