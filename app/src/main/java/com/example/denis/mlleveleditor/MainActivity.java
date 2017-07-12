package com.example.denis.mlleveleditor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toEditor(View v){
        EditText x = (EditText)findViewById(R.id.x);
        EditText y = (EditText)findViewById(R.id.y);
        Log.d("AAAAAAAAAAAAAAA",x.getText().toString());
        int lWidth = Integer.decode(x.getText().toString()) ;
        int lHeight = Integer.decode(y.getText().toString());
        EditorView ev = new EditorView(this,lWidth,lHeight);


        setContentView(ev);
    }
}
