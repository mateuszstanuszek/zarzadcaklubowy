package com.example.zarzadcaklubowy;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ZawodnicyDodaj extends Activity implements OnClickListener {
	
	private EditText etZawodnikImie, etZawodnikNazwisko, etZawodnikPesel, etZawodnikData, etZawodnikAdres;
	private EditText etZawodnikTelefon, etZawodnikEmail;
	private Spinner spinnerPozycje;
	private Button btZawodnikZapisz;
	private Context context;
	private BazaDanych baza;
	private Cursor cursor,cursorAktualnySezon;
	private boolean czyEdycja;
	private int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zawodnicy_dodaj);
		PrzypiszId();
		
		
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {
		    czyEdycja = extras.getBoolean("czyEdycja");
		    if(czyEdycja) 
		    {
		    	id = extras.getInt("id");
		    	cursor = baza.getZawodnicy(id);
		    	if(cursor!=null && cursor.moveToFirst())
		    	{
		    		etZawodnikImie.setText(cursor.getString(cursor.getColumnIndex("zaw_imie")));
		    		etZawodnikNazwisko.setText(cursor.getString(cursor.getColumnIndex("zaw_nazwisko")));
		    		etZawodnikPesel.setText(cursor.getString(cursor.getColumnIndex("zaw_PESEL")));
		    		etZawodnikData.setText(cursor.getString(cursor.getColumnIndex("zaw_data")));
		    		etZawodnikAdres.setText(cursor.getString(cursor.getColumnIndex("zaw_adres")));
		    		etZawodnikTelefon.setText(cursor.getString(cursor.getColumnIndex("zaw_nrtel")));
		    		etZawodnikEmail.setText(cursor.getString(cursor.getColumnIndex("zaw_email")));
		    	}
		    }
		    
		}
		List<String> pozycje= new ArrayList<String>();
		pozycje.add("bramkarz");
		pozycje.add("obro�ca");
		pozycje.add("pomocnik");
		pozycje.add("napastnik");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_layout,R.id.tvSpinnerDruzyny, pozycje);
		spinnerPozycje.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.zawodnicy_dodaj, menu);
		return true;
	}
	
	private void PrzypiszId()
	{
		etZawodnikImie = (EditText)findViewById(R.id.etZawodnikImie);
		etZawodnikNazwisko = (EditText)findViewById(R.id.etZawodnikNazwisko);
		etZawodnikPesel = (EditText)findViewById(R.id.etZawodnikPesel);
		etZawodnikData = (EditText)findViewById(R.id.etZawodnikData);
		etZawodnikAdres = (EditText)findViewById(R.id.etZawodnikAdres);
		etZawodnikTelefon = (EditText)findViewById(R.id.etZawodnikTelefon);
		etZawodnikEmail  = (EditText)findViewById(R.id.etZawodnikEmail);
		spinnerPozycje  = (Spinner)findViewById(R.id.spinnerPozycje);
		btZawodnikZapisz = (Button)findViewById(R.id.btZawodnicyZapisz);
		btZawodnikZapisz.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btZawodnicyZapisz:
			if(czyEdycja)
			{
				baza.updateZawodnik(id,1, etZawodnikImie.getText().toString(), etZawodnikNazwisko.getText().toString(), etZawodnikPesel.getText().toString(),
						etZawodnikAdres.getText().toString(), etZawodnikData.getText().toString(), etZawodnikTelefon.getText().toString(),
						etZawodnikEmail.getText().toString(), (String)spinnerPozycje.getSelectedItem());
				Intent intent = new Intent(context, Zawodnicy.class);
				startActivity(intent);
			}
			else
			{
				cursorAktualnySezon = baza.getSezonAktualny();
				if(cursorAktualnySezon != null && cursorAktualnySezon.moveToFirst())
				{
					int idSezon = cursorAktualnySezon.getInt(cursorAktualnySezon.getColumnIndex("_id"));
					baza.insertZawodnik(idSezon, 1, etZawodnikImie.getText().toString(), etZawodnikNazwisko.getText().toString(), etZawodnikPesel.getText().toString(),
							etZawodnikAdres.getText().toString(), etZawodnikData.getText().toString(), etZawodnikTelefon.getText().toString(),
							etZawodnikEmail.getText().toString(), (String)spinnerPozycje.getSelectedItem());
					/*for(int i=0; i<25;i++)
					{
						baza.insertZawodnik(idSezon,1, "Mateusz"+i, "Stanuszek"+i, "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");

					}*/
					/*
					baza.insertZawodnik(idSezon,1, "Mateusz", "Stanuszek", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "�ukasz", "Hajduga", "91081104136" ,"Por�ba 112", "21.08.1981", "5566233221", "hajduga@vp.pl", "napastnik");
					baza.insertZawodnik(idSezon,1, "Jaros�aw", "Ulas", "89112304564" ,"Jasie� 211", "11.12.1986", "213344567", "ulas@vp.pl", "pomocnik");
					baza.insertZawodnik(idSezon,1, "Krzysztof", "Migacz", "782323034" ,"Gnojnik 87", "15.04.1993", "888654321", "migacz@vp.pl", "pomocnik");
					baza.insertZawodnik(idSezon,1, "�ukasz", "Robak", "51081104136" ,"Gnojnik 212", "14.01.1992", "663316858", "robak@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Marian", "Wawryka", "87542304136" ,"Brzesko ul. Jagie��y 12", "15.04.1988", "567234123", "wawryka@gmail.com", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Wies�aw", "G�rak", "921112054545" ,"Gnojnik 301", "11.08.1994", "987456321", "gorak0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Konrad", "Sacha", "81081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Tomasz", "Grzyb", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Marcin", "Migacz", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Rafa�", "Lis", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "�ukasz", "Przepi�rka", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					
					baza.insertZawodnik(idSezon,1, "Tomasz", "Boczarski", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Krystian", "Or�owski", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Dominik", "Robak", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					baza.insertZawodnik(idSezon,1, "Piotr", "Krzyszkowski", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					
					
					baza.insertZawodnik(idSezon,1, "Marcin", "Misina", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obro�ca");
					*/
					Intent intent = new Intent(context, Zawodnicy.class);
					startActivity(intent);
				}
				
				
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
	
	

}
