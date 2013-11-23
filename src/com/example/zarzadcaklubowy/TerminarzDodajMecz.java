package com.example.zarzadcaklubowy;

import java.util.ArrayList;

import javax.xml.datatype.Duration;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class TerminarzDodajMecz extends Activity implements OnClickListener {

	private Context context;
	private BazaDanych baza;
	private Cursor cursorDruzyny,cursorDruzyny2, cursorAktualnySezon, cursorGospodarz, cursorGosc;
	private Spinner spinnerKolejka, spinnerGospodarz, spinnerGosc;
	private Button btZapisz;
	private DatePicker data;
	private TimePicker czas;
	private SimpleCursorAdapter spinnerAdapter;
	private SimpleCursorAdapter spinnerAdapter2;
	private ArrayAdapter<Integer> spinnerArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminarz_dodaj_mecz);
		PrzypiszId();
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		WczytajDane();
		
		
		
	}
	
	private void WczytajDane()
	{
		cursorDruzyny = baza.getNazwyDruzyn();
		cursorDruzyny2 = baza.getNazwyDruzyn();

		if(cursorDruzyny!=null && cursorDruzyny.moveToFirst() && cursorDruzyny2!=null && cursorDruzyny2.moveToFirst())
		{
			spinnerAdapter = new SimpleCursorAdapter(this,R.layout.spinner_layout,cursorDruzyny,new String[]{"dru_nazwa"},new int[]{R.id.tvSpinnerDruzyny});
			spinnerAdapter2 = new SimpleCursorAdapter(this,R.layout.spinner_layout,cursorDruzyny2,new String[]{"dru_nazwa"},new int[]{R.id.tvSpinnerDruzyny});
			spinnerGospodarz.setAdapter(spinnerAdapter);
			spinnerGosc.setAdapter(spinnerAdapter2);
		}
		
		cursorAktualnySezon = baza.getSezonAktualny();
		
		if(cursorAktualnySezon!=null && cursorAktualnySezon.moveToFirst())
		{
			int liczbaKolejek;
			liczbaKolejek = cursorAktualnySezon.getInt(cursorAktualnySezon.getColumnIndex("sez_liczbakolejek"));
			ArrayList<Integer>kolejki = new ArrayList<Integer>();
			for (int i=1;i<=liczbaKolejek;i++)
			{
				kolejki.add(i);
			}
			
			spinnerArrayAdapter = new ArrayAdapter<Integer>(context, R.layout.spinner_layout,R.id.tvSpinnerDruzyny, kolejki);
			spinnerKolejka.setAdapter(spinnerArrayAdapter);
			
		}
		
	}
	
	private void PrzypiszId()
	{
		spinnerKolejka = (Spinner)findViewById(R.id.spinnerTerminarzKolejka);
		spinnerGospodarz = (Spinner)findViewById(R.id.spinnerTerminarzGodpodarz);
		spinnerGosc = (Spinner)findViewById(R.id.spinnerTerminarzGosc);
		btZapisz = (Button)findViewById(R.id.btTerminarzZapisz);
		data = (DatePicker)findViewById(R.id.dataTerminarz);
		czas = (TimePicker)findViewById(R.id.czasTerminarz);
		btZapisz.setOnClickListener(this);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.terminarz_dodaj_mecz, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btTerminarzZapisz:
			cursorGospodarz = (Cursor)spinnerGospodarz.getSelectedItem();
			cursorGosc = (Cursor)spinnerGosc.getSelectedItem();
			String miesiac,dzien,godzina,minuta;
			if(data.getMonth()<10)
				miesiac = "0"+String.valueOf(data.getMonth()+1);
			else miesiac = String.valueOf(data.getMonth()+1);
			
			if(data.getDayOfMonth()<10)
				dzien = "0"+String.valueOf(data.getDayOfMonth());
			else dzien = String.valueOf(data.getDayOfMonth());
			
			if(czas.getCurrentHour()<10)
				godzina = "0" + String.valueOf(czas.getCurrentHour());
			else godzina = String.valueOf(czas.getCurrentHour());
			
			if(czas.getCurrentMinute()<10)
				minuta = "0" + String.valueOf(czas.getCurrentMinute());
			else minuta = String.valueOf(czas.getCurrentMinute());
				
			
			String d = dzien + "/" + miesiac + "/" + String.valueOf(data.getYear()).substring(2, 4);
			String cz = godzina + ":" + minuta;
			
			if(cursorAktualnySezon!=null && cursorGosc!=null && cursorGospodarz!= null)
				if(baza.insertMecz(cursorAktualnySezon.getInt(cursorAktualnySezon.getColumnIndex("_id")),
						cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("_id")),
						cursorGosc.getInt(cursorGosc.getColumnIndex("_id")),
						(Integer)spinnerKolejka.getSelectedItem(), d, cz)!=-1)
					{
						Toast.makeText(context, "Dodano", Toast.LENGTH_SHORT).show();
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
