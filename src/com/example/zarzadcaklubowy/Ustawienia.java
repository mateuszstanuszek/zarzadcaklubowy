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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class Ustawienia extends Activity implements OnClickListener{
	
	private TextView tvUstawieniaSezon, tvUstawieniaLKolejek;
	private Button btUstawieniaDodaj, btUstawieniaEdytuj, btUstawieniaUsun, btUstawieniaZapisz;
	private Spinner spinnerSezon;
	private Context context;
	private BazaDanych baza;
	private Cursor cursorAktualny, cursorSezony;
	private Intent intent;
	private SimpleCursorAdapter spinnerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ustawienia);
		PrzypiszId();
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		//baza.insertSezon("2012", 20,0);
		//baza.insertSezon("2011", 30,1);
		//baza.insertSezon("2013", 40,0);
		//baza.deleteSezony();
		
		PobierzDane();
		
		
	}
	
	private void PobierzDane()
	{
		cursorAktualny = baza.getSezonAktualny();
		if(cursorAktualny!=null && cursorAktualny.moveToFirst())
		{
			tvUstawieniaSezon.setText(cursorAktualny.getString(cursorAktualny.getColumnIndex("sez_numer")));
			tvUstawieniaLKolejek.setText(String.valueOf(cursorAktualny.getInt(cursorAktualny.getColumnIndex("sez_liczbakolejek"))));
		}
		else
		{
			tvUstawieniaSezon.setText("---");
			tvUstawieniaLKolejek.setText("---");
		}
		cursorSezony = baza.getSezony();
		
			spinnerAdapter = new SimpleCursorAdapter(this,R.layout.spinner_layout,cursorSezony,new String[]{"sez_numer"},new int[]{R.id.tvSpinnerDruzyny});
			spinnerSezon.setAdapter(spinnerAdapter);


	}
	
	private void PrzypiszId()
	{
		tvUstawieniaSezon = (TextView)findViewById(R.id.tvUstawieniaSezon);
		tvUstawieniaLKolejek  = (TextView)findViewById(R.id.tvUstawieniaLiczbaKolejek);
		btUstawieniaDodaj = (Button)findViewById(R.id.btUstawieniaDodaj);
		btUstawieniaEdytuj = (Button)findViewById(R.id.btUstawieniaEdytuj);
		btUstawieniaUsun = (Button)findViewById(R.id.btUstawieniaUsun);
		btUstawieniaZapisz = (Button)findViewById(R.id.btUstawieniaZapisz);
		spinnerSezon = (Spinner)findViewById(R.id.spinnerUstawieniaSezon);
		
		btUstawieniaDodaj.setOnClickListener(this);
		btUstawieniaEdytuj.setOnClickListener(this);
		btUstawieniaUsun.setOnClickListener(this);
		btUstawieniaZapisz.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ustawienia, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btUstawieniaDodaj:
			intent = new Intent(context, UstawieniaDodaj.class);
			intent.putExtra("czyEdycja", false);
			startActivity(intent);
			
			break;
		case R.id.btUstawieniaEdytuj:
			intent = new Intent(context, UstawieniaDodaj.class);
			intent.putExtra("czyEdycja", true);
			startActivity(intent);
			break;
		case R.id.btUstawieniaUsun:
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	        alertDialog.setTitle("Usuwanie aktualnego sezonu");
	        alertDialog.setMessage("Ta operacja spowoduje usuniêcie wszystkich meczów oraz dru¿yn z aktualnego sezonu. Czy jesteœ pewien?");
	        alertDialog.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	 
	            	if(cursorAktualny!=null && cursorAktualny.moveToFirst())
	            	{
	            		int id = cursorAktualny.getInt(cursorAktualny.getColumnIndex("_id"));
	            		baza.deleteDruzynyAktualnegoSezonu(id);
	            		baza.deleteMeczeAktualnegoSezonu(id);
	            		baza.deleteDaneAktualnegoSezonu(id);
	            		baza.deleteZawodnikSezon(id);
	            		Cursor mecze = baza.getMecze();
	            		if(mecze!=null && mecze.moveToFirst())
	            		{
	            			do
	            			{
	            				baza.deleteStatystykaMecz(mecze.getLong(mecze.getColumnIndex("_id")));
	            			}
	            			while (!mecze.isLast() && mecze.moveToNext());
	            		}
	            	}

	            	baza.deleteSezonAktualny();
	            	
	    			PobierzDane();
	            }
	        });

	        alertDialog.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            }
	        });
	        
	        alertDialog.show();
			
			break;
		case R.id.btUstawieniaZapisz:
			
			baza.updateSezonyNieaktualne();
			cursorAktualny = (Cursor) spinnerSezon.getSelectedItem();	
			if(cursorAktualny!=null && cursorAktualny.moveToPosition(cursorAktualny.getPosition()))
			{
				int id = cursorAktualny.getInt(cursorAktualny.getColumnIndex("_id"));
				baza.updateSezonAktualny(id);
				
			}
			PobierzDane();
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
	    PobierzDane();
	     }

}
