package com.example.zarzadcaklubowy;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UstawieniaDodaj extends Activity implements OnClickListener {

	private Context context;
	private BazaDanych baza;
	private Cursor cursor;
	private boolean czyEdycja;
	private Button btUstawieniaDodajZapisz;
	private EditText etUstawieniaSezon, etUstawieniaLKolejek;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ustawienia_dodaj);
		btUstawieniaDodajZapisz = (Button)findViewById(R.id.btUstawieniaDodajZapisz);
		etUstawieniaSezon = (EditText)findViewById(R.id.etUstawieniaSezon);
		etUstawieniaLKolejek = (EditText)findViewById(R.id.etUstawieniaLKolejek);
		btUstawieniaDodajZapisz.setOnClickListener(this);
		
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {
		    czyEdycja = extras.getBoolean("czyEdycja");
		    if(czyEdycja) 
		    {
		    	cursor = baza.getSezonAktualny();
		    	if(cursor!=null && cursor.moveToFirst())
		    	{
		    		etUstawieniaSezon.setText(cursor.getString(cursor.getColumnIndex("sez_numer")));
		    		etUstawieniaLKolejek.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("sez_liczbakolejek"))));
		    	}
		    }
		    
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ustawienia_dodaj, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btUstawieniaDodajZapisz:
			if(czyEdycja)
			{
				
				baza.updateSezon(etUstawieniaSezon.getText().toString(), Integer.parseInt(etUstawieniaLKolejek.getText().toString()));
				Intent intent = new Intent(context, Ustawienia.class);
				startActivity(intent);
			}
			else
			{
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		        alertDialog.setTitle("Dane klubu");
		        alertDialog.setMessage("Czy przepisaæ dane klubu z aktualnego sezonu?");
		        alertDialog.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		            	Cursor c = baza.getDaneKlubu();
		            	if(c!=null && c.moveToFirst()){ 
			            	Cursor druzyna = baza.getNazwyDruzyn(c.getLong(c.getColumnIndex("dan_dru_id")));
			            	
			            	if(druzyna!=null && druzyna.moveToFirst())
			            	{
			            		long sezon = baza.insertSezon(etUstawieniaSezon.getText().toString(), Integer.parseInt(etUstawieniaLKolejek.getText().toString()), 0);
			            		baza.insertDaneKlubu(sezon,baza.insertDruzyna(sezon, druzyna.getString(druzyna.getColumnIndex("dru_nazwa"))),
			            			c.getInt(c.getColumnIndex("dan_rok")),
			            			c.getString(c.getColumnIndex("dan_adres")), c.getInt(c.getColumnIndex("dan_telefon")));
				            	Intent intent = new Intent(context, Ustawienia.class);
								startActivity(intent);
			            	}
			            	else
			            	{
			            		baza.insertSezon(etUstawieniaSezon.getText().toString(), Integer.parseInt(etUstawieniaLKolejek.getText().toString()), 0);
				            	Intent intent = new Intent(context, Ustawienia.class);
								startActivity(intent);
			            	}
			            	
		            	}
		            	else
		            	{
		            		baza.insertSezon(etUstawieniaSezon.getText().toString(), Integer.parseInt(etUstawieniaLKolejek.getText().toString()), 0);
			            	Intent intent = new Intent(context, Ustawienia.class);
							startActivity(intent);
		            	}
			            				
		            }
		        });

		        alertDialog.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
			            baza.insertSezon(etUstawieniaSezon.getText().toString(), Integer.parseInt(etUstawieniaLKolejek.getText().toString()), 0);
			            dialog.cancel();
			            Intent intent = new Intent(context, Ustawienia.class);
						startActivity(intent);
		            }
		        });
		        
		        alertDialog.show();
				
				
			}
			break;
		}
		
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }
	
	public void onResume()
    {  // After a pause OR at startup
		super.onResume();
   
     }

}
