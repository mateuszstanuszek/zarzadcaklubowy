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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class DaneKlubu extends Activity implements OnClickListener{

	private TextView tvNazwaKlubu,tvRok,tvAdres,tvTelefon;
	private Button btEdytuj,btWyczysc;
	private BazaDanych baza;
	private Cursor cursor, cursorDruzyna;

	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dane_klubu);
		
		tvNazwaKlubu = (TextView)findViewById(R.id.tvNazwaKlubu);
		tvAdres = (TextView)findViewById(R.id.tvAdres);
		tvRok = (TextView)findViewById(R.id.tvRok);
		tvTelefon = (TextView)findViewById(R.id.tvTelefon);
		btEdytuj = (Button) findViewById(R.id.btEdytuj);
		btWyczysc = (Button)findViewById(R.id.btWyczysc);
		btEdytuj.setOnClickListener(this);
		btWyczysc.setOnClickListener(this);
		context = getApplicationContext();
		baza = new BazaDanych(getApplicationContext());
		baza.open();
		cursor = baza.getDaneKlubu();
		if(cursor!=null && cursor.moveToFirst())
		{
			long id = cursor.getLong(cursor.getColumnIndex("dan_dru_id"));
			cursorDruzyna = baza.getNazwyDruzyn(id);
				if(cursorDruzyna!=null && cursorDruzyna.moveToFirst())
					tvNazwaKlubu.setText(cursorDruzyna.getString(cursorDruzyna.getColumnIndex("dru_nazwa")));
				
			tvAdres.setText(cursor.getString(cursor.getColumnIndex("dan_adres")));
			tvRok.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dan_rok"))));
			tvTelefon.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dan_telefon"))));
		}
			
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dane_klubu, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btEdytuj:
			Intent intent = new Intent(context, DaneKlubuEdycja.class);
			startActivity(intent);
			break;
		case R.id.btWyczysc:
			
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	        alertDialog.setTitle("Usuwanie klubu");
	        alertDialog.setMessage("Czy jesteœ pewien? Dru¿yna oraz zawodnicy nadal bêd¹ aktywni w aktualnym sezonie. Aby usun¹æ wejdŸ w odpowiedni dzia³.");
	        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	 
	            	Cursor c = baza.getDaneKlubu();
	            	if (c!=null && c.moveToFirst())
	            	{
		            	long id = c.getLong(c.getColumnIndex("_id"));
		            	baza.deleteDaneKlubu(id);
	            	}
	    			Intent intent = new Intent(context, MenuGlowne.class);
	    			startActivity(intent);  
	            }
	        });

	        alertDialog.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            }
	        });
	        
	        alertDialog.show();
			
			break;
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
	    if(cursor==null || !cursor.moveToFirst())
	    {
	    	Intent intent = new Intent(context, DaneKlubuWprowadz.class);
			startActivity(intent);
	    }
	     }


}
