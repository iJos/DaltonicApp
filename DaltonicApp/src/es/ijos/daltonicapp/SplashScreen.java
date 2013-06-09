package es.ijos.daltonicapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class SplashScreen extends Activity {
	
	private CountDownTimer temporizador;
	ProgressBar barraProgreso;
	int progreso = 0;
	int tiempoCarga = 5000;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		//Brillo máximo permanente
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	
		barraProgreso = (ProgressBar)findViewById(R.id.barra_progreso);
		barraProgreso.setProgress(progreso);
		
		barraProgreso.setMax(tiempoCarga / 1000);
		
		    temporizador = new CountDownTimer(tiempoCarga, 1000) {
		        public void onFinish() {
		            closeScreen();
		        }
		        @Override
		        public void onTick(long millisUntilFinished) {
		            // TODO Auto-generated method stub
		        	progreso++;
		        	barraProgreso.setProgress(progreso);
		        }
		    }.start();
		}

	private void closeScreen() {
	    Intent intent = new Intent();
	    intent.setClass(this, Menu.class);
	    startActivity(intent);
	    finish();
	}
	
}
