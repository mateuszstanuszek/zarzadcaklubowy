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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Dzialacze extends Activity implements OnClickListener{
	
	private Context context;
	private BazaDanych baza;
	private Intent intent;
	private TextView tvDzialaczImie, tvDzialaczNazwisko, tvDzialaczPesel, tvDzialaczData, tvDzialaczAdres, tvDzialaczTelefon;
	private TextView tvDzialaczEmail, tvDzialaczStanowisko;
	private Cursor cursor;
	
	private Button btDzialaczPoprzedni,btDzialaczDodaj, btDzialaczEdytuj, btDzialaczUsun, btDzialaczNastepny;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dzialacze);
		PrzypiszId();
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
	
		PobierzDzialaczy();
		
		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dzialacze, menu);
		return true;
	}
	
	
	@Override
	public void onClick(View v) {

			switch (v.getId())
			{
			case R.id.btDzialaczePoprzedni:
				if(cursor!=null)
				{
					if(cursor.moveToPrevious())
					{
						UstawPolaTekstowe();
					}
					else cursor.moveToPosition(cursor.getPosition()+1);
							
				}
				
				break;
			case R.id.btDzialaczeDodaj:
				
					intent = new Intent(context, DzialaczeDodaj.class);
					intent.putExtra("czyEdycja", false);
					startActivity(intent);
				
						
				break;
			case R.id.btDzialaczeEdytuj:
				if (cursor!=null && cursor.moveToPosition(cursor.getPosition()))
				{
					intent = new Intent(context, DzialaczeDodaj.class);
					intent.putExtra("czyEdycja", true);
					intent.putExtra("id", cursor.getInt(cursor.getColumnIndex("_id")));
					startActivity(intent);
				}
				
				break;
			case R.id.btDzialaczeUsun:
				if(cursor!=null && cursor.moveToPosition(cursor.getPosition()))
            	{
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			        alertDialog.setTitle("Usuwanie dzia³acza");
			        alertDialog.setMessage("Czy jesteœ pewien?");
			        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
			            	
			            		int idDzialacz = cursor.getInt(cursor.getColumnIndex("_id"));
			            		baza.deleteDzialacz(idDzialacz); 
			            		PobierzDzialaczy();
			            	
			            }
			        });
	
			        alertDialog.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            dialog.cancel();
			            }
			        });
			        
			        alertDialog.show();
            	}
				
				break;
			case R.id.btDzialaczeNastepny:
				
				if(cursor!=null)
				{
					if (cursor.moveToNext())
					{
						UstawPolaTekstowe();
					}
					else cursor.moveToPosition(cursor.getPosition()-1);
				}
				break;
			}
			

		
	}
	
	@Override
	public void onResume()
	    {  // After a pause OR at startup
	    super.onResume();
	    PobierzDzialaczy();
	     }
	
	
	
	
	
	
	
	private void PrzypiszId()
	{
		tvDzialaczImie = (TextView)findViewById(R.id.tvDzialaczeImie);
		tvDzialaczNazwisko = (TextView)findViewById(R.id.tvDzialaczeNazwisko);
		tvDzialaczPesel = (TextView)findViewById(R.id.tvDzialaczePESEL);
		tvDzialaczAdres = (TextView)findViewById(R.id.tvDzialaczeAdres);
		tvDzialaczTelefon = (TextView)findViewById(R.id.tvDzialaczeTelefon);
		tvDzialaczEmail = (TextView)findViewById(R.id.tvDzialaczeEmail);
		tvDzialaczStanowisko = (TextView)findViewById(R.id.tvDzialaczeStanowisko);
		
		btDzialaczDodaj = (Button)findViewById(R.id.btDzialaczeDodaj);
		btDzialaczPoprzedni = (Button)findViewById(R.id.btDzialaczePoprzedni);
		btDzialaczEdytuj = (Button)findViewById(R.id.btDzialaczeEdytuj);
		btDzialaczUsun = (Button)findViewById(R.id.btDzialaczeUsun);
		btDzialaczNastepny = (Button)findViewById(R.id.btDzialaczeNastepny);
		
		btDzialaczDodaj.setOnClickListener(this);
		btDzialaczPoprzedni.setOnClickListener(this);
		btDzialaczEdytuj.setOnClickListener(this);
		btDzialaczUsun.setOnClickListener(this);
		btDzialaczNastepny.setOnClickListener(this);
	}
	
	private void UstawPolaTekstowe()
	{
		tvDzialaczImie.setText(cursor.getString(cursor.getColumnIndex("dzi_imie")));
		tvDzialaczNazwisko.setText(cursor.getString(cursor.getColumnIndex("dzi_nazwisko")));
		tvDzialaczPesel.setText(cursor.getString(cursor.getColumnIndex("dzi_PESEL")));
		tvDzialaczAdres.setText(cursor.getString(cursor.getColumnIndex("dzi_adres")));
		tvDzialaczTelefon.setText(cursor.getString(cursor.getColumnIndex("dzi_nrtel")));
		tvDzialaczEmail.setText(cursor.getString(cursor.getColumnIndex("dzi_email")));
		tvDzialaczStanowisko.setText(cursor.getString(cursor.getColumnIndex("dzi_stanowisko")));
	}
	
	private void UstawPolaTekstowePuste()
	{
		tvDzialaczImie.setText("---");
		tvDzialaczNazwisko.setText("---");
		tvDzialaczPesel.setText("---");

		tvDzialaczAdres.setText("---");
		tvDzialaczTelefon.setText("---");
		tvDzialaczEmail.setText("---");
		tvDzialaczStanowisko.setText("---");
	}
	
	
	private void PobierzDzialaczy()
	{
		cursor = baza.getDzialacze();
		
		if(cursor!=null && cursor.moveToFirst())
		{
			UstawPolaTekstowe();

		}
		else
		{
			
			UstawPolaTekstowePuste();
		}
	}
	
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }

}
