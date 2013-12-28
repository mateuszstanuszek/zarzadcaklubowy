package com.example.zarzadcaklubowy;

import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class Wykluczenia extends Activity {
	private Context context;
	private BazaDanych baza;
	private Cursor cursorWykluczenia;
	private ListView lvWykluczenia;
	
	private Button btUsun, btDodaj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wykluczenia);
		lvWykluczenia = (ListView)findViewById(R.id.lvWykluczenia);
		btUsun = (Button)findViewById(R.id.btWykluczeniaUsun);
		btDodaj = (Button)findViewById(R.id.btWykluczeniaDodaj);
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		PobierzDruzyny();
		
		btDodaj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				WyswietlDialogDodaj();
				
			}
		});
		
		btUsun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (Integer i : WykluczeniaCursorAdapter.POZYCJE) {
					Cursor cursor = (Cursor)lvWykluczenia.getItemAtPosition(i);
					if(cursor!=null)
					{
						long UsunID = cursor.getLong(cursor.getColumnIndex("_id"));
						baza.deleteWykluczeniePoID(UsunID);
					}
				}
				
				WykluczeniaCursorAdapter.POZYCJE.clear();
				PobierzDruzyny();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wykluczenia, menu);
		return true;
	}
	
	@Override
    protected void onDestroy() {
		super.onDestroy();
        if(baza != null)
            baza.close();
        
    }
	
	private void WyswietlDialogDodaj()
	{
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.wykluczenia_dodaj_dialog_layout, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		alertDialogBuilder.setView(promptsView);

		final Spinner spinnerZawodnicy = (Spinner) promptsView.findViewById(R.id.spinnerWykluczeniaNoweZawodnicy);
		final EditText etIloscMeczow = (EditText) promptsView
				.findViewById(R.id.etWykluczeniaNoweIleMeczow);

		
		Cursor cursor = baza.getZawodnicy();
		
		if(cursor!=null && cursor.moveToFirst())
		{
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.spinner_lista_zawodnikow, cursor,
					new String[]{"zaw_imie","zaw_nazwisko"}, new int[]{R.id.tvSpinnerImie,R.id.tvSpinnerNazwisko});
			spinnerZawodnicy.setAdapter(adapter);
		}
		

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Zapisz",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	
			    	
			    	Cursor c = (Cursor)spinnerZawodnicy.getSelectedItem();
			    	if(c!=null && c.moveToPosition(c.getPosition()))
			    	{
			    		
			    		long zawID = c.getLong(c.getColumnIndex("_id"));
			    		int ilMeczow = Integer.valueOf(etIloscMeczow.getText().toString());
			    		Cursor cursorAktualnySezon = baza.getSezonAktualny();
						if(cursorAktualnySezon!=null && cursorAktualnySezon.moveToFirst())
							baza.insertWykluczenie(cursorAktualnySezon.getInt(cursorAktualnySezon.getColumnIndex("_id")), -1, zawID, 1, ilMeczow, -2);
			    		
						WykluczeniaCursorAdapter.POZYCJE.clear();
						PobierzDruzyny();
						
			    	}
			    	
			    }
			  })
			.setNegativeButton("Anuluj",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		alertDialogBuilder.setTitle("Dodawanie wykluczenia");
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	public void PobierzDruzyny()
	{
		cursorWykluczenia = baza.getWykluczenia();
		if(cursorWykluczenia!=null && cursorWykluczenia.moveToFirst())
		{
			WykluczeniaCursorAdapter wykluczeniaAdapter = new WykluczeniaCursorAdapter
					(baza, this, R.layout.lv_wykluczenia_layout, cursorWykluczenia, new String[]{}, new int[]{});
			lvWykluczenia.setAdapter(wykluczeniaAdapter);
		}
		
	}
	
	static class WykluczeniaCursorAdapter extends SimpleCursorAdapter
	{
		private Activity context;
		private Cursor cursor, cursorZawodnik;
		private BazaDanych baza;
		public final static ArrayList<Integer> POZYCJE = new ArrayList<Integer>();
		public WykluczeniaCursorAdapter(BazaDanych baza, Activity context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			this.context = context;
			this.cursor = c;
			this.baza = baza;
			
		}
		
		private class ViewHolder {
			TextView tvZawodnik, tvAktywne, tvPoIlu, tvIloscMeczow;
			CheckBox chb;
			LinearLayout llWykluczenie;
	     
	    }
		
        
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder vHolder;
			View rowView = convertView;
			if(rowView == null)
			{
				LayoutInflater layoutInflater = context.getLayoutInflater();
		        rowView =  layoutInflater.inflate(R.layout.lv_wykluczenia_layout, null, true);
		        vHolder = new ViewHolder();
		        vHolder.chb = (CheckBox)rowView.findViewById(R.id.chbWykluczenie);
		        vHolder.tvZawodnik = (TextView)rowView.findViewById(R.id.tvWykluczenieZawodnik);
		        vHolder.tvAktywne = (TextView)rowView.findViewById(R.id.tvWykluczenieAktywne);
		        vHolder.tvPoIlu = (TextView)rowView.findViewById(R.id.tvWykluczeniePoIlu);
		        vHolder.tvIloscMeczow = (TextView)rowView.findViewById(R.id.tvWykluczenieIloscMeczow);
		        vHolder.llWykluczenie = (LinearLayout) rowView.findViewById(R.id.llWykluczenie);
		        
		        
		        rowView.setTag(vHolder);
		        } 
				
				else 
		            vHolder = (ViewHolder) rowView.getTag();
		        
			if(position%2==0)
			{
				vHolder.llWykluczenie.setBackgroundColor(Color.parseColor("#3F3F3F"));
			}
			else
			{
				vHolder.llWykluczenie.setBackgroundColor(Color.parseColor("#232323"));
			}

		        cursor.moveToPosition(position);
		        long zawodnikID = cursor.getLong(cursor.getColumnIndex("wyk_zaw_id"));
		        cursorZawodnik = baza.getZawodnicy(zawodnikID);
		        
		        if(cursorZawodnik!=null && cursorZawodnik.moveToFirst())
		        {
		        	String imie = cursorZawodnik.getString(cursorZawodnik.getColumnIndex("zaw_imie"));
		        	String nazwisko = cursorZawodnik.getString(cursorZawodnik.getColumnIndex("zaw_nazwisko"));
		        	vHolder.tvZawodnik.setText(imie + " " + nazwisko);
		        	
		        }
		        
		        int poIlu = cursor.getInt(cursor.getColumnIndex("wyk_po_ilu"));
		        if(poIlu==-1)
		        {
		        	vHolder.tvPoIlu.setText("czerwona");
		        }
		        else if(poIlu==-2)
		        {
		        	vHolder.tvPoIlu.setText("w³asne");
		        }
		        else
		        {
		        	vHolder.tvPoIlu.setText(poIlu + " x ¿ó³ta");
		        }
		        
		        int aktywne = cursor.getInt(cursor.getColumnIndex("wyk_aktywne"));
		        if(aktywne == 0)
		        {
		        	vHolder.tvAktywne.setText("N");
		        }
		        else
		        {
		        	vHolder.tvAktywne.setText("T");
		        }
		        
		        int ilMeczow = cursor.getInt(cursor.getColumnIndex("wyk_il_meczow"));
		        vHolder.tvIloscMeczow.setText(ilMeczow+"");
		        
		        
		        if(POZYCJE.contains(position))
		        {
		        	vHolder.chb.setChecked(true);
		        }
		        else
		        {
		        	vHolder.chb.setChecked(false);
		        }
				
		        vHolder.chb.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(POZYCJE.contains(position))
						{
							POZYCJE.remove(Integer.valueOf(position));
							
						}
						else
						{
							POZYCJE.add(position);
						}
						
					}
				});

		        
			
				return rowView;
			}
			
			

	}


}


