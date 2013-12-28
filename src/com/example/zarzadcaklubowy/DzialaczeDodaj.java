package com.example.zarzadcaklubowy;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class DzialaczeDodaj extends Activity implements OnClickListener{
	private Button btDzialaczeZapisz;
	private Context context;
	private BazaDanych baza;
	private Cursor cursor,cursorAktualnySezon;
	private boolean czyEdycja;
	private int id;
	private EditText etDzialaczImie, etDzialaczNazwisko, etDzialaczPesel, etDzialaczAdres;
	private EditText etDzialaczTelefon, etDzialaczEmail;
	private Spinner spinnerStanowisko;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dzialacze_dodaj);
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
		    	cursor = baza.getDzialacze(id);
		    	if(cursor!=null && cursor.moveToFirst())
		    	{
		    		etDzialaczImie.setText(cursor.getString(cursor.getColumnIndex("dzi_imie")));
		    		etDzialaczNazwisko.setText(cursor.getString(cursor.getColumnIndex("dzi_nazwisko")));
		    		etDzialaczPesel.setText(cursor.getString(cursor.getColumnIndex("dzi_PESEL")));

		    		etDzialaczAdres.setText(cursor.getString(cursor.getColumnIndex("dzi_adres")));
		    		etDzialaczTelefon.setText(cursor.getString(cursor.getColumnIndex("dzi_nrtel")));
		    		etDzialaczEmail.setText(cursor.getString(cursor.getColumnIndex("dzi_email")));
		    	}
		    }
		    
		}
		
		List<String> pozycje= new ArrayList<String>();
		pozycje.add("kierownik");
		pozycje.add("prezes");
		pozycje.add("z-ca prezesa");
		pozycje.add("trener");
		pozycje.add("cz³onek zarz¹du");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_layout,R.id.tvSpinnerDruzyny, pozycje);
		spinnerStanowisko.setAdapter(arrayAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dzialacze_dodaj, menu);
		return true;
	}
	
	private void PrzypiszId()
	{
		etDzialaczImie = (EditText)findViewById(R.id.etDzialaczImie);
		etDzialaczNazwisko = (EditText)findViewById(R.id.etDzialaczNazwisko);
		etDzialaczPesel = (EditText)findViewById(R.id.etDzialaczPesel);
		etDzialaczAdres = (EditText)findViewById(R.id.etDzialaczAdres);
		etDzialaczTelefon = (EditText)findViewById(R.id.etDzialaczTelefon);
		etDzialaczEmail  = (EditText)findViewById(R.id.etDzialaczEmail);
		spinnerStanowisko = (Spinner)findViewById(R.id.spinnerStanowisko);
		btDzialaczeZapisz = (Button)findViewById(R.id.btDzialaczZapisz);
		btDzialaczeZapisz.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btDzialaczZapisz:
			if(czyEdycja)
			{
				baza.updateDzialacz(id,1, etDzialaczImie.getText().toString(), etDzialaczNazwisko.getText().toString(), etDzialaczPesel.getText().toString(),
						etDzialaczAdres.getText().toString(), "00.00.0000", etDzialaczTelefon.getText().toString(),
						etDzialaczEmail.getText().toString(), (String)spinnerStanowisko.getSelectedItem());
				Intent intent = new Intent(context, Dzialacze.class);
				startActivity(intent);
			}
			else
			{
				cursorAktualnySezon = baza.getSezonAktualny();
				if(cursorAktualnySezon != null && cursorAktualnySezon.moveToFirst())
				{
					int idSezon = cursorAktualnySezon.getInt(cursorAktualnySezon.getColumnIndex("_id"));
					baza.insertDzialacz(idSezon, 1, etDzialaczImie.getText().toString(), etDzialaczNazwisko.getText().toString(), etDzialaczPesel.getText().toString(),
							etDzialaczAdres.getText().toString(), "00:00:0000", etDzialaczTelefon.getText().toString(),
							etDzialaczEmail.getText().toString(), (String)spinnerStanowisko.getSelectedItem());
					/*for(int i=0; i<25;i++)
					{
						baza.insertZawodnik(idSezon,1, "Mateusz"+i, "Stanuszek"+i, "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obroñca");

					}*/
					Intent intent = new Intent(context, Dzialacze.class);
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
