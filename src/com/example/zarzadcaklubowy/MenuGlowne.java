package com.example.zarzadcaklubowy;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MenuGlowne extends Activity implements OnClickListener{
	
	private BazaDanych baza;
	private Button btDaneKlubu,btDruzyny,btZawodnicy, btUstawienia, btTerminarz, btTabela, btMecz, btDzialacze, btWykluczenia;
	private Context context;
	private Cursor cursor,cursorSezon;
	private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_glowne);
        btDaneKlubu = (Button)findViewById(R.id.btDaneKlubu);
        btDruzyny = (Button)findViewById(R.id.btDruzyny);
        btZawodnicy = (Button)findViewById(R.id.btZawodnicy);
        btUstawienia = (Button)findViewById(R.id.btUstawienia);
        btTerminarz = (Button)findViewById(R.id.btTerminarz);
        btTabela = (Button)findViewById(R.id.btTabela);
        btMecz = (Button)findViewById(R.id.btMecz);
        btDzialacze = (Button)findViewById(R.id.btDzialacze);
        btWykluczenia = (Button)findViewById(R.id.btWykluczenia);
        btDaneKlubu.setOnClickListener(this);
        btDruzyny.setOnClickListener(this);
        btZawodnicy.setOnClickListener(this);
        btUstawienia.setOnClickListener(this);
        btTerminarz.setOnClickListener(this);
        btTabela.setOnClickListener(this);
        btMecz.setOnClickListener(this);
        btDzialacze.setOnClickListener(this);
        btWykluczenia.setOnClickListener(this);
        context = getApplicationContext();
        baza = new BazaDanych(context);
        baza.open();
        cursor = baza.getDaneKlubu();
        //baza.deleteDaneKlubu();
        //baza.deleteDruzyny();
        

    }

	@Override
	public void onClick(View v) {
		
		switch (v.getId())
		{
		case R.id.btDaneKlubu:
			if(Powiadomienie())
			{
				if(cursor==null || !cursor.moveToFirst())
				{
					intent = new Intent(context, DaneKlubuWprowadz.class);
					startActivity(intent);
				}
				else
				{
					intent = new Intent(context, DaneKlubu.class);
					startActivity(intent);
				}
			}
				
			break;
		case R.id.btDruzyny:
			if(Powiadomienie())
			{
				intent = new Intent(context,Druzyny.class);
				startActivity(intent);
			}
			
			break;
		case R.id.btZawodnicy:
			if(Powiadomienie())
			{
				intent = new Intent(context,Zawodnicy.class);
				startActivity(intent);
			}
			break;
			
		case R.id.btDzialacze:
			if(Powiadomienie())
			{
				intent = new Intent(context,Dzialacze.class);
				startActivity(intent);
			}
			break;
		case R.id.btUstawienia:
			intent = new Intent(context,Ustawienia.class);
			startActivity(intent);
			break;
		case R.id.btTerminarz:
			if(Powiadomienie())
			{
				intent = new Intent(context,Terminarz.class);
				startActivity(intent);
			}
			break;
		case R.id.btTabela:
			if(Powiadomienie())
			{
				intent = new Intent(context,Tabela.class);
				startActivity(intent);
			}
			break;
		case R.id.btMecz:
			if(Powiadomienie())
			{
				intent = new Intent(context,Mecz.class);
				startActivity(intent);
				
			}
			break;
		case R.id.btWykluczenia:
			if(Powiadomienie())
			{
				intent = new Intent(context,Wykluczenia.class);
				startActivity(intent);
				
			}
			break;
			
			
		}
		
	}
	
	private boolean Powiadomienie()
	{
		cursorSezon = baza.getSezonAktualny();
		if(cursorSezon != null && cursorSezon.moveToFirst())
		{
			return true;
		}
		else
		{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	        alertDialog.setTitle("Powiadomienie");
	        alertDialog.setMessage("Wybierz aktualny sezon!");
	        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	            	
	            	dialog.cancel();
	            	
	            }
	        });
	        
	        alertDialog.show();
	        return false;
		}
	}
		
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }
	
	@Override
	public void onResume()
	    {  // After a pause OR at startup
		    super.onResume();

	    }


    
}
