package com.example.zarzadcaklubowy;

import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class TerminarzWprowadzWyniki extends Activity implements OnClickListener {
	
	private BazaDanych baza;
	private Context context;
	private Cursor cursor,cursorAktualnySezon, cursorGospodarz,cursorGosc,cursorMojaDruzyna;
	private ListView lvWyniki;
	private WynikiCursorAdapter dataAdapter;
	private Button btPobierz, btZapisz;
	private Spinner spinnerKolejki;
	private ArrayAdapter<Integer> spinnerArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminarz_wprowadz_wyniki);
		PrzypiszId();
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		WczytajDane();
		
		
	}
	
	private void PobierzWyniki(int kolejka)
	{
		String [] kolumny = new String[]{"mecz_dru_id_gospodarz","mecz_bramkigosp",
				"mecz_bramkigosc","mecz_dru_id_gosc"};
		int [] to = new int []{R.id.tvWynikiLayoutGospodarz,R.id.etWynikiLayoutBrGosp,
				R.id.tvWynikiLayoutGosc,R.id.tvWynikiLayoutGosc};
		cursorMojaDruzyna = baza.getDaneKlubu();
		if(cursorMojaDruzyna!=null && cursorMojaDruzyna.moveToFirst())
			cursor = baza.getMecze(kolejka,cursorMojaDruzyna.getLong(cursorMojaDruzyna.getColumnIndex("dan_dru_id")));
		else
			cursor = baza.getMecze(kolejka,-1);
		dataAdapter = new WynikiCursorAdapter(baza, this, R.layout.lv_wyniki_layout, cursor, kolumny, to);
		lvWyniki.setAdapter(dataAdapter);
	}
	
	private void WczytajDane()
	{
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
			spinnerKolejki.setAdapter(spinnerArrayAdapter);
			
		}
	}
	
	private void PrzypiszId()
	{
		lvWyniki = (ListView)findViewById(R.id.lvWyniki);
		btPobierz = (Button)findViewById(R.id.btTerminarzPobierz);
		btZapisz = (Button)findViewById(R.id.btTerminarzWprowadzZapisz);
		spinnerKolejki = (Spinner)findViewById(R.id.spinnerTerminarzKolejka);
		btPobierz.setOnClickListener(this);
		btZapisz.setOnClickListener(this);
		lvWyniki.setItemsCanFocus(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.terminarz_wprowadz_wyniki, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btTerminarzPobierz:
			int kolejka = (Integer)spinnerKolejki.getSelectedItem();
			PobierzWyniki(kolejka);
			
			break;
		case R.id.btTerminarzWprowadzZapisz:
			lvWyniki.requestFocus();
			Cursor c;
			for (int i=0;i<lvWyniki.getCount();i++)
			{
				c = (Cursor)lvWyniki.getItemAtPosition(i);
				String bramkiGosp = dataAdapter.bramki.get(i).bramkiGospodarze;
				String bramkiGosc = dataAdapter.bramki.get(i).bramkiGoscie;
				int id = c.getInt(c.getColumnIndex("_id"));
				int idGospodarz, idGosc;
				idGospodarz = c.getInt(c.getColumnIndex("mecz_dru_id_gospodarz"));
				idGosc = c.getInt(c.getColumnIndex("mecz_dru_id_gosc"));
				cursorGospodarz = baza.getNazwyDruzyn(idGospodarz);
				cursorGosc = baza.getNazwyDruzyn(idGosc);
				int gospodarzMecze, gospodarzPunkty, gospodarzZwyciestwa, gospodarzRemisy, gospodarzPorazki,gospodarzZdobyte, gospodarzStracone,
					goscMecze, goscPunkty,goscZwyciestwa, goscRemisy, goscPorazki,goscZdobyte, goscStracone, brGosp,brGosc;
				
				if(!bramkiGosp.equals("") && !bramkiGosc.equals(""))
				{
					brGosp = Integer.valueOf(bramkiGosp);
					brGosc = Integer.valueOf(bramkiGosc);
					if(cursorGospodarz!=null && cursorGospodarz.moveToFirst())
					{
						gospodarzMecze = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_mecze")) + 1;
						if(brGosp>brGosc)
						{
							gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa")) + 1;
							gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy"));
							gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki"));
							gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty")) + 3;
						}
						else if (brGosp==brGosc)
						{
							gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy")) + 1;
							gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa"));
							gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki"));
							gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty")) + 1;
						}
						else
						{
							gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki")) + 1;
							gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy"));
							gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa"));
							gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty"));
						}
						
						gospodarzZdobyte = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_bramki_zdobyte")) + brGosp;
						gospodarzStracone = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_bramki_stracone")) + brGosc;
						
						baza.updateDruzyna(idGospodarz,gospodarzMecze, gospodarzPunkty, gospodarzZwyciestwa, gospodarzRemisy, gospodarzPorazki,gospodarzZdobyte, gospodarzStracone);
						
					}
					
					if(cursorGosc!=null && cursorGosc.moveToFirst())
					{
						goscMecze = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_mecze")) + 1;
						if(brGosc>brGosp)
						{
							goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa")) + 1;
							goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy"));
							goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki"));
							goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty")) + 3;
						}
						else if (brGosp==brGosc)
						{
							goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy")) + 1;
							goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa"));
							goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki"));
							goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty")) + 1;
						}
						else
						{
							goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki")) + 1;
							goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy"));
							goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa"));
							goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty"));
						}
						
						goscZdobyte = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_bramki_zdobyte")) + brGosc;
						goscStracone = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_bramki_stracone")) + brGosp;
						
						baza.updateDruzyna(idGosc, goscMecze, goscPunkty,goscZwyciestwa, goscRemisy, goscPorazki,goscZdobyte, goscStracone);
						
					}
				}
			
				
				baza.updateMecz(id, bramkiGosp, bramkiGosc);
			}
			Intent intent = new Intent(context, Terminarz.class);
			startActivity(intent);
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

class WynikiCursorAdapter extends SimpleCursorAdapter
{
	private Activity context;
	private Cursor cursor, cursorNazwaDruzyny;
	private BazaDanych baza;
	public ArrayList<Bramki> bramki;
	public WynikiCursorAdapter(BazaDanych baza, Activity context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
		this.baza = baza;
		bramki = new ArrayList<Bramki>();
		if(cursor!=null)
		{
			for (int i=0;i<cursor.getCount();i++)
			{
				cursor.moveToPosition(i);
				Bramki br = new Bramki();
				br.bramkiGospodarze = cursor.getString(cursor.getColumnIndex("mecz_bramkigosp"));
				br.bramkiGoscie = cursor.getString(cursor.getColumnIndex("mecz_bramkigosc"));
				bramki.add(br);
			}
		}
		// TODO Auto-generated constructor stub
	}
	
	static class ViewHolder {
        public TextView tvGospodarz,tvGosc;
        public EditText etBramkiGospodarz, etBramkiGosc;
     
    }
	
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder;
		View rowView = convertView;
		if(rowView == null)
		{
			LayoutInflater layoutInflater = context.getLayoutInflater();
	        rowView =  layoutInflater.inflate(R.layout.lv_wyniki_layout, null, true);
	        vHolder = new ViewHolder();
	        vHolder.tvGospodarz = (TextView)rowView.findViewById(R.id.tvWynikiLayoutGospodarz);
	        vHolder.tvGosc = (TextView)rowView.findViewById(R.id.tvWynikiLayoutGosc);
	        vHolder.etBramkiGosc = (EditText)rowView.findViewById(R.id.etWynikiLayoutBrGoscie);
	        vHolder.etBramkiGospodarz = (EditText)rowView.findViewById(R.id.etWynikiLayoutBrGosp);
	        		
	        
	        rowView.setTag(vHolder);
		
		}
		else 
            vHolder = (ViewHolder) rowView.getTag();
		
		if(cursor!=null && cursor.moveToPosition(position))
		{
			 int idGospodarz = cursor.getInt(cursor.getColumnIndex("mecz_dru_id_gospodarz"));
		        cursorNazwaDruzyny = baza.getNazwyDruzyn(idGospodarz);
		        if(cursorNazwaDruzyny!=null && cursorNazwaDruzyny.moveToFirst())
		        	vHolder.tvGospodarz.setText(cursorNazwaDruzyny.getString(cursorNazwaDruzyny.getColumnIndex("dru_nazwa")));
		        else
		        	vHolder.tvGospodarz.setText("---");
		        
		        
		        int idGosc = cursor.getInt(cursor.getColumnIndex("mecz_dru_id_gosc"));
		        cursorNazwaDruzyny = baza.getNazwyDruzyn(idGosc);
		        if(cursorNazwaDruzyny!=null && cursorNazwaDruzyny.moveToFirst())
		        	vHolder.tvGosc.setText(cursorNazwaDruzyny.getString(cursorNazwaDruzyny.getColumnIndex("dru_nazwa")));

		        else
		        	vHolder.tvGosc.setText("---");
		        
		        vHolder.etBramkiGospodarz.setText(bramki.get(position).bramkiGospodarze);
		        vHolder.etBramkiGospodarz.setId(position);
		        
		        
		        vHolder.etBramkiGosc.setText((bramki.get(position).bramkiGoscie));
		        vHolder.etBramkiGosc.setId(position);
		        
		        vHolder.etBramkiGospodarz.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus){
	                        final int position = v.getId();
	                        final EditText et = (EditText) v;
	                        bramki.get(position).bramkiGospodarze = et.getText().toString();
	                    }
						
					}
				});
		        
		        vHolder.etBramkiGosc.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus){
	                        final int position = v.getId();
	                        final EditText et = (EditText) v;
	                        bramki.get(position).bramkiGoscie = et.getText().toString();
	                    }
						
					}
				});
		}
	
		return rowView;
	}
	
	class Bramki
	{
		public String bramkiGospodarze;
		public String bramkiGoscie;
	}
	
}

